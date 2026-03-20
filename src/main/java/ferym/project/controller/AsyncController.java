package ferym.project.controller;

import ferym.project.service.AsyncTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class AsyncController {

    private final AsyncTaskService asyncTaskService;

    @PostMapping("/deploy")
    public String startTask() {
        String taskId = UUID.randomUUID().toString();
        asyncTaskService.executeLongTask(taskId)
                .thenAccept(id -> System.out.println("Task " + id + " успешно завершена асинхронно."));
        return taskId;
    }

    @GetMapping("/{taskId}")
    public String checkStatus(@PathVariable String taskId) {
        return asyncTaskService.getStatus(taskId);
    }
}