# src/embedder/embedding.py

from functools import lru_cache
from typing import List

from sentence_transformers import SentenceTransformer


@lru_cache(maxsize=1)
def _get_model() -> SentenceTransformer:
    """
    Lazily load and cache the model.
    Called once per process.
    """
    # Good default – fast and solid
    return SentenceTransformer("all-MiniLM-L6-v2")


def embed_text(text: str) -> List[float]:
    """
    Compute embedding for a single text block.
    Returns Python list[float] so it's JSON-serializable.
    """
    if not text or not text.strip():
        return []

    model = _get_model()
    return model.encode(text).tolist()


def embed_texts(texts: list[str]) -> list[list[float]]:
    """
    Batch embeddings (you may use later for sections).
    """
    if not texts:
        return []

    model = _get_model()
    return model.encode(texts).tolist()
