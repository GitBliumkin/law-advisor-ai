import shutil
from selenium.webdriver.chrome.service import Service

BOT_NAME = "scrape"

SPIDER_MODULES = ["scrape.spiders"]
NEWSPIDER_MODULE = "scrape.spiders"

# Enable Selenium Middleware
DOWNLOADER_MIDDLEWARES = {
    "scrapy_selenium.SeleniumMiddleware": 800,
}

# Selenium Settings
SELENIUM_DRIVER_NAME = "chrome"
SELENIUM_DRIVER_EXECUTABLE_PATH = shutil.which("chromedriver")  # Use shutil to find chromedriver
SELENIUM_DRIVER_SERVICE_ARGS = Service(SELENIUM_DRIVER_EXECUTABLE_PATH)  # Use Service for Selenium 4+
SELENIUM_DRIVER_ARGUMENTS = [
    "--headless",  # Run Chrome in headless mode
    "--disable-gpu",
    "--window-size=1920,1080",
    "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36",
]

# Other Scrapy Settings
ROBOTSTXT_OBEY = True
FEED_EXPORT_ENCODING = "utf-8"
LOG_LEVEL = "INFO"
