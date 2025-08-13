#!/usr/bin/env python3
"""
Original working crawler for Ontario Employment Standards Act
This file was kept as requested by the user
"""

import asyncio
import aiohttp
from bs4 import BeautifulSoup
from pathlib import Path
import time

async def crawl_esa():
    """Crawl the Ontario Employment Standards Act website."""
    base_url = "https://www.ontario.ca/laws/statute/00e41"
    
    # Create data directory
    data_dir = Path("data/raw")
    data_dir.mkdir(parents=True, exist_ok=True)
    
    async with aiohttp.ClientSession() as session:
        # Get main page
        async with session.get(base_url) as response:
            if response.status == 200:
                html = await response.text()
                
                # Save main page
                with open(data_dir / "main_page.html", "w", encoding="utf-8") as f:
                    f.write(html)
                print(f"Saved main page: {data_dir / 'main_page.html'}")
                
                # Parse for links
                soup = BeautifulSoup(html, 'html.parser')
                links = soup.find_all('a', href=True)
                
                # Find ESA-related links
                esa_links = []
                for link in links:
                    href = link['href']
                    if '/laws/statute/00e41' in href:
                        full_url = f"https://www.ontario.ca{href}" if href.startswith('/') else href
                        esa_links.append(full_url)
                
                print(f"Found {len(esa_links)} ESA-related links")
                
                # Crawl each link
                for i, url in enumerate(esa_links[:5]):  # Limit to first 5 for testing
                    try:
                        async with session.get(url) as response:
                            if response.status == 200:
                                content = await response.text()
                                
                                # Create filename from URL
                                filename = url.split('/')[-1] or 'index'
                                filename = filename.replace('#', '_').replace('?', '_')
                                if not filename.endswith('.html'):
                                    filename += '.html'
                                
                                filepath = data_dir / filename
                                with open(filepath, "w", encoding="utf-8") as f:
                                    f.write(content)
                                print(f"Saved: {filepath}")
                                
                                # Be nice to the server
                                await asyncio.sleep(1)
                                
                    except Exception as e:
                        print(f"Error crawling {url}: {e}")
                
                print("Crawling completed!")
            else:
                print(f"Failed to fetch main page: {response.status}")

if __name__ == "__main__":
    asyncio.run(crawl_esa()) 