package ferym.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectF3Cloud {
    protected ProjectF3Cloud() {
        // Пустой конструктор для предотвращения инстанцирования
    }
    public static void main(String[] args) {
        SpringApplication.run(ProjectF3Cloud.class, args);
    }
}
