package ferym.project.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "datacenters")
@Getter
@Setter
@NoArgsConstructor
public class Datacenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    @ManyToMany(mappedBy = "datacenters")
    private Set<Instance> instances;
}