package ferym.project.service;

import ferym.project.dto.OrderDto;
import ferym.project.mapper.OrderMapper;
import ferym.project.model.CloudInstance;
import ferym.project.model.CloudOrder;
import ferym.project.model.CloudUser;
import ferym.project.repository.InstanceRepository;
import ferym.project.repository.OrderRepository;
import ferym.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final InstanceRepository instanceRepository;
    private final OrderMapper mapper;

    @Transactional(readOnly = true)
    public List<OrderDto> getAll() {
        return orderRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public OrderDto create(OrderDto dto) {
        CloudOrder order = new CloudOrder();

        CloudUser user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        CloudInstance instance = instanceRepository.findById(dto.getInstanceId())
                .orElseThrow(() -> new RuntimeException("Инстанс не найден"));

        order.setUser(user);
        order.setInstance(instance);
        order.setCreatedAt(LocalDateTime.now());

        return mapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public void createOrderWithNewUser(String username, Long instanceId) {
        // 1. Создаем пользователя
        CloudUser user = new CloudUser();
        user.setUsername(username);
        userRepository.save(user);

        CloudInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("Инстанс не найден"));

        if ("fail".equalsIgnoreCase(username)) {
            throw new RuntimeException("Имитация сбоя системы! Транзакция должна откатиться.");
        }

        CloudOrder order = new CloudOrder();
        order.setUser(user);
        order.setInstance(instance);
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }
}