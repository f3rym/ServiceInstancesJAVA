package ferym.project.dto;

import lombok.Data;

import java.util.Set;

@Data
public class InstanceDto {

    private Long id;
    private String name;
    private String instanceType;
    private String status;
    private String os;
    private Double price;
    private String datacenter;
    private Set<String> software;
}



