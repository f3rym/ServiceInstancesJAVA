package ferym.project.controller;

import ferym.project.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PostMapping("/test-race-condition")
    public String testRace() throws InterruptedException {
        int threads = 60;
        int iterations = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads * iterations; i++) {
            executor.submit(() -> {
                statisticsService.incrementUnsafe();
                statisticsService.incrementSafe();
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        return String.format("Результаты теста: Safe=%d, Unsafe=%d",
                statisticsService.getSafeCount(),
                statisticsService.getUnsafeCount());
    }

    @GetMapping
    public String getStats() {
        return "Safe: " + statisticsService.getSafeCount() +
                " | Unsafe: " + statisticsService.getUnsafeCount();
    }
}