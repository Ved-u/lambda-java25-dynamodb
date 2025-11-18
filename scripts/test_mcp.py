import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from MCP.aws_mcp_server import AWSMCPServer

def test_mcp_server():
    """Test MCP server with sample commands"""
    
    print("🚀 Testing MCP Server...")
    server = AWSMCPServer()
    
    test_commands = [
        "List all DynamoDB tables",
        "Show me EC2 instances", 
        "List Lambda functions",
        "Describe EMBEDDINGS table"
    ]
    
    for command in test_commands:
        print(f"\n📝 Testing: {command}")
        print("-" * 50)
        
        result = server.process_natural_language(command)
        
        if result["success"]:
            print(f"✅ Success!")
            print(f"Service: {result['operation'].get('service')}")
            print(f"Operation: {result['operation'].get('operation')}")
            print(f"Result: {result['result']}")
        else:
            print(f"❌ Failed: {result.get('message')}")
            print(f"Error: {result.get('error')}")

if __name__ == "__main__":
    test_mcp_server()