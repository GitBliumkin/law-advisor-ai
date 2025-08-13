"""Orchestrates crawl → chunk → embed → (Qdrant or JSONL dump)."""
import asyncio
import argparse
from pathlib import Path
from utils.logging_setup import logger
from crawler.crawler import crawl
from processors.chunker import make_chunks
from processors.embedder import embed_nodes
from processors.index_builder import recreate_collection, upsert_nodes
from utils.dump import dump_nodes_jsonl
from config import OUTPUT_DIR, DRY_RUN, INCLUDE_EMBEDDINGS


def gather_html_paths() -> list[Path]:
    """Find all HTML files in raw data directory."""
    from config import RAW_DIR
    # Prefer content-only files produced by the CanLII crawler
    content_only = list(RAW_DIR.glob("*_content.html"))
    if content_only:
        return content_only
    return list(RAW_DIR.glob("*.html"))


async def pipeline(args):
    # 1) Crawl
    await crawl()
    html_paths = gather_html_paths()
    logger.info("Downloaded %s HTML files", len(html_paths))

    # 2) Chunk all
    all_nodes = []
    for p in html_paths:
        nodes = make_chunks(p)
        all_nodes.extend(nodes)
    logger.info("Generated %s text nodes", len(all_nodes))

    # 3) Embed (optional)
    if not args.no_embed:
        embed_nodes(all_nodes)
    else:
        logger.warning("--no-embed: embeddings will be omitted")

    # 4) Output
    if args.dry_run or DRY_RUN:
        out_path = OUTPUT_DIR / "nodes.jsonl"
        dump_nodes_jsonl(all_nodes, out_path, include_embeddings=(args.include_embeddings or INCLUDE_EMBEDDINGS))
        logger.info("Wrote JSONL dump → %s", out_path)
        return

    recreate_collection()
    upsert_nodes(all_nodes)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--dry-run", action="store_true", help="Skip Qdrant and write JSONL dump")
    parser.add_argument("--no-embed", action="store_true", help="Do not compute embeddings")
    parser.add_argument("--include-embeddings", action="store_true", help="Include vectors in dump")
    args = parser.parse_args()
    asyncio.run(pipeline(args)) 