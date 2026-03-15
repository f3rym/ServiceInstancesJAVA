package ferym.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SoftwareDto {
    private Long id;

    @Size(min = 2, message = "Имя должно быть не короче 2 символов")
    private String name;

    @NotBlank(message = "Версия обязательна")
    private String version;
}