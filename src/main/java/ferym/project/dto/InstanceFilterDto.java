package ferym.project.dto;


import lombok.Data;

@Data
public class InstanceFilterDto {
    private Double price;
    private String datacenterLocation;
    private boolean useNative;
}
