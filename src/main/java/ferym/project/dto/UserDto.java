package ferym.project.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;

    @Size(min = 2, message = "Имя должно быть не короче 2 символов")
    private String username;
    private List<Long> orderIds;
}