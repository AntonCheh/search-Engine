package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.config.Site;
import searchengine.model.Page;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Integer> {
    List<Page> findBySite(Site site);
    List<Page> findByPathContaining(String path);
}
