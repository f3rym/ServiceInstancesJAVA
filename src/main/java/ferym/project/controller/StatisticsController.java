package ferym.project.controller;

import ferym.project.service.StatisticsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/api/v1/stats")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final ExecutorService taskExecutor;

    public StatisticsController(
            StatisticsService statisticsService,
            @Qualifier("myTaskExecutor") ExecutorService taskExecutor) {
        this.statisticsService = statisticsService;
        this.taskExecutor = taskExecutor;
    }

    @PostMapping("/test-race-condition")
    public CompletableFuture<String> testRace() {
        statisticsService.reset();

        int threads = 60;
        int iterations = 1000;
        int total = threads * iterations;

        CompletableFuture<?>[] futures = new CompletableFuture[total];

        for (int i = 0; i < total; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                statisticsService.incrementUnsafe();
                statisticsService.incrementSafe();
            }, taskExecutor);
        }

        return CompletableFuture.allOf(futures)
                .thenApply(_ -> String.format(
                        "Результаты теста: Safe=%d, Unsafe=%d",
                        statisticsService.getSafeCount(),
                        statisticsService.getUnsafeCount()
                ));
    }

    @GetMapping
    public String getStats() {
        return "Safe: " + statisticsService.getSafeCount() + " | Unsafe: " + statisticsService.getUnsafeCount();
    }
}