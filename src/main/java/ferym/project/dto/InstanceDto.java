package ferym.project.dto;

import lombok.Data;

import java.util.List;

@Data
public class InstanceDto {
    private Long id;
    private String name;
    private String instanceType;
    private String os;
    private Double price;
    private String status;
    private String datacenterName; // Передаем имя ДЦ вместо всего объекта
    private List<Long> softwareIds; // Только ID установленного софта
}


