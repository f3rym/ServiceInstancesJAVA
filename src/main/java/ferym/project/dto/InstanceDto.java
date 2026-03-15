package ferym.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class InstanceDto {
    private Long id;

    @Size(min = 2, message = "Имя должно быть не короче 2 символов")
    private String name;

    @NotBlank(message = "type is required")
    private String instanceType;

    @NotBlank(message = "os is required")
    private String os;

    @Min(value = 1, message = "Цена должна быть больше 0")
    private Double price;

    private Set<Long> datacenterIds;

    private List<Long> softwareIds;
}