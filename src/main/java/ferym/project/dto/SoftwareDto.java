package ferym.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Данные о программном обеспечении")
public class SoftwareDto {
    @Schema(description = "ID ПО", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Название ПО", example = "PostgreSQL")
    @Size(min = 2, message = "Имя должно быть не короче 2 символов")
    private String name;

    @Schema(description = "Версия", example = "15.4")
    @NotBlank(message = "Версия обязательна")
    private String version;
}