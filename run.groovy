@Grab(group = 'org.seleniumhq.selenium', module = 'selenium-java', version = '3.4.0')

import org.openqa.selenium.*
import org.openqa.selenium.support.ui.*
import org.openqa.selenium.firefox.FirefoxDriver

System.setProperty('webdriver.gecko.driver', 'geckodriver.exe')

Properties properties = new Properties()
File propertiesFile = new File('config.properties')
propertiesFile.withInputStream {
    properties.load(it)
}

Closure waitClosure = { driverRef, elementId, by ->
    (new WebDriverWait(driverRef, 10)).until(new ExpectedCondition<Boolean>() {
        Boolean apply(WebDriver d) {
            if (by == 'id')
                return ((FirefoxDriver) d).findElementById(elementId).isDisplayed()
            if (by == 'css')
                return ((FirefoxDriver) d).findElementByCssSelector(elementId).isDisplayed()
            if (by == 'xpath')
                return ((FirefoxDriver) d).findElementByXPath(elementId).isDisplayed()
        }
    })
}

WebDriver driver = new FirefoxDriver()
driver.get('http://www.gsc.com.my')

driver.findElementById('LoginProfileToggle').click()

WebElement usernameInput = driver.findElementById('getHeader_ctrl_getHeaderDesktop_UserName')
usernameInput.clear()
usernameInput.sendKeys(properties.email)

WebElement passwordInput = driver.findElementById('getHeader_ctrl_getHeaderDesktop_Password')
passwordInput.clear()
passwordInput.sendKeys(properties.password)

driver.findElementById('getHeader_ctrl_getHeaderDesktop_LoginButton').click()

waitClosure(driver, 'LoginProfileToggle', 'id')

String mainWindow = driver.getWindowHandle()
for (; ;) {
    driver.switchTo().window(mainWindow)
    driver.get('http://www.gsc.com.my/html/epayment.aspx?ID1=38&ID=58&PID=65')

    waitClosure(driver, '//*[@id="specialNoticeModal"]/div[2]/div/div[3]/button', 'xpath')

    driver.findElementByXPath('//*[@id="specialNoticeModal"]/div[2]/div/div[3]/button').click()

    (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
        Boolean apply(WebDriver d) {
            return ((FirefoxDriver) d).findElementById('specialNoticeModal').getAttribute('style') == 'display: none;'
        }
    })

    waitClosure(driver, '//*[@id="pgBooking"]/div[1]/div[1]/ul/li[2]', 'xpath')
    driver.findElementByXPath('//*[@id="pgBooking"]/div[1]/div[1]/ul/li[2]').click()

    waitClosure(driver, "//a[text()='$properties.cinema']", 'xpath')
    driver.findElementByXPath("//a[text()='$properties.cinema']").click()

    waitClosure(driver, "//a[text()='$properties.date']", 'xpath')
    driver.findElementByXPath("//a[text()='$properties.date']").click()

    waitClosure(driver, "//h4[text()='$properties.movie']", 'xpath')
    WebElement movieElement = driver.findElementByXPath("//h4[text()='$properties.movie']")
	
    WebElement parentElement = movieElement.findElement(By.xpath("./.."))
    parentElement.findElement(By.xpath("//a[text()='$properties.time']")).click()

    waitClosure(driver, "div[seatname=\"$properties.seat\"", 'css')
    driver.findElementByCssSelector("div[seatname=\"$properties.seat\"").click()

    driver.findElementByXPath('/*//*[@id="pgSeating"]/div[3]/div[2]/div[3]').click()

    (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
        Boolean apply(WebDriver d) {
            return ((FirefoxDriver) d).findElementById('spanTicketTotal').getText() != '0.00'
        }
    })

    try {
        driver.findElementById('cbAgreement').click()
    } catch (Exception e) {
        continue
    }

    driver.findElementById('btnPurchase').click()
}


