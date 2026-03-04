package ferym.project.repository;

import ferym.project.model.CloudOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CloudOrder, Long> {
}