@echo off
echo Starting DynamoDB Local...

REM Download DynamoDB Local if not exists
if not exist "dynamodb_local" (
    echo Downloading DynamoDB Local...
    curl -L "https://d1ni2b6xgvw0s0.cloudfront.net/v2.x/dynamodb_local_latest.zip" -o "dynamodb_local.zip"
    powershell -Command "Expand-Archive -Path dynamodb_local.zip -DestinationPath dynamodb_local -Force"
    del dynamodb_local.zip
)

REM Start DynamoDB Local
echo Starting DynamoDB Local on port 8000...
java -Djava.library.path=./dynamodb_local/DynamoDBLocal_lib -jar ./dynamodb_local/DynamoDBLocal.jar -sharedDb -port 8000

pause