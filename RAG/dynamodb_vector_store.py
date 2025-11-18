import boto3
import json
import uuid
from typing import List, Dict, Any, Optional, Tuple
from langchain_core.documents import Document
from langchain_core.embeddings import Embeddings
import numpy as np
from decimal import Decimal

class DynamoDBVectorStore:
    """DynamoDB-based vector store for RAG system"""
    
    def __init__(self, table_name: str = "EMBEDDINGS", local: bool = True):
        self.table_name = table_name
        
        if local:
            # Use DynamoDB Local
            self.dynamodb = boto3.resource(
                'dynamodb',
                endpoint_url='http://localhost:8000',
                region_name='us-east-1',
                aws_access_key_id='fake',
                aws_secret_access_key='fake'
            )
        else:
            # Use AWS DynamoDB
            self.dynamodb = boto3.resource('dynamodb', region_name='us-east-1')
            
        self.table = self.dynamodb.Table(table_name)
        
    def add_documents(self, documents: List[Document], embeddings: List[List[float]], ids: Optional[List[str]] = None) -> List[str]:
        """Add documents with their embeddings to DynamoDB"""
        if ids is None:
            ids = [str(uuid.uuid4()) for _ in documents]
            
        for i, (doc, embedding, doc_id) in enumerate(zip(documents, embeddings, ids)):
            item = {
                'id': doc_id,
                'content': doc.page_content,
                'vector': [Decimal(str(x)) for x in embedding],
                'source': doc.metadata.get('source', ''),
                'page': doc.metadata.get('page', 0),
                'metadata': json.dumps(doc.metadata)
            }
            
            try:
                self.table.put_item(Item=item)
            except Exception as e:
                print(f"Error adding document {doc_id}: {e}")
                
        return ids
    
    def similarity_search_with_score(self, query_embedding: List[float], k: int = 5) -> List[Tuple[Document, float]]:
        """Find similar documents using cosine similarity"""
        try:
            # Scan all items (Note: This is not efficient for large datasets)
            response = self.table.scan()
            items = response['Items']
            
            # Calculate similarities
            similarities = []
            for item in items:
                # Convert Decimal back to float for similarity calculation
                vector = [float(x) for x in item['vector']]
                similarity = self._cosine_similarity(query_embedding, vector)
                doc = Document(
                    page_content=item['content'],
                    metadata=json.loads(item.get('metadata', '{}'))
                )
                similarities.append((doc, similarity))
            
            # Sort by similarity and return top k
            similarities.sort(key=lambda x: x[1], reverse=True)
            return similarities[:k]
            
        except Exception as e:
            print(f"Error in similarity search: {e}")
            return []
    
    def similarity_search(self, query_embedding: List[float], k: int = 5) -> List[Document]:
        """Find similar documents without scores"""
        results = self.similarity_search_with_score(query_embedding, k)
        return [doc for doc, _ in results]
    
    def delete_by_source(self, source: str):
        """Delete all embeddings from a specific source"""
        try:
            # Query by source using GSI
            response = self.table.query(
                IndexName='Source-Index',
                KeyConditionExpression=boto3.dynamodb.conditions.Key('source').eq(source)
            )
            
            # Delete each item
            for item in response['Items']:
                self.table.delete_item(Key={'id': item['id']})
                
        except Exception as e:
            print(f"Error deleting by source {source}: {e}")
    
    def clear_all(self):
        """Clear all embeddings from the table"""
        try:
            response = self.table.scan()
            for item in response['Items']:
                self.table.delete_item(Key={'id': item['id']})
        except Exception as e:
            print(f"Error clearing table: {e}")
    
    def get_existing_ids(self) -> List[str]:
        """Get all existing document IDs"""
        try:
            response = self.table.scan(ProjectionExpression='id')
            return [item['id'] for item in response['Items']]
        except Exception as e:
            print(f"Error getting existing IDs: {e}")
            return []
    
    def _cosine_similarity(self, vec1: List[float], vec2: List[float]) -> float:
        """Calculate cosine similarity between two vectors"""
        try:
            vec1 = np.array(vec1)
            vec2 = np.array(vec2)
            
            dot_product = np.dot(vec1, vec2)
            norm1 = np.linalg.norm(vec1)
            norm2 = np.linalg.norm(vec2)
            
            if norm1 == 0 or norm2 == 0:
                return 0.0
                
            return dot_product / (norm1 * norm2)
        except Exception as e:
            print(f"Error calculating cosine similarity: {e}")
            return 0.0