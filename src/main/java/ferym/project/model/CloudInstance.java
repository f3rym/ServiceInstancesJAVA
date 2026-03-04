package ferym.project.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "instances")
@Getter
@Setter
@NoArgsConstructor
public class CloudInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String instanceType;
    private String os;
    private Double price;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "datacenter_id")
    private Datacenter datacenter;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "instance_software",
            joinColumns = @JoinColumn(name = "instance_id"),
            inverseJoinColumns = @JoinColumn(name = "software_id")
    )
    private Set<Software> installedSoftware;
}