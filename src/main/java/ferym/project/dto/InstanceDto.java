package ferym.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
@Schema(description = "Сведения об облачном сервере")
public class InstanceDto {
    @Schema(description = "Идентификатор", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Название сервера", example = "Production-DB-1")
    @Size(min = 2, message = "Имя должно быть не короче 2 символов")
    private String name;

    @Schema(description = "Тип инстанса", example = "t3.medium")
    @NotBlank(message = "type is required")
    private String instanceType;

    @Schema(description = "Операционная система", example = "Ubuntu 22.04")
    @NotBlank(message = "os is required")
    private String os;

    @Schema(description = "Стоимость в час", example = "0.15")
    @Min(value = 1, message = "Цена должна быть больше 0")
    private Double price;

    @Schema(description = "Список ID дата-центров")
    private Set<Long> datacenterIds;

    @Schema(description = "Список ID предустановленного ПО")
    private List<Long> softwareIds;
}