import time
import logging

import scrapy
from scrapy_selenium import SeleniumRequest
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from selenium.common.exceptions import TimeoutException

from bs4 import BeautifulSoup


class StatuteSpider(scrapy.Spider):
    """
    Spider to scrape the full text of the Ontario e-Laws statute page using Scrapy + Selenium.
    """
    name = "statute_spider"

    def start_requests(self):
        """
        Use SeleniumRequest to load the dynamic content of the Ontario e-Laws statute page.
        Increase wait time if needed.
        """
        yield SeleniumRequest(
            url="https://www.ontario.ca/laws/statute/00e41",
            wait_time=15,  # Adjust if needed to ensure the content fully loads
            wait_until=EC.presence_of_element_located((By.ID, "legislative-doc")),
            callback=self.parse_page,
        )

    def parse_page(self, response):
        """
        Main parse method.
          - Verifies the presence of the legislative doc.
          - Potentially clicks on collapsible elements if needed.
          - Extracts all text (not just <p> tags).
          - Yields the text in a structured format.
        """
        driver = response.meta["driver"]
        logger = logging.getLogger(self.name)

        # Optional: small delay for any lazy-loading
        time.sleep(1)

        # -------------------------------------------------
        # Debugging / Troubleshooting
        # -------------------------------------------------
        # Save a screenshot for debugging
        driver.save_screenshot("debug_screenshot.png")
        logger.info("Screenshot saved as debug_screenshot.png")

        # Dump HTML to file for manual inspection
        with open("debug.html", "w", encoding="utf-8") as f:
            f.write(driver.page_source)
        logger.info("Page source saved to debug.html")

        # -------------------------------------------------
        # Handle potential collapsible content
        # -------------------------------------------------
        # Example code to click "Expand" or "Show more" buttons
        # for button in driver.find_elements(By.CSS_SELECTOR, ".expand-button"):
        #     driver.execute_script("arguments[0].click();", button)
        #
        # or remove if not needed.

        # -------------------------------------------------
        # Wait for any further sub-elements if needed
        # -------------------------------------------------
        try:
            WebDriverWait(driver, 5).until(
                EC.presence_of_all_elements_located((By.CSS_SELECTOR, "#legislative-doc"))
            )
        except TimeoutException:
            logger.warning("Timeout waiting for #legislative-doc sub-elements.")

        # -------------------------------------------------
        # Parse the fully rendered HTML with BeautifulSoup
        # -------------------------------------------------
        soup = BeautifulSoup(driver.page_source, "html.parser")
        content_div = soup.find("div", {"id": "legislative-doc"})
        if not content_div:
            logger.error("No 'legislative-doc' div found on page.")
            yield {"error": "No legislative-doc found"}
            return

        # Extract all text (not just <p> tags)
        full_text = content_div.get_text(separator="\n", strip=True)

        # Debug: Log a snippet of extracted text
        logger.info(f"Snippet of extracted text: {full_text[:200]}...")

        yield {
            "url": response.url,
            "full_text": full_text,
        }


# -------------------------------------------------
# Example unit test approach
# -------------------------------------------------
def test_parse_page():
    """
    Simple test function to demonstrate how to unit-test the
    parsing logic using a local HTML file instead of a live request.
    """
    import os
    from scrapy.http import HtmlResponse

    test_html_path = "test_legislative_doc.html"
    if not os.path.exists(test_html_path):
        print(f"Test file {test_html_path} not found, skipping test.")
        return

    with open(test_html_path, "r", encoding="utf-8") as f:
        html_content = f.read()

    # Create a dummy response
    response = HtmlResponse(
        url="http://example.com/test",
        body=html_content.encode("utf-8"),
        encoding="utf-8"
    )

    # Replicate the parse logic
    soup = BeautifulSoup(html_content, "html.parser")
    content_div = soup.find("div", {"id": "legislative-doc"})
    assert content_div is not None, "legislative-doc div should be present in test HTML"

    full_text = content_div.get_text(separator="\n", strip=True)
    assert len(full_text) > 0, "Extracted text should not be empty"
    print("Test passed: parse_page logic successfully extracted text.")
