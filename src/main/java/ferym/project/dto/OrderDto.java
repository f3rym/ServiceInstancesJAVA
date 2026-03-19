package ferym.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Данные заказа на аренду")
public class OrderDto {
    @Schema(description = "ID заказа", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID заказчика", example = "1")
    @NotBlank(message = "Пользователь обязателен при заказе")
    private Long userId;

    @Schema(description = "ID арендуемого сервера", example = "5")
    @NotBlank(message = "Инстанс обязателен при заказе")
    private Long instanceId;
}