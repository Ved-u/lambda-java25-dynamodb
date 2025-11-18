import json
import boto3
from typing import Dict, Any
from langchain_community.llms.ollama import Ollama

class AWSMCPServer:
    """MCP Server for AWS operations using natural language"""
    
    def __init__(self):
        self.llm = Ollama(model="llama3.1")
        self.aws_clients = {}
        
    def get_aws_client(self, service: str):
        """Get AWS client for service"""
        if service not in self.aws_clients:
            self.aws_clients[service] = boto3.client(service, region_name='us-east-1')
        return self.aws_clients[service]
    
    def process_natural_language(self, user_input: str) -> Dict[str, Any]:
        """Process natural language input and execute AWS operations"""
        
        # Create prompt for LLM to understand AWS operations
        prompt = f"""
        You are an AWS operations assistant. Analyze this request and respond with a JSON object containing:
        - "service": AWS service name (dynamodb, ec2, lambda, s3, etc.)
        - "operation": specific operation (list_tables, describe_instances, list_functions, etc.)
        - "parameters": any parameters needed
        - "description": what you're going to do
        
        User request: {user_input}
        
        Respond ONLY with valid JSON, no other text.
        """
        
        try:
            response = self.llm.invoke(prompt)
            # Parse LLM response as JSON
            operation_data = json.loads(response.strip())
            
            # Execute the AWS operation
            result = self.execute_aws_operation(operation_data)
            return {
                "success": True,
                "operation": operation_data,
                "result": result
            }
            
        except Exception as e:
            return {
                "success": False,
                "error": str(e),
                "message": "Failed to process request"
            }
    
    def execute_aws_operation(self, operation_data: Dict[str, Any]) -> Any:
        """Execute AWS operation based on parsed data"""
        service = operation_data.get("service", "").lower()
        operation = operation_data.get("operation", "")
        parameters = operation_data.get("parameters", {})
        
        try:
            if service == "dynamodb":
                return self.handle_dynamodb_operation(operation, parameters)
            elif service == "ec2":
                return self.handle_ec2_operation(operation, parameters)
            elif service == "lambda":
                return self.handle_lambda_operation(operation, parameters)
            else:
                return f"Service '{service}' not supported yet"
                
        except Exception as e:
            return f"Error executing {service} operation: {str(e)}"
    
    def handle_dynamodb_operation(self, operation: str, parameters: Dict) -> Any:
        """Handle DynamoDB operations"""
        client = self.get_aws_client('dynamodb')
        
        if operation == "list_tables":
            response = client.list_tables()
            return response['TableNames']
        elif operation == "describe_table":
            table_name = parameters.get('table_name', 'EMBEDDINGS')
            response = client.describe_table(TableName=table_name)
            return response['Table']
        elif operation == "scan_table":
            table_name = parameters.get('table_name', 'EMBEDDINGS')
            response = client.scan(TableName=table_name, Limit=5)
            return response['Items']
        else:
            return f"DynamoDB operation '{operation}' not implemented"
    
    def handle_ec2_operation(self, operation: str, parameters: Dict) -> Any:
        """Handle EC2 operations"""
        client = self.get_aws_client('ec2')
        
        if operation == "describe_instances":
            response = client.describe_instances()
            instances = []
            for reservation in response['Reservations']:
                for instance in reservation['Instances']:
                    instances.append({
                        'InstanceId': instance['InstanceId'],
                        'State': instance['State']['Name'],
                        'InstanceType': instance['InstanceType']
                    })
            return instances
        else:
            return f"EC2 operation '{operation}' not implemented"
    
    def handle_lambda_operation(self, operation: str, parameters: Dict) -> Any:
        """Handle Lambda operations"""
        client = self.get_aws_client('lambda')
        
        if operation == "list_functions":
            response = client.list_functions()
            return [func['FunctionName'] for func in response['Functions']]
        else:
            return f"Lambda operation '{operation}' not implemented"