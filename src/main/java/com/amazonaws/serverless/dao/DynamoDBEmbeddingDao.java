package com.amazonaws.serverless.dao;

import com.amazonaws.serverless.domain.Embedding;
import com.amazonaws.serverless.manager.DynamoDBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.*;
import java.util.stream.Collectors;

public final class DynamoDBEmbeddingDao implements EmbeddingDao {
    
    private static final Logger log = LogManager.getLogger(DynamoDBEmbeddingDao.class);
    
    private static final class InstanceHolder {
        private static final DynamoDBEmbeddingDao INSTANCE = new DynamoDBEmbeddingDao();
    }
    
    private final DynamoDbTable<Embedding> embeddingTable;
    
    private DynamoDBEmbeddingDao() {
        this.embeddingTable = DynamoDBManager.instance().embeddingTable();
    }
    
    public static DynamoDBEmbeddingDao instance() {
        return InstanceHolder.INSTANCE;
    }
    
    @Override
    public List<Embedding> findAllEmbeddings() {
        return embeddingTable.scan(ScanEnhancedRequest.builder().build())
                .items()
                .stream()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Embedding> findEmbeddingsBySource(String source) {
        var queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(source).build()))
                .build();
                
        return embeddingTable.index(Embedding.SOURCE_INDEX)
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Embedding> findEmbeddingById(String id) {
        var key = Key.builder().partitionValue(id).build();
        return Optional.ofNullable(embeddingTable.getItem(key));
    }
    
    @Override
    public void saveOrUpdateEmbedding(Embedding embedding) {
        embeddingTable.putItem(embedding);
    }
    
    @Override
    public void deleteEmbedding(String id) {
        var key = Key.builder().partitionValue(id).build();
        embeddingTable.deleteItem(key);
    }
    
    @Override
    public void deleteEmbeddingsBySource(String source) {
        var embeddings = findEmbeddingsBySource(source);
        embeddings.forEach(embedding -> deleteEmbedding(embedding.getId()));
    }
    
    @Override
    public List<Embedding> findSimilarEmbeddings(List<Double> queryVector, int limit) {
        // Note: DynamoDB doesn't have native vector similarity search
        // This is a basic implementation - for production, consider using vector databases
        var allEmbeddings = findAllEmbeddings();
        
        return allEmbeddings.stream()
                .map(embedding -> new SimilarityResult(embedding, cosineSimilarity(queryVector, embedding.getVector())))
                .sorted((a, b) -> Double.compare(b.similarity, a.similarity))
                .limit(limit)
                .map(result -> result.embedding)
                .collect(Collectors.toList());
    }
    
    private double cosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA.size() != vectorB.size()) return 0.0;
        
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    private static class SimilarityResult {
        final Embedding embedding;
        final double similarity;
        
        SimilarityResult(Embedding embedding, double similarity) {
            this.embedding = embedding;
            this.similarity = similarity;
        }
    }
}