package ferym.project.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudInstance {

    private Long id;
    private String name;
    private String instanceType;
    private String os;
    private String status;

}
