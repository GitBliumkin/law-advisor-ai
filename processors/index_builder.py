"""Manage Qdrant collection and upsert operations."""
from __future__ import annotations
from typing import List
from qdrant_client import QdrantClient
from qdrant_client.models import Distance, VectorParams
from config import (
    QDRANT_URL,
    QDRANT_API_KEY,
    COLLECTION_NAME,
    VECTOR_DIM,
    DRY_RUN,
)
from utils.logging_setup import logger

_client = None
if not DRY_RUN:  # Initialize client only if not in dry-run mode
    _client = QdrantClient(url=QDRANT_URL, api_key=QDRANT_API_KEY)


def recreate_collection():
    if DRY_RUN:
        logger.warning("DRY_RUN: skipping Qdrant collection creation")
        return
    if COLLECTION_NAME in [c.name for c in _client.get_collections().collections]:
        _client.delete_collection(collection_name=COLLECTION_NAME)
    _client.create_collection(
        collection_name=COLLECTION_NAME,
        vectors_config=VectorParams(size=VECTOR_DIM, distance=Distance.COSINE),
    )
    logger.info(f"✅ Created Qdrant collection: {COLLECTION_NAME}")


def upsert_nodes(nodes):
    if DRY_RUN:
        logger.warning("DRY_RUN: skipping Qdrant upsert")
        return
    points = []
    for n in nodes:
        payload = n.metadata or {}
        payload["text"] = n.get_content()
        points.append({
            "id": n.id_,
            "vector": n.embedding,
            "payload": payload
        })
    _client.upsert(collection_name=COLLECTION_NAME, points=points)
    logger.info(f"✅ Upserted {len(points)} points to Qdrant") 