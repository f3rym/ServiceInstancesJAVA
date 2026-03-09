package ferym.project.repository;

import ferym.project.model.Datacenter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatacenterRepository extends JpaRepository<Datacenter, Long> {
}