"""Convert raw HTML into hierarchical text chunks."""
from __future__ import annotations
from uuid import uuid4
from pathlib import Path
from bs4 import BeautifulSoup
from llama_index.core.schema import TextNode, MetadataMode
from utils.logging_setup import logger

HEADING_TAGS = ["h1", "h2", "h3", "h4"]  # ESA pages use h2 for Parts, h3 for sections

def make_chunks(html_path: Path) -> list[TextNode]:
    """Parse HTML file and create hierarchical text chunks."""
    logger.info(f"Chunking {html_path}")
    
    with html_path.open("r", encoding="utf-8") as f:
        soup = BeautifulSoup(f.read(), "lxml")
    
    # Remove script and style elements
    for script in soup(["script", "style"]):
        script.decompose()
    
    nodes = []
    current_section = None
    current_parent = None
    
    # Find main content area - look for the legislative content
    content = soup.find("div", {"id": "legislative-doc"}) or soup.find("div", {"class": "content"}) or soup.find("body")
    if not content:
        logger.warning(f"No content found in {html_path}")
        return nodes
    
    # Handle Ontario ESA specific structure
    # Look for table of contents and section content
    toc_elements = content.find_all(["p", "div"], class_=lambda x: x and any(cls in x for cls in ["TOCpartCenter", "TOCid", "TOCheadCenter"]))
    
    for element in toc_elements:
        class_name = element.get("class", [])
        class_name = class_name[0] if class_name else ""
        
        # Handle Part headings (TOCpartCenter)
        if "TOCpartCenter" in class_name:
            title = element.get_text(strip=True)
            if title and len(title) > 5:
                # Create section node
                section_id = str(uuid4())
                section_node = TextNode(
                    text=title,
                    metadata={
                        "title": title,
                        "level": 2,
                        "section": True,
                        "parent_id": current_parent,
                        "source": html_path.name,
                        "type": "part"
                    }
                )
                section_node.id_ = section_id
                nodes.append(section_node)
                current_section = section_id
                current_parent = section_id
        
        # Handle section headings (TOCid)
        elif "TOCid" in class_name:
            title = element.get_text(strip=True)
            if title and len(title) > 5:
                # Create section node
                section_id = str(uuid4())
                section_node = TextNode(
                    text=title,
                    metadata={
                        "title": title,
                        "level": 3,
                        "section": True,
                        "parent_id": current_parent,
                        "source": html_path.name,
                        "type": "section"
                    }
                )
                section_node.id_ = section_id
                nodes.append(section_node)
                current_section = section_id
        
        # Handle subsection headings (TOCheadCenter)
        elif "TOCheadCenter" in class_name:
            title = element.get_text(strip=True)
            if title and len(title) > 5:
                # Create section node
                section_id = str(uuid4())
                section_node = TextNode(
                    text=title,
                    metadata={
                        "title": title,
                        "level": 4,
                        "section": True,
                        "parent_id": current_parent,
                        "source": html_path.name,
                        "type": "subsection"
                    }
                )
                section_id = section_id
                nodes.append(section_node)
                current_section = section_id
    
    # Also look for actual content sections
    content_sections = content.find_all(["p", "div"], class_=lambda x: x and any(cls in x for cls in ["section", "definition", "paragraph", "partnum", "headnote"]))
    
    for element in content_sections:
        class_name = element.get("class", [])
        class_name = class_name[0] if class_name else ""
        
        text = element.get_text(strip=True)
        if text and len(text) > 20:  # Skip very short content
            content_node = TextNode(
                text=text,
                metadata={
                    "title": current_section,
                    "level": 5,
                    "parent_id": current_parent,
                    "source": html_path.name,
                    "type": "content",
                    "content_class": class_name
                }
            )
            content_node.id_ = str(uuid4())
            nodes.append(content_node)
    
    # If we didn't find any structured content, try to extract from the main content area
    if not nodes:
        logger.info(f"No structured content found, extracting from main content area")
        main_content = content.find("div", {"class": "laws-document__act-content"})
        if main_content:
            for element in main_content.find_all(["p", "div", "h1", "h2", "h3", "h4"]):
                text = element.get_text(strip=True)
                if text and len(text) > 20:
                    content_node = TextNode(
                        text=text,
                        metadata={
                            "title": "Main Content",
                            "level": 1,
                            "parent_id": None,
                            "source": html_path.name,
                            "type": "fallback"
                        }
                    )
                    content_node.id_ = str(uuid4())
                    nodes.append(content_node)
    
    logger.info(f"Created {len(nodes)} chunks from {html_path}")
    return nodes 