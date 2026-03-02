package ferym.project.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "software")
@Data
public class Software {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String version;

    @ManyToMany(mappedBy = "installedSoftware")
    private Set<CloudInstance> instances;
}