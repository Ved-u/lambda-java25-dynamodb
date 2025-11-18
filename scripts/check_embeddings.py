import boto3
import json

def check_dynamodb_embeddings():
    """Check what's stored in DynamoDB Local"""
    
    # Connect to DynamoDB Local
    dynamodb = boto3.resource(
        'dynamodb',
        endpoint_url='http://localhost:8000',
        region_name='us-east-1',
        aws_access_key_id='fake',
        aws_secret_access_key='fake'
    )
    
    table = dynamodb.Table('EMBEDDINGS')
    
    try:
        # Get table info
        response = table.scan()
        items = response['Items']
        
        print(f"📊 Total embeddings stored: {len(items)}")
        
        if items:
            print("\n📄 Sample embedding:")
            sample = items[0]
            print(f"  ID: {sample['id']}")
            print(f"  Source: {sample['source']}")
            print(f"  Content preview: {sample['content'][:100]}...")
            print(f"  Vector size: {len(sample['vector'])}")
            print(f"  Page: {sample.get('page', 'N/A')}")
            
            print(f"\n📁 Sources found:")
            sources = set(item['source'] for item in items)
            for source in sources:
                count = sum(1 for item in items if item['source'] == source)
                print(f"  {source}: {count} chunks")
        else:
            print("❌ No embeddings found in DynamoDB")
            
    except Exception as e:
        print(f"❌ Error accessing DynamoDB: {e}")
        print("Make sure DynamoDB Local is running on port 8000")

if __name__ == "__main__":
    check_dynamodb_embeddings()