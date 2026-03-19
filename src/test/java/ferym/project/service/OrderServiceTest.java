package ferym.project.service;

import ferym.project.exception.SystemFailureException;
import ferym.project.model.Instance;
import ferym.project.model.User;
import ferym.project.repository.InstanceRepository;
import ferym.project.repository.OrderRepository;
import ferym.project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private InstanceRepository instanceRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderWithNewUser_Success_SavesUserAndOrder() {
        // Arrange
        String username = "validUser";
        Long instanceId = 1L;
        Instance instance = new Instance();
        instance.setId(instanceId);

        when(instanceRepository.findById(instanceId)).thenReturn(Optional.of(instance));

        orderService.createOrderWithNewUser(username, instanceId);

        verify(userRepository).save(any(User.class));
        verify(orderRepository).save(any());
    }

    @Test
    void createOrderWithNewUser_FailUsername_ThrowsSystemFailureException() {
        // Arrange
        String username = "fail";
        Long instanceId = 1L;
        Instance instance = new Instance();

        when(instanceRepository.findById(instanceId)).thenReturn(Optional.of(instance));

        assertThrows(SystemFailureException.class,
                () -> orderService.createOrderWithNewUser(username, instanceId));

        verify(userRepository).save(any(User.class));
        verify(orderRepository, never()).save(any());
    }
}