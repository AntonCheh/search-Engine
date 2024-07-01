package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.Status;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class IndexingServiceImpl implements IndexingService {

    private final SitesList sites;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private volatile boolean isIndexing = false;

    @Autowired
    public IndexingServiceImpl(SitesList sites) {
        this.sites = sites;
    }

    @Override
    public synchronized void startIndexing() {
        if (isIndexing) {
            throw new IllegalStateException("Индексация уже запущена");
        }
        isIndexing = true;
        for (Site site : sites.getSites()) {
            executorService.submit(() -> indexSite(site));
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
        for (Site site : sites.getSites()) {
            if (url.startsWith(site.getUrl())) {
                indexSitePage(site, url);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isIndexing() {
        return isIndexing;
    }

    private void indexSite(Site site) {
        try {
            // Удалить данные по сайту
            deleteSiteData(site);

            // Создать запись со статусом INDEXING
            createSiteRecord(site, Status.INDEXING);

            // Обход страниц
            crawlSite(site);

            // Изменить статус на INDEXED
            updateSiteStatus(site, Status.INDEXED, null);
        } catch (Exception e) {
            updateSiteStatus(site, Status.FAILED, e.getMessage());
        }
    }

    private void indexSitePage(Site site, String url) {
        try {
            // Логика индексации страницы
        } catch (Exception e) {
            updateSiteStatus(site, Status.FAILED, e.getMessage());
        }
    }

    private void deleteSiteData(Site site) {
        // Удаление данных из таблиц site и page
    }

    private void createSiteRecord(Site site, Status status) {
        // Создание новой записи в таблице site
    }

    private void updateSiteStatus(Site site, Status status, String error) {
        // Обновление статуса и времени в таблице site
    }

    private void crawlSite(Site site) {
        // Обход страниц с использованием Fork-Join и JSOUP
    }
}
