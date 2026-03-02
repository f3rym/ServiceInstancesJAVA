package ferym.project.repository;

import ferym.project.model.CloudInstance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstanceRepository extends JpaRepository<CloudInstance, Long> {

    @EntityGraph(attributePaths = {"datacenter", "installedSoftware"})
    List<CloudInstance> findAll();
}
