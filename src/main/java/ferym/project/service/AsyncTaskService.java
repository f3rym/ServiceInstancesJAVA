package ferym.project.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AsyncTaskService {
    private final Map<String, String> taskStorage = new ConcurrentHashMap<>();

    @Async
    public CompletableFuture<String> executeLongTask(String taskId) {
        taskStorage.put(taskId, "RUNNING");
        try {
            log.info("Starting async task with ID: {}", taskId);
            Thread.sleep(15000);
            taskStorage.put(taskId, "COMPLETED");
            log.info("Task {} finished successfully", taskId);
        } catch (InterruptedException e) {
            taskStorage.put(taskId, "ERROR");
            log.error("Task {} interrupted", taskId);
            Thread.currentThread().interrupt();
        }
        return CompletableFuture.completedFuture(taskId);
    }

    public String getStatus(String taskId) {
        return taskStorage.getOrDefault(taskId, "NOT_FOUND");
    }
}