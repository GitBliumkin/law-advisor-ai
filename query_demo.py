"""Minimal query demo: filter + semantic search."""
import sys
from langchain_openai import OpenAIEmbeddings
from langchain_community.vectorstores import Qdrant
from qdrant_client import QdrantClient
from config import (
    OPENAI_API_KEY,
    QDRANT_URL,
    QDRANT_API_KEY,
    COLLECTION_NAME,
    EMBEDDING_MODEL_NAME,
)

def main():
    # Initialize clients
    client = QdrantClient(url=QDRANT_URL, api_key=QDRANT_API_KEY)
    embeddings = OpenAIEmbeddings(
        model=EMBEDDING_MODEL_NAME, openai_api_key=OPENAI_API_KEY
    )
    
    # Create vector store
    vectorstore = Qdrant(
        client=client,
        collection_name=COLLECTION_NAME,
        embeddings=embeddings,
    )
    
    # Example query
    query = "What are the minimum wage requirements?"
    
    # Search with metadata filter (optional)
    results = vectorstore.similarity_search_with_score(
        query,
        k=5,
        # filter={"level": {"$eq": 2}}  # Only top-level sections
    )
    
    print(f"Query: {query}\n")
    for i, (doc, score) in enumerate(results, 1):
        print(f"{i}. Score: {score:.3f}")
        print(f"   Text: {doc.page_content[:200]}...")
        print(f"   Metadata: {doc.metadata}")
        print()

if __name__ == "__main__":
    main() 