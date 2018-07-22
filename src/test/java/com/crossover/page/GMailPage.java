package test.java.com.crossover.page;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GMailPage {
	private static WebDriver driver;
	private Properties properties;
	private WebDriverWait wait;

	public GMailPage(WebDriver driver) throws Exception {
		GMailPage.driver = driver;
		loadProperties();
		wait = new WebDriverWait(driver, 3000);
	}

	private void loadProperties() {
		try {
			properties = new Properties();
			properties.load(new FileReader(new File("test.properties")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void login() throws Exception {
		driver.get(properties.getProperty("url"));

		WebElement userElement = driver.findElement(By.id("identifierId"));
		userElement.sendKeys(properties.getProperty("username"));
		driver.findElement(By.id("identifierNext")).click();

		Thread.sleep(1000);

		WebElement passwordElement = driver.findElement(By.name("password"));
		passwordElement.sendKeys(properties.getProperty("password"));
		driver.findElement(By.id("passwordNext")).click();

		Thread.sleep(1000);
	}

	public Boolean isUserLoggedIn() {
		WebElement signout = driver.findElement(By.xpath("//a[contains(@href,'SignOut')]"));
		Boolean isLoggedIn = (signout.getAttribute("title").contains(properties.getProperty("firstname")));
		return isLoggedIn;
	}

	public void composeEmail() {
		WebElement composeElement = driver.findElement(By.xpath("//*[@role='button' and (.)='COMPOSE']"));
		composeElement.click();

		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[(.)='New Message']")));

		driver.findElement(By.name("to")).clear();
		driver.findElement(By.name("to")).sendKeys(String.format("%s", properties.getProperty("username")));

		driver.findElement(By.name("subjectbox")).sendKeys(properties.getProperty("subject"));

		String bodyContentPath = "//div[@aria-label='Message Body' and @role='textbox']";
		driver.findElement(By.xpath(bodyContentPath)).sendKeys(properties.getProperty("content"));

		By attachFiles = By.xpath("//div[@data-tooltip='Attach files']/div/div/div[contains(@style,'user-select')]");
		driver.findElement(attachFiles).click();

		attachFiles();
	}

	private void attachFiles() {
		try {

			String dataFolderPath = System.getProperty("user.dir") + "\\src\\test\\java\\com\\crossover\\data\\";
			String fileFolderPath = System.getProperty("user.dir") + "\\";

			String execFilePath = dataFolderPath + "FileUpload.exe";
			String filePath = fileFolderPath + "Divya_Resume.pdf";

			// pass file name as argument
			Runtime.getRuntime().exec(execFilePath + " " + filePath);

			Thread.sleep(5000);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void sendEmail() throws Exception {
		driver.findElement(By.xpath("//*[@role='button' and text()='Send']")).click();
		Thread.sleep(5000);
		driver.findElement(By.xpath("//a[contains(@title,'Inbox')]")).click();
	}

}
