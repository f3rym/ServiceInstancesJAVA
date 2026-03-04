package ferym.project.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "software")
@Getter
@Setter
@NoArgsConstructor
public class Software {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String version;

    @ManyToMany(mappedBy = "installedSoftware")
    private Set<Instance> instances;
}