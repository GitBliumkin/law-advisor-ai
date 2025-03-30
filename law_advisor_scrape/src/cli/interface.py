"""
Command-line interface for the legislative crawler.
"""
import asyncio
import importlib
from typing import Optional, List, Dict, Any

import typer
from rich.console import Console
from rich.progress import Progress, SpinnerColumn, TextColumn

from ..config.crawl_config import (
    get_link_by_identifier,
    get_links_by_province,
    get_province_settings
)
from ..utils.helpers import logger, save_to_json, save_to_markdown

app = typer.Typer(help="Legislative Crawler CLI")
console = Console()

async def crawl_identifier(identifier: str) -> Dict[str, Any]:
    """
    Crawl a specific law by its identifier.
    
    Args:
        identifier (str): Law identifier (e.g., FED_CLC)
        
    Returns:
        Dict[str, Any]: Crawling result
    """
    # Get link information
    link = get_link_by_identifier(identifier)
    if not link:
        raise ValueError(f"Unknown identifier: {identifier}")
    
    # Get province settings
    province = link["province"]
    province_settings = get_province_settings(province)
    
    # Import crawler module and class
    module_name = province_settings["crawler_module"]
    class_name = province_settings["crawler_class"]
    
    module = importlib.import_module(f"legislative_crawler.{module_name}")
    crawler_class = getattr(module, class_name)
    
    # Initialize crawler
    crawler = crawler_class()
    
    # Crawl law
    result = await crawler.crawl_law(
        identifier=link["identifier"],
        url=link["url"],
        law_name=link["law_name"]
    )
    
    return result

async def crawl_province(province: str) -> List[Dict[str, Any]]:
    """
    Crawl all laws for a specific province.
    
    Args:
        province (str): Province code (e.g., FED, ONT)
        
    Returns:
        List[Dict[str, Any]]: List of crawling results
    """
    # Get province settings
    province_settings = get_province_settings(province)
    
    # Get all links for the province
    links = get_links_by_province(province)
    if not links:
        raise ValueError(f"No links found for province: {province}")
    
    # Import crawler module and class
    module_name = province_settings["crawler_module"]
    class_name = province_settings["crawler_class"]
    
    module = importlib.import_module(f"legislative_crawler.{module_name}")
    crawler_class = getattr(module, class_name)
    
    # Initialize crawler
    crawler = crawler_class()
    
    # Crawl all laws
    results = await crawler.crawl_all_laws(links)
    
    return results

def run_crawler(
    identifier: str,
    output_json: Optional[str] = None,
    output_md: Optional[str] = None
):
    """
    Run the crawler from the command line.
    
    Args:
        identifier (str): Law or province identifier
        output_json (Optional[str]): JSON output filename
        output_md (Optional[str]): Markdown output filename
    """
    with Progress(
        SpinnerColumn(),
        TextColumn("[progress.description]{task.description}"),
        transient=True,
    ) as progress:
        task = progress.add_task(f"Crawling {identifier}...", total=None)
        
        try:
            # Check if identifier is a province or a specific law
            if len(identifier) <= 3:  # Province code (e.g., FED, ONT)
                results = asyncio.run(crawl_province(identifier))
                
                # Save results
                if output_json:
                    save_to_json({"results": results}, output_json)
                
                if output_md:
                    # Combine all markdown content
                    combined_md = "\n\n---\n\n".join(
                        [f"# {r['law_name']}\n\n{r['markdown']}" for r in results]
                    )
                    save_to_markdown(combined_md, output_md)
                
                # Print summary
                console.print(f"[green]Successfully crawled {len(results)} laws for {identifier}[/green]")
                
            else:  # Specific law identifier (e.g., FED_CLC)
                result = asyncio.run(crawl_identifier(identifier))
                
                # Save result
                if output_json:
                    save_to_json(result, output_json)
                
                if output_md:
                    save_to_markdown(result["markdown"], output_md)
                
                # Print summary
                console.print(f"[green]Successfully crawled {result['law_name']}[/green]")
            
            progress.update(task, completed=True)
            
        except Exception as e:
            logger.error(f"Error crawling {identifier}: {e}")
            console.print(f"[red]Error: {e}[/red]")
            raise typer.Exit(code=1)

@app.command()
def crawl(
    identifier: str = typer.Argument(..., help="Law identifier (e.g., FED_CLC) or province code (e.g., FED)"),
    output_json: Optional[str] = typer.Option(None, "--output-json", help="JSON output filename"),
    output_md: Optional[str] = typer.Option(None, "--output-md", help="Markdown output filename")
):
    """
    Crawl a specific law or all laws for a province.
    """
    run_crawler(identifier, output_json, output_md)

if __name__ == "__main__":
    app()
