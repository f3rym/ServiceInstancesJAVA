package ferym.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
public class DatacenterDto {
    private Long id;

    @Size(min = 2, message = "Имя должно быть не короче 2 символов")
    private String name;

    @NotBlank(message = "Локация обязательна")
    private String location;
    private Set<Long> instanceIds;
}