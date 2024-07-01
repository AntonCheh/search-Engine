package searchengine.services;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.List;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.util.*;

@Service
public class SearchService {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;

    public SearchService(SiteRepository siteRepository, PageRepository pageRepository, LemmaRepository lemmaRepository, IndexRepository indexRepository) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
    }

    public Map<String, Object> search(String query, String site, int offset, int limit) {
        List<String> lemmas = getLemmasFromQuery(query);
        List<Lemma> foundLemmas = lemmaRepository.findByLemmaIn(lemmas);

        if (foundLemmas.isEmpty()) {
            return Map.of("result", false, "error", "Нет результатов для данного запроса");
        }

        List<Page> pages = findPagesByLemmas(foundLemmas, site, offset, limit);

        List<Map<String, Object>> data = new ArrayList<>();
        for (Page page : pages) {
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("site", page.getSite().getUrl());
            pageData.put("siteName", page.getSite().getName());
            pageData.put("uri", page.getPath());
            pageData.put("title", getTitleFromContent(page.getContent()));
            pageData.put("snippet", getSnippetFromContent(page.getContent(), lemmas));
            pageData.put("relevance", calculateRelevance(page, foundLemmas));
            data.add(pageData);
        }

        return Map.of("result", true, "count", pages.size(), "data", data);
    }

    private List<String> getLemmasFromQuery(String query) {
        // Лемматизация запроса, преобразование запроса в список лемм
        return Arrays.asList(query.toLowerCase().split("\\s+"));
    }

    private List<Page> findPagesByLemmas(List<Lemma> lemmas, String site, int offset, int limit) {
        List<Page> pages = new ArrayList<>();
        List<Index> indexes = indexRepository.findByLemmaIn(lemmas);

        for (Index index : indexes) {
            Page page = index.getPage();
            if (site == null || page.getSite().getUrl().equals(site)) {
                pages.add(page);
            }
        }

        // Удаление дубликатов
        Set<Page> uniquePages = new LinkedHashSet<>(pages);
        pages = new ArrayList<>(uniquePages);

        // Применение offset и limit
        int toIndex = Math.min(offset + limit, pages.size());
        return pages.subList(offset, toIndex);
    }

    private String getTitleFromContent(String content) {
        try {
            Document doc = Jsoup.parse(content);
            return doc.title();
        } catch (Exception e) {
            return "No Title";
        }
    }

    private String getSnippetFromContent(String content, List<String> lemmas) {
        Document doc = Jsoup.parse(content);
        String text = doc.body().text();
        StringBuilder snippet = new StringBuilder();
        int snippetLength = 30;

        for (String lemma : lemmas) {
            int index = text.toLowerCase().indexOf(lemma.toLowerCase());
            if (index != -1) {
                int start = Math.max(0, index - snippetLength / 2);
                int end = Math.min(text.length(), index + snippetLength / 2);
                String snippetPart = text.substring(start, end);
                snippetPart = snippetPart.replaceAll("(?i)" + lemma, "<b>" + lemma + "</b>");
                snippet.append(snippetPart).append("... ");
            }
        }

        if (snippet.length() == 0) {
            snippet.append(text.substring(0, Math.min(snippetLength, text.length()))).append("...");
        }

        return snippet.toString();
    }

    private double calculateRelevance(Page page, List<Lemma> lemmas) {
        double relevance = 0.0;
        List<Index> indices = indexRepository.findByPage(page);
        for (Index index : indices) {
            if (lemmas.contains(index.getLemma().getLemma())) {
                relevance += index.getRank();
            }
        }
        return relevance / lemmas.size();
    }
}