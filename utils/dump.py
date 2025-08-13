from __future__ import annotations
import json
from pathlib import Path
from typing import Iterable
from llama_index.core.schema import TextNode

def _node_to_dict(n: TextNode, include_embeddings: bool) -> dict:
    d = {
        "id": n.id_,
        "text": n.get_content(),
        "metadata": n.metadata or {},
    }
    if include_embeddings:
        # Convert to list for JSON
        d["embedding"] = list(n.embedding) if getattr(n, "embedding", None) is not None else None
    return d

def dump_nodes_jsonl(nodes: Iterable[TextNode], path: Path, include_embeddings: bool = True) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8") as f:
        for n in nodes:
            f.write(json.dumps(_node_to_dict(n, include_embeddings), ensure_ascii=False) + "\n") 