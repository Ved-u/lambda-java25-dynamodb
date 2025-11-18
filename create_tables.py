import boto3
from botocore.exceptions import ClientError

def create_dynamodb_tables(local=True):
    """Create DynamoDB tables for the application"""
    if local:
        # Use DynamoDB Local
        dynamodb = boto3.client(
            'dynamodb',
            endpoint_url='http://localhost:8000',
            region_name='us-east-1',
            aws_access_key_id='fake',
            aws_secret_access_key='fake'
        )
    else:
        # Use AWS DynamoDB
        dynamodb = boto3.client('dynamodb', region_name='us-east-1')
    
    # Create EMBEDDINGS table
    try:
        embeddings_table = dynamodb.create_table(
            TableName='EMBEDDINGS',
            KeySchema=[
                {
                    'AttributeName': 'id',
                    'KeyType': 'HASH'  # Partition key
                }
            ],
            AttributeDefinitions=[
                {
                    'AttributeName': 'id',
                    'AttributeType': 'S'
                },
                {
                    'AttributeName': 'source',
                    'AttributeType': 'S'
                }
            ],
            GlobalSecondaryIndexes=[
                {
                    'IndexName': 'Source-Index',
                    'KeySchema': [
                        {
                            'AttributeName': 'source',
                            'KeyType': 'HASH'
                        }
                    ],
                    'Projection': {
                        'ProjectionType': 'ALL'
                    }
                }
            ],
            BillingMode='PAY_PER_REQUEST'
        )
        print("✅ EMBEDDINGS table created successfully")
    except ClientError as e:
        if e.response['Error']['Code'] == 'ResourceInUseException':
            print("ℹ️ EMBEDDINGS table already exists")
        else:
            print(f"❌ Error creating EMBEDDINGS table: {e}")
    
    # Create EVENT table (if not exists)
    try:
        event_table = dynamodb.create_table(
            TableName='EVENT',
            KeySchema=[
                {
                    'AttributeName': 'homeTeam',
                    'KeyType': 'HASH'  # Partition key
                },
                {
                    'AttributeName': 'eventDate',
                    'KeyType': 'RANGE'  # Sort key
                }
            ],
            AttributeDefinitions=[
                {
                    'AttributeName': 'homeTeam',
                    'AttributeType': 'S'
                },
                {
                    'AttributeName': 'eventDate',
                    'AttributeType': 'N'
                },
                {
                    'AttributeName': 'city',
                    'AttributeType': 'S'
                },
                {
                    'AttributeName': 'awayTeam',
                    'AttributeType': 'S'
                }
            ],
            GlobalSecondaryIndexes=[
                {
                    'IndexName': 'City-Index',
                    'KeySchema': [
                        {
                            'AttributeName': 'city',
                            'KeyType': 'HASH'
                        }
                    ],
                    'Projection': {
                        'ProjectionType': 'ALL'
                    }
                },
                {
                    'IndexName': 'AwayTeam-Index',
                    'KeySchema': [
                        {
                            'AttributeName': 'awayTeam',
                            'KeyType': 'HASH'
                        }
                    ],
                    'Projection': {
                        'ProjectionType': 'ALL'
                    }
                }
            ],
            BillingMode='PAY_PER_REQUEST'
        )
        print("✅ EVENT table created successfully")
    except ClientError as e:
        if e.response['Error']['Code'] == 'ResourceInUseException':
            print("ℹ️ EVENT table already exists")
        else:
            print(f"❌ Error creating EVENT table: {e}")

if __name__ == "__main__":
    create_dynamodb_tables(local=True)  # Set to False for AWS DynamoDB