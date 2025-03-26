package com.darwinsys.eselling.base;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

@Theme("starter-theme")
@PWA(name = "eSales", shortName = "eSales")
public class AppConfig implements AppShellConfigurator {
}
