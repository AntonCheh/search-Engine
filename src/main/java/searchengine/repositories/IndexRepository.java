package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;

import java.util.List;

public interface IndexRepository extends JpaRepository<Index, Integer> {
    List<Index> findByPage(Page page);
    List<Index> findByLemmaIn(List<Lemma> lemmas);
}