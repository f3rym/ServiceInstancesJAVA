package ferym.project.service;

import ferym.project.exception.InstanceNotFoundException;
import ferym.project.model.CloudInstance;
import ferym.project.model.CloudOrder;
import ferym.project.model.CloudUser;
import ferym.project.repository.InstanceRepository;
import ferym.project.repository.OrderRepository;
import ferym.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final InstanceRepository instanceRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createPurchase(String username, Long instanceId) {

        CloudUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Демонстрация отката транзакции (теперь после поиска)
        if (username.equalsIgnoreCase("error")) {
            throw new RuntimeException("Ошибка! Транзакция будет откатана.");
        }

        // 2. Ищем инстанс
        CloudInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new InstanceNotFoundException("Инстанс не найден с id: " + instanceId));

        // 3. Создаем заказ
        CloudOrder order = new CloudOrder();
        order.setInstance(instance);
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }
}
