
package com.zigwheels.tests;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.zigwheels.base.BaseTest;
import com.zigwheels.pages.ElectricScootersPage;
import com.zigwheels.pages.HomePage;
import com.zigwheels.pages.PopularCarsPage;
import com.zigwheels.pages.ProfilePage;
import com.zigwheels.pages.SearchComparisonPage;
import com.zigwheels.pages.UsedCarsPage;

public class ZigWheelsScenariosTest extends BaseTest {
	
	@Test(priority = 1)
	public void scenario1_searchAndCompareCars() throws InterruptedException {
		SearchComparisonPage comparePage = new SearchComparisonPage(driver, wait);
		comparePage.searchCar("mercedes-benz");
		comparePage.navigateToComparison();
		captureStep("S1_Comparison_Page");

		List<List<String>> rows = comparePage.scrapeComparisonTable();
		excel.writeSheet("CarComparison", null, rows);

		Assert.assertTrue(rows.size() > 0, "Comparison table is empty");
	}

	@Test(priority = 2)
	public void scenario2_popularCars() {
		HomePage home = new HomePage(driver);
		home.openPopularCars();
		captureStep("S2_PopularCars_Page");

		PopularCarsPage page = new PopularCarsPage(driver);
		List<List<String>> rows = page.scrapeNamesAndPrices();
		if (!rows.isEmpty()) {
	        excel.writeSheet("PopularCars", List.of("#", "Car Name", "Price"), rows);
	    }
		// This ensures the report shows "FAILED" if size is 0
		Assert.assertTrue(rows.size() > 0, "No popular cars were found on the page!");
	}

	@Test(priority = 4)
	public void scenario3_electricScootersOLA() throws InterruptedException {
		HomePage home = new HomePage(driver);
		home.openElectricScooters();

		ElectricScootersPage page = new ElectricScootersPage(driver, wait);
		page.filterOLA();
		captureStep("S3_Ola_Electric_Scooters");

		List<List<String>> rows = page.scrapeModels();
		excel.writeSheet("ElectricScooters_OLA", List.of("#", "Model", "Price"), rows);

		Assert.assertTrue(rows.size() > 0, "OLA Filter returned no results");
	}

	@Test(priority = 5)
	public void scenario4_usedCarsChennaiSortAndValidate() throws InterruptedException {
		HomePage home = new HomePage(driver);
		home.openUsedCars();

		UsedCarsPage page = new UsedCarsPage(driver, wait);
		page.selectCityChennaiIfModal();
		page.sortByPriceHighToLow();
		captureStep("S4_Used cars_Page");
		List<List<String>> rows = page.scrapeUsedCars();
		excel.writeSheet("UsedCars_Chennai", List.of("#", "Car Name", "Price", "Fuel Type"), rows);
		
		String errorMsg = null;
        errorMsg = page.attemptViewSellerAndGetError("9988777");
        excel.writeSheet("UsedCars_Chennai_Error", List.of("Error Message"), List.of(List.of(errorMsg)));
		
		
//		String errorMsg = page.attemptViewSellerAndGetError("987898");
//        excel.writeSheet("UsedCars_Chennai_Error", List.of("Error Message"), List.of(List.of(errorMsg)));

		Assert.assertNotNull(errorMsg, "Error message for invalid login was not captured");
	}

	@Test(priority = 6)
	public void scenario5_profileGoogleInvalidLogin() {
		HomePage home = new HomePage(driver);
		home.openProfile();

		ProfilePage profile = new ProfilePage(driver);
		String msg = profile.tryGoogleLoginInvalid();
		captureStep("S5_Error Msg");
		excel.writeSheet("GoogleLogin_Error", List.of("Error Message"), List.of(List.of(msg)));

		Assert.assertTrue(msg.length() > 0, "Google login error message is missing");
	}

	
}

