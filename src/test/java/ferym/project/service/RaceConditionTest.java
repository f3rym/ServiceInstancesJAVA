package ferym.project.service;

import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class RaceConditionTest {

    @Test
    @SuppressWarnings("resource")
    void demonstrateRaceCondition() throws InterruptedException {
        StatisticsService service = new StatisticsService();
        int threads = 60;
        int iterationsPerThread = 1000;
        int expectedTotal = threads * iterationsPerThread;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            executor.execute(() -> {
                for (int j = 0; j < iterationsPerThread; j++) {
                    service.incrementSafe();
                    service.incrementUnsafe();
                }
            });
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(30, TimeUnit.SECONDS);

        assertThat(finished).isTrue();

        System.out.println("Safe: " + service.getSafeCount());
        System.out.println("Unsafe: " + service.getUnsafeCount());

        assertThat(service.getSafeCount()).isEqualTo(expectedTotal);
        assertThat(service.getUnsafeCount()).isLessThan(expectedTotal);
    }
}