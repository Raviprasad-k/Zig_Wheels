package com.zigwheels.pages;

import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.zigwheels.utils.ConfigReader;

public class ProfilePage {
	private final WebDriver driver;
	private final WebDriverWait wait;

	public ProfilePage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Increased to 15 for Grid stability
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//div[@id='des_lIcon']")
	private WebElement profileIcon;

	@FindBy(xpath = "//div[contains(@class,'googleSignIn')]")
	private WebElement googleBtn;

	public void openProfile() {
		wait.until(ExpectedConditions.elementToBeClickable(profileIcon)).click();
	}

	public String tryGoogleLoginInvalid() {
		String originalWindow = driver.getWindowHandle();
		
		// 1. VISIBILITY & CLICK LOGIC
		try {
			// Ensure button is visible before clicking
			wait.until(ExpectedConditions.visibilityOf(googleBtn));
			googleBtn.click();
		} catch (Exception e) {
			// If standard click fails on Grid, use JavaScript 'Force' Click
			System.out.println("Standard click failed, attempting JS click...");
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", googleBtn);
		}

		// 2. STABLE WINDOW SWITCHING
		// Replaces strict 'numberOfWindowsToBe' with a more flexible check for Grid
		try {
			wait.until(d -> d.getWindowHandles().size() > 1);
			
			Set<String> allWindows = driver.getWindowHandles();
			for (String handle : allWindows) {
				if (!handle.equals(originalWindow)) {
					driver.switchTo().window(handle);
					break;
				}
			}
		} catch (Exception e) {
			return "FAIL: Google popup did not appear within timeout.";
		}

		// 3. INTERACT WITH POPUP
		try {
			// Wait for email input
			WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='email']")));
			emailInput.sendKeys(ConfigReader.get("email"), Keys.ENTER);

			// 4. CAPTURE ERROR MESSAGE
			// Using a more generic xpath to handle Google's dynamic UI updates
			WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//div[contains(@class,'Ekjuhf') or contains(@class,'Jj6Lae')]")));
			
			return errorMessage.getText().trim();

		} catch (Exception e) {
			return "Google login error not captured: " + e.getMessage();
		} finally {
			// 5. CLEANUP: Close any popups and return to main window
			for (String handle : driver.getWindowHandles()) {
				if (!handle.equals(originalWindow)) {
					driver.switchTo().window(handle).close();
				}
			}
			driver.switchTo().window(originalWindow);
		}
	}
}