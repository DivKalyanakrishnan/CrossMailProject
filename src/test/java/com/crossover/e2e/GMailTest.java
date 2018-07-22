package com.crossover.e2e;

import junit.framework.TestCase;
import org.junit.Test;

import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import com.crossover.page.*;

public class GMailTest extends TestCase {
	static WebDriver driver = null;	
	
    public void setUp()  {
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();      
    }
 
	
    public void tearDown() throws Exception {
       driver.quit();
    }
 
    @Test
    public void testCompleteScenario() throws Exception {
    	GMailPage pageObj = new GMailPage(driver);
    	pageObj.login();
    	Assert.assertTrue(pageObj.isUserLoggedIn());
    	
    	pageObj.composeEmail();
    	pageObj.sendEmail();
    	
    	SearchMailPage searchPageObj = new SearchMailPage();
    	Assert.assertTrue(searchPageObj.searchEmail());
    	
    }
}

