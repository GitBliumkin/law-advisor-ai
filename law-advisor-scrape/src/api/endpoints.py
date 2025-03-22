"""
FastAPI endpoints for the legislative crawler.
"""
import asyncio
import json
from typing import Dict, Any, List, Optional, Union

from fastapi import FastAPI, HTTPException, BackgroundTasks
from pydantic import BaseModel
import aiokafka

from ..config.crawl_config import (
    get_link_by_identifier,
    get_links_by_province,
    get_province_settings,
    API_SETTINGS,
    KAFKA_SETTINGS
)
from ..utils.helpers import logger
from ..cli.interface import crawl_identifier, crawl_province

app = FastAPI(title="Legislative Crawler API")

# Pydantic models for request/response
class CrawlRequest(BaseModel):
    """Request model for crawling a law or province."""
    identifier: str

class CrawlResponse(BaseModel):
    """Response model for crawling results."""
    task_id: str
    status: str
    message: str

class CrawlResult(BaseModel):
    """Model for crawling results."""
    url_identifier: str
    url: str
    law_name: str
    scrape_date: str
    scrape_time: str
    status: str
    markdown: str

# Kafka producer
producer = None

async def initialize_kafka():
    """Initialize Kafka producer."""
    global producer
    try:
        producer = aiokafka.AIOKafkaProducer(
            bootstrap_servers=KAFKA_SETTINGS["bootstrap_servers"]
        )
        await producer.start()
        logger.info("Kafka producer initialized")
    except Exception as e:
        logger.error(f"Error initializing Kafka producer: {e}")
        producer = None

async def send_to_kafka(topic: str, data: Dict[str, Any]):
    """
    Send data to Kafka topic.
    
    Args:
        topic (str): Kafka topic
        data (Dict[str, Any]): Data to send
    """
    if producer:
        try:
            await producer.send_and_wait(
                topic,
                json.dumps(data).encode("utf-8")
            )
            logger.info(f"Data sent to Kafka topic: {topic}")
        except Exception as e:
            logger.error(f"Error sending data to Kafka: {e}")

async def crawl_task(identifier: str, task_id: str):
    """
    Background task for crawling.
    
    Args:
        identifier (str): Law or province identifier
        task_id (str): Task ID
    """
    try:
        # Check if identifier is a province or a specific law
        if len(identifier) <= 3:  # Province code (e.g., FED, ONT)
            results = await crawl_province(identifier)
            
            # Send results to Kafka
            for result in results:
                await send_to_kafka(
                    f"{KAFKA_SETTINGS['topic']}.{identifier.lower()}",
                    {
                        "task_id": task_id,
                        "result": result
                    }
                )
            
            # Send completion message
            await send_to_kafka(
                f"{KAFKA_SETTINGS['topic']}.tasks",
                {
                    "task_id": task_id,
                    "status": "completed",
                    "message": f"Crawled {len(results)} laws for {identifier}"
                }
            )
            
        else:  # Specific law identifier (e.g., FED_CLC)
            result = await crawl_identifier(identifier)
            
            # Send result to Kafka
            await send_to_kafka(
                f"{KAFKA_SETTINGS['topic']}.{identifier.lower()}",
                {
                    "task_id": task_id,
                    "result": result
                }
            )
            
            # Send completion message
            await send_to_kafka(
                f"{KAFKA_SETTINGS['topic']}.tasks",
                {
                    "task_id": task_id,
                    "status": "completed",
                    "message": f"Crawled {result['law_name']}"
                }
            )
            
    except Exception as e:
        logger.error(f"Error in crawl task {task_id}: {e}")
        
        # Send error message
        await send_to_kafka(
            f"{KAFKA_SETTINGS['topic']}.tasks",
            {
                "task_id": task_id,
                "status": "error",
                "message": str(e)
            }
        )

@app.on_event("startup")
async def startup_event():
    """Initialize Kafka on startup."""
    await initialize_kafka()

@app.on_event("shutdown")
async def shutdown_event():
    """Stop Kafka producer on shutdown."""
    if producer:
        await producer.stop()
        logger.info("Kafka producer stopped")

@app.post("/crawl", response_model=CrawlResponse)
async def crawl_endpoint(
    request: CrawlRequest,
    background_tasks: BackgroundTasks
):
    """
    Crawl a specific law or all laws for a province.
    
    Args:
        request (CrawlRequest): Crawl request
        background_tasks (BackgroundTasks): Background tasks
        
    Returns:
        CrawlResponse: Crawl response
    """
    identifier = request.identifier
    
    # Generate task ID
    import uuid
    task_id = str(uuid.uuid4())
    
    try:
        # Validate identifier
        if len(identifier) <= 3:  # Province code
            if not get_links_by_province(identifier):
                raise HTTPException(
                    status_code=404,
                    detail=f"No links found for province: {identifier}"
                )
        else:  # Law identifier
            if not get_link_by_identifier(identifier):
                raise HTTPException(
                    status_code=404,
                    detail=f"Unknown identifier: {identifier}"
                )
        
        # Add task to background tasks
        background_tasks.add_task(crawl_task, identifier, task_id)
        
        return CrawlResponse(
            task_id=task_id,
            status="pending",
            message=f"Crawling task for {identifier} started"
        )
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error starting crawl task: {e}")
        raise HTTPException(
            status_code=500,
            detail=f"Error starting crawl task: {str(e)}"
        )

@app.get("/crawl/{task_id}", response_model=CrawlResponse)
async def get_task_status(task_id: str):
    """
    Get task status.
    
    Args:
        task_id (str): Task ID
        
    Returns:
        CrawlResponse: Task status
    """
    # In a real implementation, this would check a database or Kafka for task status
    # For now, we'll just return a placeholder
    return CrawlResponse(
        task_id=task_id,
        status="unknown",
        message="Task status checking not implemented yet"
    )

@app.get("/health")
async def health_check():
    """
    Health check endpoint.
    
    Returns:
        Dict[str, str]: Health status
    """
    return {"status": "healthy"}
