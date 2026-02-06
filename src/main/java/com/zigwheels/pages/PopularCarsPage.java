package com.zigwheels.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PopularCarsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public PopularCarsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15)); 
        PageFactory.initElements(driver, this);
    }

    public List<List<String>> scrapeNamesAndPrices() {
        List<List<String>> rows = new ArrayList<>();
        By carCardsLocator = By.xpath("//li[contains(@class,'modelItem')]");

        try {
            // --- NEW VISIBILITY CONDITION ---
            // Wait specifically for the first element to be visible on the screen
            WebElement checkVisibility = wait.until(ExpectedConditions.visibilityOfElementLocated(carCardsLocator));
            
            if (!checkVisibility.isDisplayed()) {
                System.out.println("Condition Failed: Cars are not visible on the page.");
                return rows; // Returns empty list, so nothing is written to Excel
            }
            // --------------------------------

            // SMART WAIT: Ensure text is actually loaded inside the elements
            wait.until(d -> {
                List<WebElement> elements = d.findElements(carCardsLocator);
                return !elements.isEmpty() && elements.get(0).getText().length() > 0;
            });

            // Scroll to the first card to trigger lazy-loading of images/prices
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", checkVisibility);

        } catch (TimeoutException e) {
            System.out.println("Timed out: Car cards did not appear or were not visible.");
            return rows; 
        }

        // Scrape the items
        List<WebElement> items = driver.findElements(carCardsLocator);

        // Fallback for different page layouts
        if (items.isEmpty()) {
            items = driver.findElements(By.xpath("//a[contains(@href,'/new-cars/')]/ancestor::*[self::li or self::div][1]"));
        }

        int idx = 1;
        Set<String> seenNames = new HashSet<>();

        for (WebElement it : items) {
            try {
                // Name lookup
                WebElement nameEl = it.findElement(By.xpath(
                        ".//a[contains(@class,'ht-name') or contains(@class,'lnk-hvr') or contains(@href,'/new-cars/')]"));
                String name = nameEl.getText().trim();

                if (name.isEmpty() || seenNames.contains(name)) continue;

                // Price lookup
                String price = "NA";
                try {
                    WebElement priceEl = it.findElement(By.xpath(
                            ".//span[contains(normalize-space(@title),'Price') or contains(normalize-space(.),'Lakh') or contains(normalize-space(@class),'price')]"));
                    price = priceEl.getText().trim();
                } catch (NoSuchElementException ignore) {}

                rows.add(List.of(String.valueOf(idx++), name, price));
                seenNames.add(name);

            } catch (StaleElementReferenceException e) {
                continue; // Skip if element refreshed
            } catch (NoSuchElementException ignore) {}
        }

        return rows;
    }
}



