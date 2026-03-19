package ferym.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
@Schema(description = "Объект передачи данных дата-центра")
public class DatacenterDto {
    @Schema(description = "ID дата-центра", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Название ЦОД", example = "MSK-01")
    @Size(min = 2, message = "Имя должно быть не короче 2 символов")
    private String name;

    @Schema(description = "Географическое расположение", example = "Москва, ул. Льва Толстого")
    @NotBlank(message = "Локация обязательна")
    private String location;

    @Schema(description = "ID связанных инстансов")
    private Set<Long> instanceIds;
}