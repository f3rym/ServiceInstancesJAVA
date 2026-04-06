package ferym.project.controller;

import ferym.project.service.AsyncTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class AsyncController {

    private final AsyncTaskService asyncTaskService;

    @PostMapping("/deploy")
    public String startTask(@RequestParam(required = false) String manualId) {

        String taskId = (manualId != null && !manualId.isEmpty())
                ? manualId
                : UUID.randomUUID().toString();

        asyncTaskService.executeLongTask(taskId)
                .thenAccept(id -> log.info("Task {} обработана асинхронно.", id));

        return taskId;
    }
    @GetMapping("/{taskId}")
    public String checkStatus(@PathVariable String taskId) {
        return asyncTaskService.getStatus(taskId);
    }
}