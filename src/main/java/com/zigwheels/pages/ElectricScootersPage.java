package com.zigwheels.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.ArrayList;
import java.util.List;

public class ElectricScootersPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ElectricScootersPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[contains(text(),'Ola Electric')]")
    private WebElement olaFilter;
    
    public void filterOLA() throws InterruptedException {
        // 1. Wait for element to exist in the DOM (Presence is safer for Jenkins)
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[contains(text(),'Ola Electric')] | //a[@title='Ola Electric Scooters']")));
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        // 2. Scroll to center to avoid the sticky header overlap
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    public List<List<String>> scrapeModels() {
        List<List<String>> rows = new ArrayList<>();
        
        // Wait for results to actually appear
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h3[contains(@class,'lnk-hvr')]")));
        } catch (Exception ignored) {}

        List<WebElement> items = driver.findElements(By.xpath("//h3[contains(@class,'lnk-hvr')]"));
        List<WebElement> prices = driver.findElements(By.xpath("//div[@class='clr-bl']"));
        
        int idx = 1;
        for (int i = 0; i < items.size(); i++) {
            try {
                String name = items.get(i).getText().trim();
                String price = (i < prices.size()) ? prices.get(i).getText().trim() : "NA";
                if (!name.isEmpty()) rows.add(List.of(String.valueOf(idx++), name, price));
            } catch (Exception ignored) {}
        }
        return rows;
    }
}