package ferym.project.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

    @Entity
    @Table(name = "instances")
    @Getter
    @Setter
    @NoArgsConstructor
    public class Instance {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        @Column(length = 64)
        private String instanceType;

        private String os;
        private Double price;

        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinTable(
                name = "datacenter_instances",
                joinColumns = @JoinColumn(name = "instance_id"),
                inverseJoinColumns = @JoinColumn(name = "datacenter_id")
        )
        private Set<Datacenter> datacenters;

        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinTable(
                name = "instance_software",
                joinColumns = @JoinColumn(name = "instance_id"),
                inverseJoinColumns = @JoinColumn(name = "software_id")
        )
        private Set<Software> installedSoftware;
    }