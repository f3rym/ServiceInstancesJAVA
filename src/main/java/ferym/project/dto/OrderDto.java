package ferym.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderDto {
    private Long id;

    @NotBlank(message = "Пользователь обязателен при заказе")
    private Long userId;

    @NotBlank(message = "Инстанс обязателен при заказе")
    private Long instanceId;
}