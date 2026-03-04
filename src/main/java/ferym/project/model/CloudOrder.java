package ferym.project.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class CloudOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private CloudUser user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "instance_id")
    private CloudInstance instance;

    private LocalDateTime createdAt;
}