//--------------VERSION 2
//package com.zigwheels.tests;
//
//import java.io.ByteArrayInputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//
//import com.zigwheels.base.BaseTest;
//import com.zigwheels.pages.ElectricScootersPage;
//import com.zigwheels.pages.HomePage;
//import com.zigwheels.pages.PopularCarsPage;
//import com.zigwheels.pages.ProfilePage;
//import com.zigwheels.pages.SearchComparisonPage;
//import com.zigwheels.pages.UpcomingBikesPage;
//import com.zigwheels.pages.UsedCarsPage;
//
//import io.qameta.allure.*;
//
//@Epic("ZigWheels Automation Suite")
//public class ZigWheelsScenariosTest extends BaseTest {
//
//    @Feature("Homepage Links")
//    @Story("Validate all homepage links")
//    @Severity(SeverityLevel.CRITICAL)
//    @Owner("QA Team")
//    @Test(priority = 1, enabled = false)
//    public void scenario1_writeAllLinksAndValidate() {
//        Allure.step("Open ZigWheels homepage");
//        HomePage home = new HomePage(driver);
//
//        Allure.step("Collect meaningful links and validate");
//        List<HomePage.LinkData> links = home.collectMeaningfulLinksAndValidate();
//
//        List<String> headers = List.of("Text", "URL", "Status", "HTTP Code");
//        List<List<String>> rows = new ArrayList<>();
//        for (HomePage.LinkData ld : links) {
//            rows.add(List.of(ld.text, ld.href, ld.status, ld.code));
//        }
//
//        excel.writeSheet("HomeLinks", headers, rows);
//
//        // Screenshot attachment
//        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
//        Allure.addAttachment("Homepage Links Screenshot", new ByteArrayInputStream(screenshot));
//
//        captureStep("01_Links_Validated");
//    }
//
//    @Feature("Popular Cars")
//    @Story("Scrape and validate popular cars list")
//    @Severity(SeverityLevel.CRITICAL)
//    @Owner("QA Team")
//    @Test(priority = 2)
//    public void scenario2_popularCars() {
//        Allure.step("Navigate to Popular Cars");
//        HomePage home = new HomePage(driver);
//        home.openPopularCars();
//        captureStep("S1_01_PopularCars_Page");
//
//        PopularCarsPage page = new PopularCarsPage(driver);
//        List<List<String>> rows = page.scrapeNamesAndPrices();
//        excel.writeSheet("PopularCars", List.of("#", "Car Name", "Price"), rows);
//
//        Assert.assertTrue(rows.size() > 0, "No popular cars scraped");
//
//        // Screenshot attachment
//        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
//        Allure.addAttachment("Popular Cars Screenshot", new ByteArrayInputStream(screenshot));
//
//        captureStep("S1_02_Back_Home");
//    }
//
//    @Feature("Upcoming Bikes")
//    @Story("Scrape upcoming bikes list")
//    @Severity(SeverityLevel.NORMAL)
//    @Owner("QA Team")
//    @Test(priority = 3, enabled = false)
//    public void scenario3_upcomingBikes() {
//        Allure.step("Navigate to Upcoming Bikes");
//        HomePage home = new HomePage(driver);
//        home.openUpcomingBikes();
//        captureStep("S2_01_UpcomingBikes_Page");
//
//        UpcomingBikesPage page = new UpcomingBikesPage(driver);
//        List<List<String>> rows = page.scrapeUpcomingBikes();
//        excel.writeSheet("UpcomingBikes", List.of("#", "Bike Name", "Price/Expected Price", "Expected Launch"), rows);
//
//        Assert.assertTrue(rows.size() > 0, "No upcoming bikes scraped");
//
//        captureStep("S2_02_Back_Home");
//    }
//
//    @Feature("Electric Scooters")
//    @Story("Filter OLA scooters and validate")
//    @Severity(SeverityLevel.CRITICAL)
//    @Owner("QA Team")
//    @Test(priority = 4)
//    public void scenario4_electricScootersOLA() throws InterruptedException {
//        Allure.step("Navigate to Electric Scooters");
//        HomePage home = new HomePage(driver);
//        home.openElectricScooters();
//        captureStep("S3_01_ElectricScooters_Page");
//
//        ElectricScootersPage page = new ElectricScootersPage(driver);
//        page.filterOLA();
//        captureStep("S3_02_OLA_Filtered");
//
//        List<List<String>> rows = page.scrapeModels();
//        excel.writeSheet("ElectricScooters_OLA", List.of("#", "Model", "Price"), rows);
//
//        Assert.assertTrue(rows.size() > 0, "No OLA scooters scraped");
//
//        captureStep("S3_03_Back_Home");
//    }
//
//    @Feature("Used Cars")
//    @Story("Scrape used cars in Chennai sorted High->Low")
//    @Severity(SeverityLevel.CRITICAL)
//    @Owner("QA Team")
//    @Test(priority = 5)
//    public void scenario5_usedCarsChennaiSortAndValidate() {
//        Allure.step("Navigate to Used Cars");
//        HomePage home = new HomePage(driver);
//        home.openUsedCars();
//
//        UsedCarsPage page = new UsedCarsPage(driver, wait);
//        page.selectCityChennaiIfModal();
//        page.sortByPriceHighToLow();
//
//        List<List<String>> rows = page.scrapeUsedCars();
//        excel.writeSheet("UsedCars_Chennai", List.of("#", "Car Name", "Price", "Fuel Type"), rows);
//
//        String errorMsg = page.attemptViewSellerAndGetError("987898");
//        excel.writeSheet("UsedCars_Chennai_Error", List.of("Error Message"), List.of(List.of(errorMsg)));
//
//        Allure.addAttachment("Error Message", "text/plain", errorMsg);
//
//        captureStep("S4_04_Error_Captured");
//    }
//
//    @Feature("Profile")
//    @Story("Invalid Google login attempt")
//    @Severity(SeverityLevel.NORMAL)
//    @Owner("QA Team")
//    @Test(priority = 6)
//    public void scenario6_profileGoogleInvalidLogin() {
//        Allure.step("Navigate to Profile");
//        HomePage home = new HomePage(driver);
//        home.openProfile();
//        captureStep("S5_01_Profile_Open");
//
//        ProfilePage profile = new ProfilePage(driver);
//        String msg = profile.tryGoogleLoginInvalid();
//        excel.writeSheet("GoogleLogin_Error", List.of("Error Message"), List.of(List.of(msg)));
//
//        Allure.addAttachment("Google Login Error", "text/plain", msg);
//
//        captureStep("S5_02_Google_Error_Captured");
//        captureStep("S5_03_Close_Profile_Back_Home");
//    }
//
//    @Feature("Search & Compare Cars")
//    @Story("Search Mercedes-Benz and compare GLS vs XC90")
//    @Severity(SeverityLevel.CRITICAL)
//    @Owner("QA Team")
//    @Test(priority = 7)
//    public void scenario7_searchAndCompareCars() throws InterruptedException {
//        Allure.step("Search Mercedes-Benz cars");
//        SearchComparisonPage comparePage = new SearchComparisonPage(driver, wait);
//        comparePage.searchCar("mercedes-benz");
//        captureStep("S6_01_Search_Results");
//
//        Allure.step("Navigate to Comparison Page");
//        comparePage.navigateToComparison();
//        captureStep("S6_02_Comparison_Page");
//
//        List<List<String>> rows = comparePage.scrapeComparisonTable();
//        excel.writeSheet("CarComparison", null, rows);
//
//        Assert.assertTrue(rows.size() > 0, "Comparison table was not scraped.");
//
//        // Screenshot attachment
//        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
//        Allure.addAttachment("Comparison Screenshot", new ByteArrayInputStream(screenshot));
//
//        captureStep("S6_03_Back_Home");
//    }
//}

