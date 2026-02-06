package com.zigwheels.base;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import com.zigwheels.utils.ConfigReader;
import com.zigwheels.utils.ExcelUtils;
import com.zigwheels.utils.ScreenshotUtils;

public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected ExcelUtils excel;
    protected final String artifactsDir = "artifacts";
    protected String screenshotsDir = "screenshots";
    protected String browserName;

    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String RUN_STAMP = LocalDateTime.now().format(TS_FORMAT);

    @Parameters({"browser"})
    @BeforeClass(alwaysRun = true) 
    public void setUp(@Optional("chrome") String browser) {
        this.browserName = browser;
        
        // 1. READ CONFIGS: Get execution mode and headless status from properties
        String executionMode = ConfigReader.get("execution", "local");
        boolean headless = Boolean.parseBoolean(ConfigReader.get("headless", "false"));
        
        // 2. INITIALIZE DRIVER: Using our updated Factory
        DriverFactory.initDriver(browser, headless, executionMode); 
        driver = DriverFactory.getDriver();

        // 3. TIMEOUTS: Standard page load and explicit waits
        int pageLoadTimeout = Integer.parseInt(ConfigReader.get("pageLoadTimeout", "50"));
        int explicitWait = Integer.parseInt(ConfigReader.get("explicitWait", "20"));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));

        // 4. DIRECTORIES: Setup screenshots and artifacts
        new File(artifactsDir).mkdirs();
        screenshotsDir = "screenshots" + File.separator + RUN_STAMP + "_" + browserName;
        new File(screenshotsDir).mkdirs();

        // 5. EXCEL: Initialize report file
        String excelPath = artifactsDir + File.separator + "zigwheels_report_" + browserName + "_" + RUN_STAMP + ".xlsx";
        excel = new ExcelUtils(excelPath);

        // 6. LAUNCH: Go to base URL
        driver.get(ConfigReader.get("baseUrl"));
        logger.info("Browser launched in {} mode. URL: {}", executionMode, ConfigReader.get("baseUrl"));
    }

    @AfterMethod(alwaysRun = true)
    public void resetPage() {
        try {
            driver.navigate().to(ConfigReader.get("baseUrl"));
        } catch (Exception e) {
            logger.error("Failed to reset page: " + e.getMessage());
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            DriverFactory.quitDriver();
            logger.info("Driver session closed.");
        }
    }

    protected void captureStep(String stepName) {
        try {
            boolean doShots = Boolean.parseBoolean(ConfigReader.get("screenshotEveryStep", "true"));
            if (!doShots) return;
            String ts = LocalDateTime.now().format(TS_FORMAT);
            String file = screenshotsDir + File.separator + ts + "_" + browserName + "_" + stepName + ".png";
            ScreenshotUtils.takeScreenshot(driver, file);
        } catch (Exception e) {
            logger.warn("Screenshot failed: " + e.getMessage());
        }
    }
}