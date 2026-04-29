import java.io.*;
import java.sql.*;

void main() throws Exception {
	Class.forName("org.postgresql.Driver");
	Connection conn = DriverManager.getConnection("jdbc:postgresql:eselling");
	var rs = conn.prepareStatement("select id,urls from item").executeQuery();
	var insert = conn.prepareStatement("insert into item_urls(item_id, url) values(?, ?)");
	while (rs.next()){
		long id = rs.getInt(1);
		String urls = rs.getString(2);
		urls = urls.substring(1, urls.length() - 1).replaceAll("\"", "");
		// System.out.printf("id %d, urls %s\n", id, urls);
		String[] list = urls.split(",");
			for (String u : list) {
				if (!u.isEmpty()) {
					insert.setLong(1, id);
					insert.setString(2, u);
					System.out.println(insert);
					insert.executeUpdate();
				}
			}
	}
}
