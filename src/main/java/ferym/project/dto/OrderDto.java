package ferym.project.dto;


import ferym.project.model.CloudInstance;
import ferym.project.model.CloudUser;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Long id;
    private CloudUser user;
    private CloudInstance instance;
    private LocalDateTime createdAt;

}
