package ferym.project.dto;

import ferym.project.model.CloudInstance;
import lombok.Data;

import java.util.Set;

@Data
public class SoftwareDto {

    private Long id;
    private String name;
    private String version;
    private Set<CloudInstance> instances;

}