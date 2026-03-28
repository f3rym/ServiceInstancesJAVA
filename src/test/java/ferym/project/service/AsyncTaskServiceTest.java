package ferym.project.service;

import org.junit.jupiter.api.Test;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class AsyncTaskServiceTest {

    private final AsyncTaskService service = new AsyncTaskService();

    @Test
    void getStatus_ShouldReturnNotFound_WhenTaskDoesNotExist() {
        assertThat(service.getStatus("none")).isEqualTo("NOT_FOUND");
    }

    @Test
    void executeLongTask_ShouldFlowThroughRunningToCompleted() throws Exception {
        String taskId = "success-task";

        CompletableFuture<CompletableFuture<String>> futureContainer = CompletableFuture.supplyAsync(() ->
                service.executeLongTask(taskId)
        );

        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(service.getStatus(taskId)).isEqualTo("RUNNING")
        );

        CompletableFuture<String> resultFuture = futureContainer.get();
        resultFuture.get();

        assertThat(service.getStatus(taskId)).isEqualTo("COMPLETED");
    }

    @Test
    void executeLongTask_ShouldHandleInterruption_AndSetErrorStatus() throws Exception {
        String taskId = "interrupted-task";

        Thread thread = new Thread(() -> service.executeLongTask(taskId));

        thread.start();
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(service.getStatus(taskId)).isEqualTo("RUNNING")
        );

        thread.interrupt();
        thread.join(2000);

        assertThat(service.getStatus(taskId)).isEqualTo("ERROR");
    }
}