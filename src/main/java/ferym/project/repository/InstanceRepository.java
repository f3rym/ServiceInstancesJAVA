package ferym.project.repository;

import ferym.project.model.Instance;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
    @NullMarked
    @Override
    @EntityGraph(attributePaths = {"datacenters", "installedSoftware"})
    List<Instance> findAll();

    @Query("SELECT i FROM Instance i LEFT JOIN i.datacenters d " + "WHERE i.price >= :price AND d.location = :location")
    Page<Instance> findByFilterJPQL(
            @Param("price") Double price,
            @Param("location") String location,
            Pageable pageable);

    @Query(value = "SELECT DISTINCT i.* FROM instances i " + "JOIN datacenter_instances di ON i.id = di.instance_id "
            + "JOIN datacenters d ON di.datacenter_id = d.id " + "WHERE i.price >= :price AND d.location = :location",
            nativeQuery = true)
    Page<Instance> findByFilterNative(
            @Param("price") Double price,
            @Param("location") String location,
            Pageable pageable);
}