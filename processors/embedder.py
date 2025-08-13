"""Embed text nodes using OpenAI embeddings."""
from __future__ import annotations
from typing import List
from config import EMBEDDING_MODEL_NAME, OPENAI_API_KEY
from langchain_openai import OpenAIEmbeddings
from utils.logging_setup import logger

_embedder = OpenAIEmbeddings(model=EMBEDDING_MODEL_NAME, openai_api_key=OPENAI_API_KEY)

def embed_nodes(nodes: List) -> None:
    """Add embeddings to all text nodes."""
    logger.info(f"Embedding {len(nodes)} nodes with {EMBEDDING_MODEL_NAME}")
    
    texts = [node.get_content() for node in nodes]
    embeddings = _embedder.embed_documents(texts)
    
    for node, embedding in zip(nodes, embeddings):
        node.embedding = embedding
    
    logger.info("✅ Embeddings completed") 