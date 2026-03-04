package ferym.project.repository;

import ferym.project.model.Datacenter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DatacenterRepository extends JpaRepository<Datacenter, Long> {
    Optional<Datacenter> findByName(String name);
}