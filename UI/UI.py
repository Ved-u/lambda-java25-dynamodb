import streamlit as st
import os
import sys
import json
# Add project root BEFORE importing RAG
ROOT_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(ROOT_DIR)
# Import RAG
from RAG import query_data
from RAG import populate_database
import Send_Email
from MCP.aws_mcp_server import AWSMCPServer

# Global Variable for path to data folder for storing PDFs
PATH = r"C:\Users\vedan\OneDrive\Desktop\Hackathon\lambda-java25-dynamodb\UI\data"

# ---------------------- Session State ----------------------
if "chat_history" not in st.session_state:
    st.session_state.chat_history = []

if "user_email" not in st.session_state:
    st.session_state.user_email = ""

if "email_set" not in st.session_state:
    st.session_state.email_set = False

if "aws_mode" not in st.session_state:
    st.session_state.aws_mode = False

if "mcp_server" not in st.session_state:
    st.session_state.mcp_server = AWSMCPServer()

def handle_message(message: str):
    if not message.strip():
        return

    # For Keeping Chat History 
    st.session_state.chat_history.append(("user", message))

    # Check if AWS mode is enabled
    if st.session_state.aws_mode:
        # Use MCP server for AWS operations
        with st.spinner("Processing AWS operation..."):
            result = st.session_state.mcp_server.process_natural_language(message)
            if result["success"]:
                bot_reply = f"**AWS Operation Completed:**\n\n**Service:** {result['operation'].get('service', 'N/A')}\n**Operation:** {result['operation'].get('operation', 'N/A')}\n**Description:** {result['operation'].get('description', 'N/A')}\n\n**Result:**\n```json\n{json.dumps(result['result'], indent=2)}\n```"
            else:
                bot_reply = f"**AWS Operation Failed:**\n{result.get('message', 'Unknown error')}"
    else:
        # Generate Response through RAG & LLM
        with st.spinner("Generating response..."):
            bot_reply = query_data.query_rag(message)  # This might take time

    # For Keeping Chat History 
    st.session_state.chat_history.append(("BOT:", bot_reply))
   

# ---------------------- Streamlit UI ----------------------
st.set_page_config(layout="wide")
st.title("📑 BOT - Contract Chatbot")

# Sidebar
with st.sidebar:
    # Upload Button 
    st.header("📂 Upload Contract")
    uploaded_pdf = st.file_uploader("Upload PDF", type=["pdf"])
    if uploaded_pdf:
        dest_folder = r""+PATH

        os.makedirs(dest_folder, exist_ok=True)

        # Save uploaded file
        file_name = uploaded_pdf.name
        dest_path = os.path.join(dest_folder, file_name)
        
        # Save uploaded file to dest_path
        with open(dest_path, "wb") as f:
            f.write(uploaded_pdf.read())

        populate_database.load()
        st.success(f"Uploaded: {file_name}")

    # Clear Database button
    if st.button("🧹 Clear DB"):
        # Clear Context by deleting PDFs and DB
        if os.path.exists(PATH):
            for file in os.listdir(PATH):
                if file.lower().endswith(".pdf"):
                    try:
                        os.remove(os.path.join(PATH, file))
                    except Exception as e:
                        print(f"Could not delete {file}: {e}")
        # Clear DB
        populate_database.clear_database_new() 
        st.success("Database cleared successfully!")

    # Email
    if not st.session_state.email_set:
        st.header("📧 Email")
        name_input = st.text_input("Enter your name (one time):")
        email_input = st.text_input("Enter your email (one time):")

        if st.button("Save Email"):
            if email_input:
                st.session_state.user_email = email_input
                st.session_state.email_set = True
                st.success("Email saved!")
                Send_Email.add_contract_and_notify(name_input, email_input)
                st.rerun()
    else:
        st.success(f"Email: {st.session_state.user_email}")

   
# Main chat area
st.subheader("💬 Conversation")
for sender, message in st.session_state.chat_history:
    if sender == "user":
        st.markdown(f"**👤 You:**  **{message}**")
    else:
        st.markdown(f"*🤖 BOT:* {message}")

# AWS Operations Toggle
st.session_state.aws_mode = st.checkbox(
    "🔧 AWS Operations Mode", 
    value=st.session_state.aws_mode,
    help="Enable to perform AWS operations using natural language"
)

if st.session_state.aws_mode:
    st.info("💡 **AWS Mode Active:** Ask me to perform AWS operations like 'list my DynamoDB tables' or 'show EC2 instances'")
else:
    st.info("💡 **RAG Mode Active:** Ask questions about uploaded documents")

# Input form
with st.form(key="message_form", clear_on_submit=True):
    placeholder_text = "Ask about AWS operations..." if st.session_state.aws_mode else "Ask about uploaded documents..."
    user_input = st.text_area("Enter your message:", placeholder=placeholder_text)
    submit_button = st.form_submit_button("Send")
    if submit_button and user_input:
        handle_message(user_input)
        st.rerun()

