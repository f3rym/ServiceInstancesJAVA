package ferym.project.service;

import ferym.project.dto.OrderDto;
import ferym.project.exception.SystemFailureException;
import ferym.project.mapper.OrderMapper;
import ferym.project.model.Instance;
import ferym.project.model.Order;
import ferym.project.model.User;
import ferym.project.repository.InstanceRepository;
import ferym.project.repository.OrderRepository;
import ferym.project.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final InstanceRepository instanceRepository;
    private final OrderMapper mapper;

    private static final String INSTANCE_NOT_FOUND = "Инстанс не найден";

    @Transactional(readOnly = true)
    public List<OrderDto> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public OrderDto create(OrderDto dto) {
        Order order = new Order();

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        Instance instance = instanceRepository.findById(dto.getInstanceId())
                .orElseThrow(() -> new EntityNotFoundException(INSTANCE_NOT_FOUND));

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

        User user = new User();
        user.setUsername(username);
        userRepository.save(user);

        Instance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new EntityNotFoundException(INSTANCE_NOT_FOUND));

        if ("fail".equalsIgnoreCase(username)) {
            throw new SystemFailureException("Имитация сбоя.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setInstance(instance);
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }

}