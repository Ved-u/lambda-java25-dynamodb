# DynamoDB RAG System

## Project Structure

```
lambda-java25-dynamodb/
├── src/main/java/          # Java Lambda functions
├── RAG/                    # Python RAG system
├── UI/                     # Streamlit interface
├── scripts/                # Setup & utility scripts
├── docs/                   # Documentation
├── config/                 # Configuration files
└── pom.xml                 # Maven configuration
```

## Quick Start

1. **Start Local DynamoDB**:
   ```bash
   scripts/start_local_db.bat
   ```

2. **Create Tables**:
   ```bash
   python scripts/create_tables.py
   ```

3. **Run UI**:
   ```bash
   cd UI && streamlit run UI.py
   ```

4. **Check Embeddings**:
   ```bash
   python scripts/check_embeddings.py
   ```

## Features

- ✅ Local DynamoDB (no AWS costs)
- ✅ PDF upload & processing
- ✅ Vector embeddings storage
- ✅ RAG with Llama3.1
- ✅ Streamlit UI