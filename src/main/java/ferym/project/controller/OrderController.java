package ferym.project.controller;

import ferym.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/purchase")
    @ResponseStatus(HttpStatus.CREATED)
    public String purchaseInstance(@RequestParam String username, @RequestParam Long instanceId) {
        orderService.createPurchase(username, instanceId);
        return "Purchase completed successfully for user: " + username;
    }
}