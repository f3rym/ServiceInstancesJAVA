package ferym.project.controller;

import ferym.project.dto.OrderDto;
import ferym.project.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Заказы", description = "Операции по оформлению аренды серверов")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Список всех заказов")
    public List<OrderDto> getAll() {
        return orderService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать заказ")
    public OrderDto create(@Valid @RequestBody OrderDto dto) {
        return orderService.create(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отменить заказ")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }

    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Транзакционное создание", description = "Создает пользователя и заказ в одной транзакции")
    public String transaction(@RequestParam String username, @RequestParam Long instanceId) {
        orderService.createOrderWithNewUser(username, instanceId);
        return "Success, order created!";
    }
}