package ferym.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticsServiceTest {

    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsService();
    }

    @Test
    void incrementSafe_ShouldIncreaseCounter() {
        statisticsService.incrementSafe();
        statisticsService.incrementSafe();

        assertThat(statisticsService.getSafeCount()).isEqualTo(2);
    }

    @Test
    void incrementUnsafe_ShouldIncreaseCounter() {
        statisticsService.incrementUnsafe();

        assertThat(statisticsService.getUnsafeCount()).isEqualTo(1);
    }
}