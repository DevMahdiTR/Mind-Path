package mindpath.core.repository;

import mindpath.core.domain.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query(value = "SELECT G FROM Group  G WHERE G.id = :classId")
    Optional<Group> fetchGroupById(@Param("classId") final long classId);

    @Query(value = "SELECT G FROM Group G WHERE G.isPublic = true ORDER BY function('RANDOM')")
    List<Group> fetchAllByIsPublicTrue();


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Group G WHERE G.id = :classId")
    void deleteGroupById(@Param("classId") final long classId);
}
