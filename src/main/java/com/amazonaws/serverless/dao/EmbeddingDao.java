package com.amazonaws.serverless.dao;

import com.amazonaws.serverless.domain.Embedding;
import java.util.List;
import java.util.Optional;

public interface EmbeddingDao {
    
    List<Embedding> findAllEmbeddings();
    
    List<Embedding> findEmbeddingsBySource(String source);
    
    Optional<Embedding> findEmbeddingById(String id);
    
    void saveOrUpdateEmbedding(Embedding embedding);
    
    void deleteEmbedding(String id);
    
    void deleteEmbeddingsBySource(String source);
    
    List<Embedding> findSimilarEmbeddings(List<Double> queryVector, int limit);
}