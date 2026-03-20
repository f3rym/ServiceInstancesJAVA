package ferym.project.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StatisticsService {
    private final AtomicLong safeCounter = new AtomicLong(0);
    private long unsafeCounter = 0;

    public void incrementSafe() {
        safeCounter.incrementAndGet();
    }

    public void incrementUnsafe() {
        unsafeCounter++;
    }

    public long getSafeCount() {
        return safeCounter.get();
    }

    public long getUnsafeCount() {
        return unsafeCounter;
    }
}