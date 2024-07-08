package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.config.SiteConfig;
import searchengine.model.Lemma;
import searchengine.model.SiteTable;

import java.util.List;

public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    List<Lemma> findByLemmaIn(List<String> lemmas);
    List<Lemma> findBySiteTableAndLemmaIn(SiteTable siteTable, List<String> lemmas);
}