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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

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
    @Mock private org.springframework.transaction.TransactionStatus status;
    @Mock private TransactionTemplate transactionTemplate;
    @Mock private StatisticsService statisticsService;
    @InjectMocks private OrderService orderService;

    @BeforeEach
    @SuppressWarnings("unused")
    void setupTx() {
        lenient().when(transactionTemplate.execute(any())).thenAnswer(inv -> {
            TransactionCallback<?> cb = inv.getArgument(0);
            return cb.doInTransaction(status);
        });
    }


    @Test
    void getAll_ShouldReturnMappedList() {
        Order order = new Order();
        OrderDto dto = new OrderDto();
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(mapper.toDto(order)).thenReturn(dto);

        List<OrderDto> result = orderService.getAll();

        assertThat(result).containsExactly(dto);
    }


    @Test
    void createOrdersBulk_WithTransaction_ShouldExecuteWithinTemplate() {
        OrderDto dto = new OrderDto();
        dto.setUserId(1L);
        dto.setInstanceId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(instanceRepository.findById(2L)).thenReturn(Optional.of(new Instance()));
        when(orderRepository.save(any())).thenReturn(new Order());
        when(mapper.toDto(any())).thenReturn(dto);

        List<OrderDto> result = orderService.createOrdersBulk(List.of(dto), true);

        assertThat(result).hasSize(1);
        verify(transactionTemplate).execute(any());
    }

    @Test
    void createOrdersBulk_WithoutTransaction_ShouldExecuteDirectly() {
        OrderDto dto = new OrderDto();
        dto.setUserId(1L);
        dto.setInstanceId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(instanceRepository.findById(2L)).thenReturn(Optional.of(new Instance()));
        when(orderRepository.save(any())).thenReturn(new Order());

        orderService.createOrdersBulk(List.of(dto), false);

        verify(transactionTemplate, never()).execute(any());

        verify(statisticsService, atLeastOnce()).incrementSafe();
    }

    @Test
    void createOrdersBulk_ShouldThrow_WhenUserNotFoundInBulk() {

        OrderDto dto = new OrderDto();
        dto.setUserId(99L);
        List<OrderDto> dtoList = List.of(dto);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrdersBulk(dtoList, false))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void createOrdersBulk_ShouldThrow_WhenInstanceNotFoundInBulk() {
        OrderDto dto = new OrderDto();
        dto.setUserId(1L);
        dto.setInstanceId(99L);
        List<OrderDto> dtoList = List.of(dto);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(instanceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrdersBulk(dtoList, false))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Инстанс не найден");
    }


    @Test
    void createOrderWithNewUser_ShouldSuccess_WhenUsernameValid() {
        String username = "JohnDoe";
        Long instanceId = 1L;
        Instance instance = new Instance();

        when(instanceRepository.findById(instanceId)).thenReturn(Optional.of(instance));

        orderService.createOrderWithNewUser(username, instanceId);

        verify(userRepository).save(any(User.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrderWithNewUser_ShouldThrow_WhenInstanceNotFound() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrderWithNewUser("any", 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Инстанс не найден");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrderWithNewUser_ShouldThrow_WhenUsernameIsFail() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(new Instance()));

        assertThatThrownBy(() -> orderService.createOrderWithNewUser("fail", 1L))
                .isInstanceOf(SystemFailureException.class)
                .hasMessage("Имитация сбоя.");
    }


    @Test
    void delete_ShouldCallRepo() {
        orderService.delete(1L);
        verify(orderRepository).deleteById(1L);
    }
}