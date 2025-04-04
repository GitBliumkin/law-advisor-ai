"""
Unit tests for the CLI interface.
"""
import pytest
from unittest.mock import patch, MagicMock
from typer.testing import CliRunner

from legislative_crawler.cli.interface import app, run_crawler

runner = CliRunner()

@patch("legislative_crawler.cli.interface.crawl_identifier")
@patch("legislative_crawler.cli.interface.save_to_json")
@patch("legislative_crawler.cli.interface.save_to_markdown")
def test_run_crawler_with_law_identifier(mock_save_md, mock_save_json, mock_crawl_identifier):
    """Test run_crawler function with a law identifier."""
    # Mock the async function
    mock_crawl_identifier.return_value = {
        "url_identifier": "FED_CLC",
        "url": "https://laws-lois.justice.gc.ca/eng/acts/L-2/",
        "law_name": "Canada Labour Code",
        "scrape_date": "01/01/2024",
        "scrape_time": "12:00",
        "status": "success",
        "markdown": "# Canada Labour Code"
    }
    
    # Run the function
    run_crawler(
        identifier="FED_CLC",
        output_json="test.json",
        output_md="test.md"
    )
    
    # Check that the mocks were called
    mock_crawl_identifier.assert_called_once()
    mock_save_json.assert_called_once()
    mock_save_md.assert_called_once()
    
    # Check arguments
    mock_save_json.assert_called_with(mock_crawl_identifier.return_value, "test.json")
    mock_save_md.assert_called_with(mock_crawl_identifier.return_value["markdown"], "test.md")

@patch("legislative_crawler.cli.interface.crawl_province")
@patch("legislative_crawler.cli.interface.save_to_json")
@patch("legislative_crawler.cli.interface.save_to_markdown")
def test_run_crawler_with_province(mock_save_md, mock_save_json, mock_crawl_province):
    """Test run_crawler function with a province code."""
    # Mock the async function
    mock_crawl_province.return_value = [
        {
            "url_identifier": "FED_CLC",
            "url": "https://laws-lois.justice.gc.ca/eng/acts/L-2/",
            "law_name": "Canada Labour Code",
            "scrape_date": "01/01/2024",
            "scrape_time": "12:00",
            "status": "success",
            "markdown": "# Canada Labour Code"
        },
        {
            "url_identifier": "FED_CHRA",
            "url": "https://laws-lois.justice.gc.ca/eng/acts/h-6/",
            "law_name": "Canadian Human Rights Act",
            "scrape_date": "01/01/2024",
            "scrape_time": "12:00",
            "status": "success",
            "markdown": "# Canadian Human Rights Act"
        }
    ]
    
    # Run the function
    run_crawler(
        identifier="FED",
        output_json="test.json",
        output_md="test.md"
    )
    
    # Check that the mocks were called
    mock_crawl_province.assert_called_once()
    mock_save_json.assert_called_once()
    mock_save_md.assert_called_once()
    
    # Check arguments
    mock_save_json.assert_called_with({"results": mock_crawl_province.return_value}, "test.json")
    # We can't easily check the exact markdown content, but we can check that it was called

@patch("legislative_crawler.cli.interface.run_crawler")
def test_cli_crawl_command(mock_run_crawler):
    """Test the CLI crawl command."""
    # Run the command
    result = runner.invoke(app, ["crawl", "FED_CLC", "--output-json", "test.json", "--output-md", "test.md"])
    
    # Check that the command ran successfully
    assert result.exit_code == 0
    
    # Check that run_crawler was called with the correct arguments
    mock_run_crawler.assert_called_once_with("FED_CLC", "test.json", "test.md")

@patch("legislative_crawler.cli.interface.run_crawler")
def test_cli_crawl_command_with_province(mock_run_crawler):
    """Test the CLI crawl command with a province code."""
    # Run the command
    result = runner.invoke(app, ["crawl", "FED", "--output-json", "test.json", "--output-md", "test.md"])
    
    # Check that the command ran successfully
    assert result.exit_code == 0
    
    # Check that run_crawler was called with the correct arguments
    mock_run_crawler.assert_called_once_with("FED", "test.json", "test.md")

@patch("legislative_crawler.cli.interface.run_crawler")
def test_cli_crawl_command_without_output(mock_run_crawler):
    """Test the CLI crawl command without output options."""
    # Run the command
    result = runner.invoke(app, ["crawl", "FED_CLC"])
    
    # Check that the command ran successfully
    assert result.exit_code == 0
    
    # Check that run_crawler was called with the correct arguments
    mock_run_crawler.assert_called_once_with("FED_CLC", None, None)

@patch("legislative_crawler.cli.interface.crawl_identifier")
def test_run_crawler_with_error(mock_crawl_identifier):
    """Test run_crawler function with an error."""
    # Mock the async function to raise an exception
    mock_crawl_identifier.side_effect = ValueError("Test error")
    
    # Run the function and check that it raises a typer.Exit
    with pytest.raises(SystemExit):
        run_crawler(
            identifier="FED_CLC",
            output_json="test.json",
            output_md="test.md"
        )
