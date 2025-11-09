import asyncio
import typer
from rich.console import Console
from rich.progress import Progress, SpinnerColumn, TextColumn

from src.config.crawl_config import (
    get_link_by_identifier,
    get_links_by_province,
    get_province_settings
)
from src.utils.helpers import logger, save_to_json, save_to_markdown
import importlib

console = Console()
app = typer.Typer(help="Legislative Crawler CLI")


# --- CORE LOGIC --- #
async def crawl_identifier(identifier: str):
    link = get_link_by_identifier(identifier)
    if not link:
        raise ValueError(f"Unknown identifier: {identifier}")

    province = link["province"]
    province_settings = get_province_settings(province)

    module = importlib.import_module(province_settings["crawler_module"])
    crawler_class = getattr(module, province_settings["crawler_class"])
    crawler = crawler_class()

    result = await crawler.crawl_law(
        identifier=link["identifier"],
        url=link["url"],
        law_name=link["law_name"]
    )
    return result


async def crawl_province(province: str):
    province_settings = get_province_settings(province)
    links = get_links_by_province(province)
    if not links:
        raise ValueError(f"No links found for province: {province}")

    module = importlib.import_module(province_settings["crawler_module"])
    crawler_class = getattr(module, province_settings["crawler_class"])
    crawler = crawler_class()

    results = await crawler.crawl_all_laws(links)
    return results


# --- CLI COMMAND --- #
@app.command()
def crawl(
    identifier: str = typer.Argument(..., help="Law identifier or province code (e.g. ON_ESA or ON)"),
    output_json: str = typer.Option(None, "--output-json", help="Save output as JSON"),
    output_md: str = typer.Option(None, "--output-md", help="Save output as Markdown")
):
    """Crawl a specific law or all laws for a province."""
    with Progress(SpinnerColumn(), TextColumn("[progress.description]{task.description}"), transient=True) as progress:
        task = progress.add_task(f"Crawling {identifier}...", total=None)

        try:
            if len(identifier) <= 3:
                results = asyncio.run(crawl_province(identifier))
                if output_json:
                    save_to_json({"results": results}, output_json)
                if output_md:
                    combined_md = "\n\n---\n\n".join(
                        [f"# {r['law_name']}\n\n{r['markdown']}" for r in results]
                    )
                    save_to_markdown(combined_md, output_md)
                console.print(f"[green]Crawled {len(results)} laws from {identifier}[/green]")
            else:
                result = asyncio.run(crawl_identifier(identifier))
                if output_json:
                    save_to_json(result, output_json)
                if output_md:
                    save_to_markdown(result["markdown"], output_md)
                console.print(f"[green]Crawled {result['law_name']}[/green]")

            progress.update(task, completed=True)
        except Exception as e:
            logger.error(f"Error: {e}")
            console.print(f"[red]Error: {e}[/red]")
            raise typer.Exit(code=1)


def main():
    app(prog_name="python -m src.cli.main")


if __name__ == "__main__":
    typer.run(crawl)
