package com.example.urlshortener;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

public class Main {

	public static void main(String[] args) throws Exception {
		final int port = 8080;

		final Tomcat tomcat = new Tomcat();
		tomcat.setPort(port);
		tomcat.getConnector();

		final Context context = tomcat.addContext("", new File(".").getAbsolutePath());

		Tomcat.addServlet(context, "urlShortener", new UrlShortenerServlet());
		context.addServletMappingDecoded("/*", "urlShortener");

		tomcat.start();
		System.out.println("Server started on http://localhost:" + port);
		tomcat.getServer().await();
	}

}
