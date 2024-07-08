package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.StatisticsService;
import searchengine.services.IndexingService;
import searchengine.services.SearchService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final IndexingService indexingService;
    private final SearchService searchService;

    public ApiController(StatisticsService statisticsService, IndexingService indexingService, SearchService searchService) {

        this.statisticsService = statisticsService;
        this.indexingService = indexingService;
        this.searchService = searchService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<Map<String, Object>> startIndexing() {
        if (indexingService.isIndexing()) {
            return ResponseEntity.ok(Map.of("result", false, "error", "Индексация уже запущена"));
        }
        indexingService.startIndexing();
        return ResponseEntity.ok(Map.of("result", true));
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Map<String, Object>> stopIndexing() {
        if (!indexingService.isIndexing()) {
            return ResponseEntity.ok(Map.of("result", false, "error", "Индексация не запущена"));
        }
        indexingService.stopIndexing();
        return ResponseEntity.ok(Map.of("result", true));
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Map<String, Object>> indexPage(@RequestParam String url) {
        boolean result = indexingService.indexPage(url);
        if (!result) {
            return ResponseEntity.ok(Map.of("result", false, "error", "Данная страница находится за пределами сайтов, указанных в конфигурационном файле"));
        }
        return ResponseEntity.ok(Map.of("result", true));
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String query,
            @RequestParam(required = false) String site,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        if (query == null || query.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("result", false, "error", "Задан пустой поисковый запрос"));
        }

        Map<String, Object> searchResults = searchService.search(query, site, offset, limit);
        return ResponseEntity.ok(searchResults);
    }

}
