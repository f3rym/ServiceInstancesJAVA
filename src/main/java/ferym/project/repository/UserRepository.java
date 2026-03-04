package ferym.project.repository;

import ferym.project.model.CloudUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<CloudUser, Long> {
    @Override
    @EntityGraph(attributePaths = {"orders"})
    List<CloudUser> findAll();
}