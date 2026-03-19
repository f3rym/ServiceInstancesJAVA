package ferym.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Информация о пользователе")
public class UserDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Уникальное имя пользователя", example = "john_doe")
    @Size(min = 2, message = "Имя должно быть не короче 2 символов")
    private String username;

    @Schema(description = "Список ID заказов пользователя")
    private List<Long> orderIds;
}