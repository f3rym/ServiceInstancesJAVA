package ferym.project.dto;

import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class InstanceDto {
    private Long id;
    private String name;
    private String instanceType;
    private String os;
    private Double price;

    private Set<Long> datacenterIds;

    private List<Long> softwareIds;
}