package ferym.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudOrder {
    private Long id;
    private Long userId;
    private Long instanceId;
    private Double price;
    private LocalDateTime createdAt;
}