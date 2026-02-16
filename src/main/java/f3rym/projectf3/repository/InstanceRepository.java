package f3rym.projectf3.repository;

import f3rym.projectf3.model.CloudInstance;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InstanceRepository {
    private final List<CloudInstance> instances = new ArrayList<>();

    public InstanceRepository() {
        instances.add(new CloudInstance(1L, "Web-Server", "t2.micro", "RUNNING"));
        instances.add(new CloudInstance(2L, "DB-Server", "m5.large", "STOPPED"));
    }
    public List<CloudInstance> findAll() {
        return instances;
    }
    public Optional<CloudInstance> findById(Long id) {
        return instances.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst();
    }
}