package fajarcode.serverappinitializr.repositories;

import fajarcode.serverappinitializr.models.entities.GeneratedProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratedProjectRepository extends JpaRepository<GeneratedProject, Long> {
}
