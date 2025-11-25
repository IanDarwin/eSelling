package com.darwinsys.eselling.listing;

import com.darwinsys.eselling.model.Item;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import jakarta.enterprise.context.ApplicationScoped;

/// Export one item to Kijiji.
/// @author Origially by Google Gemini.
///
@ApplicationScoped
public class KijijiMarket implements Market<Item> {

    static String userHome = System.getProperty("user.home");

    @Override
    public String getFileLocation() {
        return "";
    }

    @Override
    public String getUploadURL() {
        return "";
    }

    @Override
    public MarketName getMarketName() {
        return MarketName.Kijiji;
    }

    @Override
    public void startStream(String location) {

    }

    @Override
    public ListResponse closeStream() {
        return null;
    }

    @Override
    public String getPostMessage() {
        return "Review the browser screen and press Submit";
    }

    @Override
    public ListResponse list(Item item) {

        // Setup ChromeDriver automatically
        WebDriverManager.chromedriver().setup();

        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        // --- PERSISTENT SESSION SETTINGS ---
        // This creates a specific folder to save cookies/login so you don't have to login every time.
        // It is stored in your eselling folder to avoid conflicts with your main open browser.
        File profileDir = new File(userHome, ".eselling/chrome-profile");
        options.addArguments("user-data-dir=" + profileDir.getAbsolutePath());
        // Keep browser open after script finishes so we can click "Post"
        options.setExperimentalOption("detach", true);

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 2. Navigate to URL
            String url = "https://www.kijiji.ca/p-post-ad.html?categoryId=" + item.getCategory().kijijiCategory();
            System.out.println("Navigating to: " + url);
            driver.get(url);

            // 3. MANUAL LOGIN INTERVENTION
            // Kijiji has strict bot detection for login. It is safest to let the human log in.
            System.out.println("----------------------------------------------------------------");
            System.out.println("ACTION REQUIRED: Check the opened Chrome window.");
            System.out.println("1. If redirected to login, please Log In manually.");
            System.out.println("2. Wait until you see the 'Post Ad' form.");
            System.out.println("3. Press ENTER in this console to continue filling data...");
            System.out.println("----------------------------------------------------------------");
            new Scanner(System.in).nextLine();

            System.out.println("Resuming... populating form.");

            // 4. Fill Basic Text Fields
            // We use explicit waits to ensure the form is loaded
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("postAdForm.title")))
                .sendKeys(item.getName());

            driver.findElement(By.name("postAdForm.description")).sendKeys(item.getDescription());

            KijijiMarket.setTrickyInput(driver, "postAdForm.tags", item.getTags());

            // 5. Handle Price (Radio button + Input)
            try {
                // Click "Fixed Price" radio button. ID might vary, usually "priceType1" for fixed
                WebElement fixedPriceRadio = driver.findElement(By.id("priceType1"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", fixedPriceRadio);
                
                driver.findElement(By.name("postAdForm.priceAmount")).sendKeys(item.getAskingPrice().toString());
            } catch (Exception e) {
                System.out.println("Could not set price - category might be 'Please Contact'.");
            }

            // 6. Handle Location - not needed as KJ defaults it to my address
            // KijijiMarket.setTrickyInput(driver, "postAdForm.postalCode", item.postalCode);
            //WebElement postalInput = driver.findElement(By.name("postAdForm.postalCode"));
            //postalInput.clear();
            // postalInput.sendKeys(item.postalCode);

            // 7. Handle Attributes (Condition, For Sale By, etc)
            // Kijiji uses radio buttons/hidden inputs for these. We try to find by value.
            try {
                // Try to click the element that matches the value (e.g., "usedgood", "ownr")
                // Using XPath to find input with value=X or the label associated with it
                String xpath = String.format("//input[@value='%s']", item.getCondition());
                WebElement conditionBtn = driver.findElement(By.xpath(xpath));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", conditionBtn);
            } catch (Exception e) {
                System.out.println("Attribute specific to this category not found (optional).");
            }
            
            // Always select "Owner" if available
            try {
                WebElement ownerBtn = driver.findElement(By.xpath("//input[@value='ownr']"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ownerBtn);
            } catch (Exception ignored) {}


            // 8. IMAGE UPLOADING
            uploadImages(driver, item.getPhotosDir());

            System.out.println("Form filled. Please review and click 'Post Your Ad' manually.");

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Note: driver.quit() is intentionally omitted to keep browser open

        return new ListResponse();
    }

    /// Get images and upload them.
    /// XXX Rewrite using Files and stream?
    private static void uploadImages(WebDriver driver, String shortName) {
        // Construct path: User Home + eselling + shortName
        File imageDir = new File(userHome, "eselling/" + shortName);

        if (!imageDir.exists() || !imageDir.isDirectory()) {
            System.out.println("Image directory not found: " + imageDir.getAbsolutePath());
            return;
        }

        File[] files = imageDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".jpg") || 
            name.toLowerCase().endsWith(".png") || 
            name.toLowerCase().endsWith(".jpeg")
        );

        if (files == null || files.length == 0) {
            System.out.println("No images found in " + imageDir.getAbsolutePath());
            return;
        }

        try {
            // Find the file input element. Kijiji hides it, so we need to be careful.
            // Usually it's an <input type="file"> inside the upload section.
            WebElement fileInput = driver.findElement(By.xpath("//input[@type='file']"));

            System.out.println("Uploading " + files.length + " images...");

            // Prepare absolute paths joined by \n (standard trick for multiple file uploads)
            // Note: Kijiji sometimes prefers one at a time.
            for (File img : files) {
                // Send the absolute path of the image to the file input
                // We do not CLICK the button, we send keys to the hidden input
                fileInput.sendKeys(img.getAbsolutePath());
                
                // Small sleep to ensure the UI processes the image before sending the next
                // (Kijiji uploads happen via AJAX)
                Thread.sleep(1000); 
            }
            
        } catch (Exception e) {
            System.out.println("Image upload failed: " + e.getMessage());
        }
    }

    /**
     * Attempts to set the value of a potentially tricky input field and forces validation.
     * Use this in place of the simple .sendKeys() calls.
     */
    private static void setTrickyInput(WebDriver driver, String elementName, String value) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            // Try finding the element
            WebElement inputElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.name(elementName)));

            try {
                // Strategy A: Standard Selenium interaction
                inputElement.clear();
                inputElement.sendKeys(value);
                inputElement.sendKeys(Keys.TAB);

                if (elementName.contains("postalCode")) Thread.sleep(500);
                System.out.println("Set " + elementName + " via standard input.");

            } catch (Exception e) {
                // Strategy B: JavaScript Injection (The "Nuclear Option")
                // If Selenium says "element not Interactable", we force the value via JS.
                System.out.println("Standard input failed for " + elementName + ". Trying JS injection...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", inputElement, value);

                // Fire a change event so Kijiji knows we changed it
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", inputElement
                );
            }

        } catch (Exception e) {
            System.err.println("Failed to set " + elementName + " even with JS fallback.");
        }
    }
}
