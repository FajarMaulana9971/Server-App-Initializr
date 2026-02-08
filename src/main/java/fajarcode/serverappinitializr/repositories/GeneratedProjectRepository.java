package fajarcode.serverappinitializr.repositories;

import fajarcode.serverappinitializr.models.entities.GeneratedProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneratedProjectRepository extends JpaRepository<GeneratedProject, Long> {
    @Modifying
    @Query(" UPDATE GeneratedProject g " +
            "SET g.downloadCount = g.downloadCount + 1 " +
            "WHERE g.applicationName = :projectName")
    int incrementDownloadCount(@Param("projectName") String projectName);

    Optional<GeneratedProject> getProjectByApplicationName(String applicationName);

}
