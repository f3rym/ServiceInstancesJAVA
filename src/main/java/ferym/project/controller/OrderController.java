package ferym.project.controller;

import ferym.project.dto.OrderDto;
import ferym.project.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Заказы")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/bulk")
    public List<OrderDto> createBulk(
            @RequestBody List<OrderDto> dtos,
            @RequestParam(defaultValue = "true") boolean trl) {
        return orderService.createOrdersBulk(dtos, trl);
    }


    @GetMapping
    public List<OrderDto> getAll() {
        return orderService.getAll();
    }

    @PostMapping("/transaction")
    public String transaction(@RequestParam String username, @RequestParam Long instanceId) {
        orderService.createOrderWithNewUser(username, instanceId);
        return "Success!";
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить заказ", description = "Удаляет запись о заказе по его ID")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}