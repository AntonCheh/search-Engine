package searchengine.services;

public interface IndexingService {
    void startIndexing();
    void stopIndexing();
    boolean indexPage(String url);
    boolean isIndexing();
}
