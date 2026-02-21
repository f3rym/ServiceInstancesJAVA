package f3rym.projectf3.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Datacenter {
    private Long id;
    private String name;
    private String location;
}