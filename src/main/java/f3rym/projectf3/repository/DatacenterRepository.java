package f3rym.projectf3.repository;

import f3rym.projectf3.model.Datacenter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DatacenterRepository {
    private final List<Datacenter> datacenters = new ArrayList<>();


    public DatacenterRepository() {
        datacenters.add(new Datacenter(1L, "Minsk" , "EU"));
        datacenters.add(new Datacenter(2L, "Moscow" , "EU"));
        datacenters.add(new Datacenter(3L, "Warsaw" , "EU"));
        datacenters.add(new Datacenter(4L, "California" , "US"));

    }
    public List<Datacenter> findAll() {

        return datacenters;
    }
    public Optional<Datacenter> findById(Long id){
        return datacenters.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst();
    }
}
