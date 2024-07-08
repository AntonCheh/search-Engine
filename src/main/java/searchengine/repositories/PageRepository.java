package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.config.SiteConfig;
import searchengine.model.Page;
import searchengine.model.SiteTable;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Integer> {
    List<Page> findBySiteTable(SiteTable siteTable);
    List<Page> findByPathContaining(String path);
}


