package ferym.project.repository;

import ferym.project.model.CloudUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // ОБЯЗАТЕЛЬНО проверь этот импорт

public interface UserRepository extends JpaRepository<CloudUser, Long> {
    Optional<CloudUser> findByUsername(String username);
}