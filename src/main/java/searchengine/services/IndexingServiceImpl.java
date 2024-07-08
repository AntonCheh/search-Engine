package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.SiteConfig;
import searchengine.config.SitesList;
import searchengine.model.Status;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class IndexingServiceImpl implements IndexingService {

    private final SitesList sitesList;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private volatile boolean isIndexing = false;

    @Autowired
    public IndexingServiceImpl(SitesList sitesList) {
        this.sitesList = sitesList;
    }

    @Override
    public synchronized void startIndexing() {
        if (isIndexing) {
            throw new IllegalStateException("Индексация уже запущена");
        }
        isIndexing = true;
        for (SiteConfig siteConfig : sitesList.getSiteConfigs()) {
            executorService.submit(() -> indexSite(siteConfig));
        }
    }

    @Override
    public synchronized void stopIndexing() {
        if (!isIndexing) {
            throw new IllegalStateException("Индексация не запущена");
        }
        executorService.shutdownNow();
        isIndexing = false;
    }

    @Override
    public boolean indexPage(String url) {
        for (SiteConfig siteConfig : sitesList.getSiteConfigs()) {
            if (url.startsWith(siteConfig.getUrl())) {
                indexSitePage(siteConfig, url);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isIndexing() {
        return isIndexing;
    }

    private void indexSite(SiteConfig siteConfig) {
        try {
            // Удалить данные по сайту
            deleteSiteData(siteConfig);

            // Создать запись со статусом INDEXING
            createSiteRecord(siteConfig, Status.INDEXING);

            // Обход страниц
            crawlSite(siteConfig);

            // Изменить статус на INDEXED
            updateSiteStatus(siteConfig, Status.INDEXED, null);
        } catch (Exception e) {
            updateSiteStatus(siteConfig, Status.FAILED, e.getMessage());
        }
    }

    private void indexSitePage(SiteConfig siteConfig, String url) {
        try {
            // Логика индексации страницы
        } catch (Exception e) {
            updateSiteStatus(siteConfig, Status.FAILED, e.getMessage());
        }
    }

    private void deleteSiteData(SiteConfig siteConfig) {
        // Удаление данных из таблиц site и page
    }

    private void createSiteRecord(SiteConfig siteConfig, Status status) {
        // Создание новой записи в таблице site
    }

    private void updateSiteStatus(SiteConfig siteConfig, Status status, String error) {
        // Обновление статуса и времени в таблице site
    }

    private void crawlSite(SiteConfig siteConfig) {
        // Обход страниц с использованием Fork-Join и JSOUP
    }
}