//package com.zigwheels.pages;
//
//import org.openqa.selenium.*;
//import org.openqa.selenium.support.PageFactory;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class PopularCarsPage {
//    private final WebDriver driver;
//    private final WebDriverWait wait;
//
//    public PopularCarsPage(WebDriver driver) {
//        this.driver = driver;
//        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Increased for Edge stability
//        PageFactory.initElements(driver, this);
//    }
//
//    public List<List<String>> scrapeNamesAndPrices() {
//        List<List<String>> rows = new ArrayList<>();
//        
//        // 1. Define locators
//        By carCardsLocator = By.xpath("//li[contains(@class,'modelItem')]");
//        
//        try {
//            // 2. SMART WAIT: Wait until at least one car name link is visible and has text
//            // This replaces Thread.sleep by polling the browser until the data actually exists
//            wait.until(driver -> {
//                List<WebElement> elements = driver.findElements(carCardsLocator);
//                return !elements.isEmpty() && elements.get(0).getText().length() > 0;
//            });
//
//            // 3. Scroll to the container to ensure all dynamic elements load
//            WebElement firstCard = driver.findElement(carCardsLocator);
//            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", firstCard);
//
//        } catch (TimeoutException e) {
//            // If the wait fails, we try one last time to find the items directly before giving up
//            System.out.println("Timed out waiting for car cards to load text.");
//        }
//
//        // 4. Scrape the items
//        List<WebElement> items = driver.findElements(carCardsLocator);
//
//        // Fallback for different page layouts
//        if (items.isEmpty()) {
//            items = driver.findElements(By.xpath("//a[contains(@href,'/new-cars/')]/ancestor::*[self::li or self::div][1]"));
//        }
//
//        int idx = 1;
//        Set<String> seenNames = new HashSet<>();
//
//        for (WebElement it : items) {
//            try {
//                // Name lookup
//                WebElement nameEl = it.findElement(By.xpath(
//                        ".//a[contains(@class,'ht-name') or contains(@class,'lnk-hvr') or contains(@href,'/new-cars/')]"));
//                String name = nameEl.getText().trim();
//
//                if (name.isEmpty() || seenNames.contains(name)) continue;
//
//                // Price lookup
//                String price = "NA";
//                try {
//                    WebElement priceEl = it.findElement(By.xpath(
//                            ".//span[contains(normalize-space(@title),'Price') or contains(normalize-space(.),'Lakh') or contains(normalize-space(@class),'price')]"));
//                    price = priceEl.getText().trim();
//                } catch (NoSuchElementException ignore) {}
//
//                rows.add(List.of(String.valueOf(idx++), name, price));
//                seenNames.add(name);
//
//            } catch (StaleElementReferenceException e) {
//                // If elements shift, skip this specific row safely
//                continue;
//            } catch (NoSuchElementException ignore) {}
//        }
//
//        return rows;
//    }
//}

//package com.zigwheels.pages;


//
//import org.openqa.selenium.*;
//import org.openqa.selenium.support.PageFactory;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class PopularCarsPage {
//	private final WebDriver driver;
//
//	public PopularCarsPage(WebDriver driver) {
//		this.driver = driver;
//		PageFactory.initElements(driver, this);
//	}
//
//	public List<List<String>> scrapeNamesAndPrices() {
//		List<List<String>> rows = new ArrayList<>();
//
//		// 1) Try primary card containers
//		List<WebElement> items = driver.findElements(By.xpath("//li[contains(@class,'modelItem')]"));
//
//		// 2) Fallback: broader container if primary not found
//		if (items.isEmpty()) {
//			items = driver
//					.findElements(By.xpath("//a[contains(@href,'/new-cars/')]/ancestor::*[self::li or self::div][1]"));
//		}
//
//		int idx = 1;
//		Set<String> seenNames = new HashSet<>();
//
//		for (WebElement it : items) {
//			try {
//				// RELATIVE lookup: note the leading dot in .//
//				// Name: be tolerant with classes and pick the first link that looks like the
//				// model name
//				WebElement nameEl = it.findElement(By.xpath(
//						".//a[contains(@class,'ht-name') or contains(@class,'lnk-hvr') or contains(@href,'/new-cars/')]"));
//				String name = nameEl.getText().trim();
//
//				if (name.isEmpty() || seenNames.contains(name)) {
//					continue; // skip blanks and duplicates
//				}
//
//				String price = "NA";
//				try {
//					// Price: normalize title (avoid leading/trailing spaces)
//					WebElement priceEl = it.findElement(By.xpath(
//							".//span[normalize-space(@title)='Ex-Showroom Price' or contains(normalize-space(.),'Ex-Showroom Price') or contains(normalize-space(@class),'price')]"));
//					String priceText = priceEl.getText().trim();
//					if (!priceText.isEmpty()) {
//						price = priceText;
//					}
//				} catch (NoSuchElementException ignore) {
//					// Leave price as "NA"
//				}
//
//				rows.add(List.of(String.valueOf(idx++), name, price));
//				seenNames.add(name);
//
//			} catch (NoSuchElementException ignore) {
//				// This item doesn't have a name in the expected structure; skip
//			} catch (StaleElementReferenceException sere) {
//				// The page updated; skip this card safely
//			} catch (Exception e) {
//               .debug("Item parse error: {}", e.getMessage());
//			}
//		}
//
//		return rows;
//	}
//
//}