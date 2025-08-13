#!/usr/bin/env python3
"""Convert JSONL output to a single JSON file with all nodes in an array."""
import json
import argparse
from pathlib import Path
from typing import List, Dict, Any

def convert_jsonl_to_json(jsonl_path: Path, json_path: Path) -> None:
    """Convert JSONL file to single JSON file with array of objects."""
    nodes = []
    
    print(f"Reading JSONL file: {jsonl_path}")
    with jsonl_path.open('r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if line:
                try:
                    node = json.loads(line)
                    nodes.append(node)
                except json.JSONDecodeError as e:
                    print(f"Warning: Invalid JSON on line {line_num}: {e}")
    
    print(f"Loaded {len(nodes)} nodes")
    
    # Create output directory if it doesn't exist
    json_path.parent.mkdir(parents=True, exist_ok=True)
    
    # Write as single JSON file
    with json_path.open('w', encoding='utf-8') as f:
        json.dump({
            "metadata": {
                "total_nodes": len(nodes),
                "source": "ESA 2000 Act",
                "format": "single_json"
            },
            "nodes": nodes
        }, f, ensure_ascii=False, indent=2)
    
    print(f"✅ Converted to JSON file: {json_path}")
    print(f"📊 Total nodes: {len(nodes)}")
    print(f"📁 File size: {json_path.stat().st_size / (1024*1024):.1f} MB")

def main():
    parser = argparse.ArgumentParser(description="Convert JSONL to single JSON file")
    parser.add_argument("--input", "-i", default="data/outputs/nodes.jsonl", 
                       help="Input JSONL file path")
    parser.add_argument("--output", "-o", default="data/outputs/nodes.json",
                       help="Output JSON file path")
    
    args = parser.parse_args()
    
    jsonl_path = Path(args.input)
    json_path = Path(args.output)
    
    if not jsonl_path.exists():
        print(f"❌ Error: Input file not found: {jsonl_path}")
        return 1
    
    convert_jsonl_to_json(jsonl_path, json_path)
    return 0

if __name__ == "__main__":
    exit(main()) 