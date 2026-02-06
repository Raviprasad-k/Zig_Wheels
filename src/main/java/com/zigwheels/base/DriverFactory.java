package com.zigwheels.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;
import java.net.MalformedURLException;

public class DriverFactory {
	private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

	public static synchronized void initDriver(String browser, boolean headless, String executionMode) {
		WebDriver driver = null;
		String browserType = browser.toLowerCase();
		String gridUrl = "http://localhost:4444/wd/hub"; // Change this to your Grid IP

		try {
			if (browserType.equals("chrome")) {
				ChromeOptions co = new ChromeOptions();
				co.addArguments("--disable-notifications", "--start-maximized");
				if (headless) co.addArguments("--headless=new");

				// Simple Switch: Remote vs Local
				if (executionMode.equalsIgnoreCase("remote")) {
					driver = new RemoteWebDriver(new URL(gridUrl), co);
					driver.manage().window().maximize();
				} else {
					driver = new ChromeDriver(co);
				}

			} else if (browserType.equals("edge")) {
				EdgeOptions eo = new EdgeOptions();
				eo.addArguments("--disable-notifications", "--start-maximized");
				if (headless) eo.addArguments("--headless=new");

				if (executionMode.equalsIgnoreCase("remote")) {
					driver = new RemoteWebDriver(new URL(gridUrl), eo);
					driver.manage().window().maximize();
				} else {
					driver = new EdgeDriver(eo);
				}
			}
		} catch (MalformedURLException e) {
			System.out.println("Invalid Grid URL provided.");
		}

		tlDriver.set(driver);
	}

	public static synchronized WebDriver getDriver() {
		return tlDriver.get();
	}

	public static synchronized void quitDriver() {
		if (getDriver() != null) {
			getDriver().quit();
			tlDriver.remove();
		}
	}
}