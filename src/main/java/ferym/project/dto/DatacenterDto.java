package ferym.project.dto;

import lombok.Data;
import java.util.Set;

@Data
public class DatacenterDto {
    private Long id;
    private String name;
    private String location;
    private Set<Long> instanceIds;
}