import subprocess
import sys
import os
import time
import requests

def download_dynamodb_local():
    """Download DynamoDB Local"""
    url = "https://d1ni2b6xgvw0s0.cloudfront.net/v2.x/dynamodb_local_latest.zip"
    
    print("📥 Downloading DynamoDB Local...")
    subprocess.run([
        "curl", "-L", url, "-o", "dynamodb_local.zip"
    ], check=True)
    
    print("📦 Extracting DynamoDB Local...")
    subprocess.run([
        "powershell", "-Command", 
        "Expand-Archive -Path dynamodb_local.zip -DestinationPath dynamodb_local -Force"
    ], check=True)
    
    print("✅ DynamoDB Local downloaded and extracted")

def start_dynamodb_local():
    """Start DynamoDB Local"""
    if not os.path.exists("dynamodb_local"):
        download_dynamodb_local()
    
    print("🚀 Starting DynamoDB Local on port 8000...")
    
    # Start DynamoDB Local in background
    process = subprocess.Popen([
        "java", "-Djava.library.path=./dynamodb_local/DynamoDBLocal_lib",
        "-jar", "./dynamodb_local/DynamoDBLocal.jar",
        "-sharedDb", "-port", "8000"
    ])
    
    # Wait for DynamoDB Local to start
    time.sleep(3)
    
    try:
        response = requests.get("http://localhost:8000")
        print("✅ DynamoDB Local is running on http://localhost:8000")
        return process
    except:
        print("❌ Failed to start DynamoDB Local")
        return None

if __name__ == "__main__":
    start_dynamodb_local()
    print("Press Ctrl+C to stop DynamoDB Local")
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("\n🛑 Stopping DynamoDB Local")