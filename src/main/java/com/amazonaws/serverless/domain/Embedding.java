package com.amazonaws.serverless.domain;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import java.util.List;

@DynamoDbBean
@DynamoDbTable(tableName = "EMBEDDINGS")
public class Embedding {
    
    public static final String TABLE_NAME = "EMBEDDINGS";
    public static final String SOURCE_INDEX = "Source-Index";
    
    private String id;
    private String content;
    private List<Double> vector;
    private String source;
    private Integer page;
    private String metadata;
    
    public Embedding() {}
    
    public Embedding(String id, String content, List<Double> vector, String source, Integer page, String metadata) {
        this.id = id;
        this.content = content;
        this.vector = vector;
        this.source = source;
        this.page = page;
        this.metadata = metadata;
    }
    
    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    @DynamoDbAttribute("content")
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    @DynamoDbAttribute("vector")
    public List<Double> getVector() { return vector; }
    public void setVector(List<Double> vector) { this.vector = vector; }
    
    @DynamoDbSecondaryPartitionKey(indexNames = {SOURCE_INDEX})
    @DynamoDbAttribute("source")
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    @DynamoDbAttribute("page")
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    
    @DynamoDbAttribute("metadata")
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}