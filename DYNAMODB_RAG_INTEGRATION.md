# DynamoDB RAG Integration Guide

## Overview
Successfully integrated DynamoDB as the vector store for your RAG system, replacing ChromaDB. The system now stores embeddings in DynamoDB while maintaining all RAG functionality.

## Architecture

### Java Components (Lambda Functions)
- **Embedding Domain Model**: `Embedding.java` - Stores vector embeddings with metadata
- **EmbeddingDao**: Interface and DynamoDB implementation for vector operations
- **EmbeddingFunctions**: Lambda handlers for embedding CRUD operations
- **DynamoDBManager**: Enhanced to support both Event and Embedding tables

### Python Components (RAG System)
- **DynamoDBVectorStore**: Custom vector store implementation for DynamoDB
- **Updated populate_database.py**: Now uses DynamoDB instead of ChromaDB
- **Updated query_data.py**: Queries DynamoDB for similarity search
- **create_tables.py**: Script to create required DynamoDB tables

## Setup Instructions

### 1. Create DynamoDB Tables
```bash
python create_tables.py
```

### 2. Install Python Dependencies
```bash
pip install -r RAG/requirements.txt
```

### 3. Configure AWS Credentials
Ensure AWS credentials are configured for DynamoDB access:
```bash
aws configure
```

### 4. Build Java Components
```bash
mvn clean package
```

## Usage

### Upload and Process Documents
```python
# In UI or directly
from RAG import populate_database
populate_database.load()  # Processes PDFs and stores in DynamoDB
```

### Query RAG System
```python
from RAG import query_data
response = query_data.query_rag("Your question here")
```

### Clear Database
```python
from RAG import populate_database
populate_database.clear_database_new()  # Clears DynamoDB
```

## DynamoDB Schema

### EMBEDDINGS Table
- **Partition Key**: `id` (String) - Unique document chunk ID
- **Attributes**:
  - `content` (String) - Document text content
  - `vector` (List<Number>) - Embedding vector
  - `source` (String) - Source file path
  - `page` (Number) - Page number
  - `metadata` (String) - JSON metadata
- **GSI**: `Source-Index` on `source` attribute

### EVENT Table (Existing)
- Maintains original schema for events

## Key Features

### Vector Similarity Search
- Cosine similarity calculation in DynamoDB
- Configurable result limit (k parameter)
- Metadata preservation

### Efficient Storage
- JSON metadata serialization
- Optimized for document chunks
- Source-based indexing for fast retrieval

### Integration Points
- **UI**: Streamlit interface unchanged
- **RAG**: Transparent ChromaDB replacement
- **Lambda**: Java functions for advanced operations

## Performance Considerations

### Limitations
- DynamoDB scan operations for similarity search (not optimal for large datasets)
- Consider Amazon OpenSearch for production vector search

### Optimizations
- Batch operations for bulk inserts
- Parallel processing for embeddings
- Caching for frequently accessed vectors

## Migration Benefits

1. **Serverless**: No infrastructure management
2. **Scalable**: Auto-scaling with demand
3. **Integrated**: Single AWS ecosystem
4. **Cost-Effective**: Pay-per-request pricing
5. **Durable**: Built-in backup and recovery

## Next Steps

### For Production
1. Consider Amazon OpenSearch Service for vector search
2. Implement batch processing for large documents
3. Add monitoring and logging
4. Optimize similarity search algorithms

### Current Functionality
- ✅ PDF upload and processing
- ✅ Vector embedding storage
- ✅ Similarity search
- ✅ RAG query processing
- ✅ Streamlit UI integration
- ✅ Database clearing

The system now successfully uses DynamoDB as the vector store while maintaining all original RAG functionality!