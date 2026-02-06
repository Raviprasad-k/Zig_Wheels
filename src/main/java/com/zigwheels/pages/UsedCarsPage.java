package com.zigwheels.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.ArrayList;
import java.util.List;

public class UsedCarsPage {
	private final WebDriver driver;
	private final WebDriverWait wait;

	// Receive both driver and wait from BaseTest
	public UsedCarsPage(WebDriver driver, WebDriverWait wait) {
		this.driver = driver;
		this.wait = wait;
		PageFactory.initElements(driver, this);
	}

	// --- POM Elements using @FindBy ---

	@FindBy(id = "gs_input5")
	private WebElement cityInput;

	@FindBy(xpath = "//a[@title='Chennai']")
	private WebElement chennaiSuggestion;

	@FindBy(xpath = "//button[contains(.,'Search') or contains(.,'Go') or @type='submit']")
	private WebElement searchBtn;

	@FindBy(id = "websortbyusedcar")
	private WebElement sortDropdown;

	@FindBy(xpath = "//span[contains(@class,'contactSellerbtn')][1]")
	private WebElement contactSellerBtn;

	@FindBy(xpath = "//*[@class='mobile']")
	private WebElement mobileInput;

	@FindBy(xpath = "//span[@class='error ']")
	private WebElement errorMessage;

	// Locators for scraping (stored as By to prevent stale errors during loops)
	private By carNamesLoc = By.xpath("//a[contains(@class, 'zw-sr-headingPadding')]");
	private By carPricesLoc = By.xpath("//span[@class='zw-cmn-price n pull-left mt-3']");
	private By fuelTypesLoc = By.xpath("//li[@itemprop='fuelType']");

	// --- Action Methods ---
	public void selectCityChennaiIfModal() {
		try {
			if (cityInput.isDisplayed()) {
				cityInput.clear();
				cityInput.sendKeys("Chennai");
				Thread.sleep(500);
				try {
					chennaiSuggestion.click();
				} catch (Exception e) {
					cityInput.sendKeys(Keys.ENTER);
				}
				try {
					searchBtn.click();
				} catch (Exception ignored) {
				}
			}
		} catch (Exception ignored) {
		}
	}

	public void sortByPriceHighToLow() {
		try {
			// Wait until dropdown is visible to avoid stale error
			wait.until(ExpectedConditions.visibilityOf(sortDropdown));
			Select select = new Select(sortDropdown);
			select.selectByVisibleText("Price : High to Low");
		} catch (Exception e) {
			System.out.println("Could not sort: " + e.getMessage());
		}
	}

	public List<List<String>> scrapeUsedCars() {
		List<List<String>> rows = new ArrayList<>();

		// We find the fresh list inside the loop to avoid
		// StaleElementReferenceException
		for (int i = 0; i < 6; i++) {
			try {
				String name = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(carNamesLoc)).get(i)
						.getText();
				String price = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(carPricesLoc)).get(i)
						.getText();
				String fuel = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(fuelTypesLoc)).get(i)
						.getText();

				rows.add(List.of(String.valueOf(i + 1), name, price, fuel));
			} catch (Exception e) {
				// If the page jitters, try again once
				i--;
			}
		}
		return rows;
	}

	public String attemptViewSellerAndGetError(String phone) {
	    try {
	        wait.until(ExpectedConditions.elementToBeClickable(contactSellerBtn));
	        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", contactSellerBtn);

	        wait.until(ExpectedConditions.visibilityOf(mobileInput)).sendKeys(phone);

	        try {
	            return wait.until(ExpectedConditions.visibilityOf(errorMessage)).getText();
	        } catch (TimeoutException e) {
	            return "No explicit error message found (modal likely blocked invalid input).";
	        }
	    } catch (Exception e) {
	        return "Could not trigger seller details modal: " + e.getMessage();
	    }
	}

}