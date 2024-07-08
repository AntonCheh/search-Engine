package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.config.SiteConfig;
import searchengine.model.SiteTable;

public interface SiteRepository extends JpaRepository<SiteTable, Integer> {
}
