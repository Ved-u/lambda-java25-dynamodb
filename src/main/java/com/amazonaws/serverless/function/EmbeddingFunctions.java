package com.amazonaws.serverless.function;

import com.amazonaws.serverless.dao.DynamoDBEmbeddingDao;
import com.amazonaws.serverless.domain.Embedding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class EmbeddingFunctions {
    
    private static final Logger log = LogManager.getLogger(EmbeddingFunctions.class);
    private static final DynamoDBEmbeddingDao embeddingDao = DynamoDBEmbeddingDao.instance();
    
    public List<Embedding> getAllEmbeddingsHandler() {
        log.info("GetAllEmbeddings invoked");
        var embeddings = embeddingDao.findAllEmbeddings();
        log.info("Found {} total embeddings", embeddings.size());
        return embeddings;
    }
    
    public List<Embedding> getEmbeddingsBySource(String source) {
        if (source == null || source.isEmpty()) {
            log.error("GetEmbeddingsBySource received null or empty source");
            throw new IllegalArgumentException("Source cannot be null or empty");
        }
        
        log.info("GetEmbeddingsBySource invoked for source = {}", source);
        var embeddings = embeddingDao.findEmbeddingsBySource(source);
        log.info("Found {} embeddings for source = {}", embeddings.size(), source);
        return embeddings;
    }
    
    public List<Embedding> findSimilarEmbeddings(List<Double> queryVector, int limit) {
        if (queryVector == null || queryVector.isEmpty()) {
            log.error("FindSimilarEmbeddings received null or empty query vector");
            throw new IllegalArgumentException("Query vector cannot be null or empty");
        }
        
        log.info("FindSimilarEmbeddings invoked with vector size = {}, limit = {}", queryVector.size(), limit);
        var similarEmbeddings = embeddingDao.findSimilarEmbeddings(queryVector, limit);
        log.info("Found {} similar embeddings", similarEmbeddings.size());
        return similarEmbeddings;
    }
    
    public void saveOrUpdateEmbedding(Embedding embedding) {
        if (embedding == null) {
            log.error("SaveEmbedding received null input");
            throw new IllegalArgumentException("Cannot save null embedding");
        }
        
        log.info("Saving embedding with id = {}", embedding.getId());
        embeddingDao.saveOrUpdateEmbedding(embedding);
        log.info("Successfully saved embedding");
    }
    
    public void deleteEmbedding(String id) {
        if (id == null || id.isEmpty()) {
            log.error("DeleteEmbedding received null or empty id");
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        
        log.info("Deleting embedding with id = {}", id);
        embeddingDao.deleteEmbedding(id);
        log.info("Successfully deleted embedding");
    }
    
    public void deleteEmbeddingsBySource(String source) {
        if (source == null || source.isEmpty()) {
            log.error("DeleteEmbeddingsBySource received null or empty source");
            throw new IllegalArgumentException("Source cannot be null or empty");
        }
        
        log.info("Deleting embeddings for source = {}", source);
        embeddingDao.deleteEmbeddingsBySource(source);
        log.info("Successfully deleted embeddings for source");
    }
}