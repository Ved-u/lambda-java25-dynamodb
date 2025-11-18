# Local DynamoDB Setup Guide

## Quick Start (3 Steps)

### 1. Start DynamoDB Local
```bash
# Option A: Use batch file (Windows)
start_local_db.bat

# Option B: Manual command
java -Djava.library.path=./dynamodb_local/DynamoDBLocal_lib -jar ./dynamodb_local/DynamoDBLocal.jar -sharedDb -port 8000
```

### 2. Create Tables
```bash
python create_tables.py
```

### 3. Run UI
```bash
cd UI
streamlit run UI.py
```

## What This Does

- ✅ **No AWS Credits Used** - Everything runs locally
- ✅ **No AWS Credentials Needed** - Uses fake credentials
- ✅ **Same Functionality** - Upload PDFs, ask questions
- ✅ **Fast Testing** - No network latency

## Files Modified

- `dynamodb_vector_store.py` - Uses `localhost:8000` by default
- `create_tables.py` - Creates tables in local DynamoDB
- `start_local_db.bat` - Easy startup script

## Switching to AWS

To use AWS DynamoDB instead:

```python
# In dynamodb_vector_store.py
db = DynamoDBVectorStore(local=False)

# In create_tables.py
create_dynamodb_tables(local=False)
```

## Requirements

- Java 8+ (for DynamoDB Local)
- Internet connection (first time download only)

Your PDFs and embeddings now stay completely local!