package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.config.Site;
import searchengine.model.Lemma;

import java.util.List;

public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    List<Lemma> findByLemmaIn(List<String> lemmas);
    List<Lemma> findBySiteAndLemmaIn(Site site, List<String> lemmas);
}