// Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License"). You may
// not use this file except in compliance with the License. A copy of the
// License is located at
//
//	  http://aws.amazon.com/apache2.0/
//
// or in the "license" file accompanying this file. This file is distributed
// on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
// express or implied. See the License for the specific language governing
// permissions and limitations under the License.


package com.amazonaws.serverless.manager;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import com.amazonaws.serverless.domain.Event;
import com.amazonaws.serverless.domain.Embedding;

public final class DynamoDBManager {
    
    private static final class InstanceHolder {
        private static final DynamoDBManager INSTANCE = new DynamoDBManager();
    }
    
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Event> eventTable;
    private final DynamoDbTable<Embedding> embeddingTable;
    
    private DynamoDBManager() {
        var dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
        
        this.enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
                
        this.eventTable = enhancedClient.table(Event.TABLE_NAME, TableSchema.fromBean(Event.class));
        this.embeddingTable = enhancedClient.table(Embedding.TABLE_NAME, TableSchema.fromBean(Embedding.class));
    }
    
    public static DynamoDBManager instance() {
        return InstanceHolder.INSTANCE;
    }
    
    public DynamoDbEnhancedClient enhancedClient() {
        return enhancedClient;
    }
    
    public DynamoDbTable<Event> eventTable() {
        return eventTable;
    }
    
    public DynamoDbTable<Embedding> embeddingTable() {
        return embeddingTable;
    }
}
