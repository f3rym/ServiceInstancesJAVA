package ferym.project.repository;

import ferym.project.model.CloudInstance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InstanceRepository extends JpaRepository<CloudInstance, Long> {
    @Override
    @EntityGraph(attributePaths = {"datacenter", "installedSoftware"})
    List<CloudInstance> findAll();
}