//------------------version 1 

//package com.zigwheels.tests;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.testng.Assert;
//import org.testng.annotations.Test;
//
//import com.zigwheels.base.BaseTest;
//import com.zigwheels.pages.ElectricScootersPage;
//import com.zigwheels.pages.HomePage;
//import com.zigwheels.pages.PopularCarsPage;
//import com.zigwheels.pages.ProfilePage;
//import com.zigwheels.pages.SearchComparisonPage;
//import com.zigwheels.pages.UpcomingBikesPage;
//import com.zigwheels.pages.UsedCarsPage;
//
//
//public class ZigWheelsScenariosTest extends BaseTest {
//
//    @Test(priority = 1, enabled = false)
//    public void scenario1_writeAllLinksAndValidate() {
//        logger.info("Scenario 1: Collect homepage meaningful links and validate");
//
//        HomePage home = new HomePage(driver);
//        List<HomePage.LinkData> links = home.collectMeaningfulLinksAndValidate();
//
//        List<String> headers = List.of("Text", "URL", "Status", "HTTP Code");
//        List<List<String>> rows = new ArrayList<>();
//
//        for (HomePage.LinkData ld : links) {
//            rows.add(List.of(ld.text, ld.href, ld.status, ld.code));
//        }
//
//        excel.writeSheet("HomeLinks", headers, rows);
//        captureStep("01_Links_Validated");
//    }
//
//
//    @Test(priority = 2)
//    public void scenario2_popularCars() {
//        logger.info("Scenario 2: New Cars > Popular Cars");
//        HomePage home = new HomePage(driver);
//        home.openPopularCars();
//        captureStep("S1_01_PopularCars_Page");
//        PopularCarsPage page = new PopularCarsPage(driver);
//        List<List<String>> rows = page.scrapeNamesAndPrices();
//        excel.writeSheet("PopularCars", List.of("#", "Car Name", "Price"), rows);
//        Assert.assertTrue(rows.size() > 0, "No popular cars scraped");
//        //home.goHome();
//        captureStep("S1_02_Back_Home");
//    }
//
//    @Test(priority = 3 ,enabled = false)
//    public void scenario3_upcomingBikes() {
//        logger.info("Scenario 3: New Bikes > Upcoming Bikes");
//        HomePage home = new HomePage(driver);
//        home.openUpcomingBikes();
//        captureStep("S2_01_UpcomingBikes_Page");
//        UpcomingBikesPage page = new UpcomingBikesPage(driver);
//        List<List<String>> rows = page.scrapeUpcomingBikes();
//        excel.writeSheet("UpcomingBikes", List.of("#", "Bike Name", "Price/Expected Price", "Expected Launch"), rows);
//        Assert.assertTrue(rows.size() > 0, "No upcoming bikes scraped");
//       // home.goHome();
//        captureStep("S2_02_Back_Home");
//    }
//
//    @Test(priority = 4)
//    public void scenario4_electricScootersOLA() {
//        logger.info("Scenario 4: Scooters > Electric Scooters > OLA");
//        HomePage home = new HomePage(driver);
//        home.openElectricScooters();
//        captureStep("S3_01_ElectricScooters_Page");
//        ElectricScootersPage page = new ElectricScootersPage(driver);
//        page.filterOLA();
//        captureStep("S3_02_OLA_Filtered");
//        List<List<String>> rows = page.scrapeModels();
//        excel.writeSheet("ElectricScooters_OLA", List.of("#", "Model", "Price"), rows);
//        Assert.assertTrue(rows.size() > 0, "No OLA scooters scraped");
//        //home.goHome();
//        captureStep("S3_03_Back_Home");
//    }
//
//  
//    @Test(priority = 5)
//    public void scenario5_usedCarsChennaiSortAndValidate() {
//        logger.info("Scenario 5: More > Used Cars (Chennai) sort High->Low");
//        HomePage home = new HomePage(driver);
//        home.openUsedCars();
//        
//        // We pass 'wait' from BaseTest here
//        UsedCarsPage page = new UsedCarsPage(driver, wait);
//        
//        page.selectCityChennaiIfModal();
//        page.sortByPriceHighToLow(); // Potential stale point handled inside page class
//        
//        List<List<String>> rows = page.scrapeUsedCars();
//        excel.writeSheet("UsedCars_Chennai", List.of("#", "Car Name", "Price", "Fuel Type"), rows);
//
//        // Get the error message from the modal
//        String errorMsg = page.attemptViewSellerAndGetError("987898"); 
//        excel.writeSheet("UsedCars_Chennai_Error", List.of("Error Message"), List.of(List.of(errorMsg)));
//        
//        captureStep("S4_04_Error_Captured");
//    }
//
//    @Test(priority = 6)
//    public void scenario6_profileGoogleInvalidLogin() {
//        logger.info("Scenario 6: Profile > Google login invalid");
//        HomePage home = new HomePage(driver);
//        home.openProfile();
//        captureStep("S5_01_Profile_Open");
//        ProfilePage profile = new ProfilePage(driver);
//        String msg = profile.tryGoogleLoginInvalid();
//        excel.writeSheet("GoogleLogin_Error", List.of("Error Message"), List.of(List.of(msg)));
//        captureStep("S5_02_Google_Error_Captured");
//        
//        captureStep("S5_03_Close_Profile_Back_Home");
//    }
//    @Test(priority = 7)
//    public void scenario7_searchAndCompareCars() throws InterruptedException {
//        logger.info("Scenario 7: Search Mercedes-Benz and Compare GLS vs XC90");
//        
//        
//        // Using existing search functionality if available, or direct interaction
//        SearchComparisonPage comparePage = new SearchComparisonPage(driver, wait);
//        
//        comparePage.searchCar("mercedes-benz");
//        captureStep("S6_01_Search_Results");
//        
//        comparePage.navigateToComparison();
//        captureStep("S6_02_Comparison_Page");
//        
//        List<List<String>> rows = comparePage.scrapeComparisonTable();
//        
//        // We pass null for headers because scrapeComparisonTable captures <th> as the first row
//        excel.writeSheet("CarComparison", null, rows);
//        
//        Assert.assertTrue(rows.size() > 0, "Comparison table was not scraped.");
//        captureStep("S6_03_Back_Home");
//    }
//
//
//}
