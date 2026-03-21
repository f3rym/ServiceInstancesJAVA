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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private InstanceRepository instanceRepository;
    @Mock private OrderMapper mapper;
    @Mock private StatisticsService statisticsService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrdersBulk_ShouldCreateMultipleOrders_WhenValidDataProvided() {

        OrderDto dto = new OrderDto();
        dto.setUserId(1L);
        dto.setInstanceId(10L);

        User user = new User();
        Instance instance = new Instance();
        Order savedOrder = new Order();
        OrderDto responseDto = new OrderDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(instanceRepository.findById(10L)).thenReturn(Optional.of(instance));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(mapper.toDto(savedOrder)).thenReturn(responseDto);

        List<OrderDto> result = orderService.createOrdersBulk(List.of(dto, dto), true);

        assertThat(result).hasSize(2).containsOnly(responseDto);
        verify(statisticsService, times(2)).incrementSafe();
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void createOrdersBulk_ShouldThrowException_WhenUserNotFound() {
        OrderDto dto = new OrderDto();
        dto.setUserId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrdersBulk(List.of(dto), true))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Пользователь не найден");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrderWithNewUser_ShouldThrowSystemFailure_WhenUsernameIsFail() {
        String username = "fail";
        Long instanceId = 10L;
        Instance instance = new Instance();

        when(instanceRepository.findById(instanceId)).thenReturn(Optional.of(instance));

        assertThatThrownBy(() -> orderService.createOrderWithNewUser(username, instanceId))
                .isInstanceOf(SystemFailureException.class)
                .hasMessage("Имитация сбоя.");

        verify(userRepository, times(1)).save(any(User.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
}