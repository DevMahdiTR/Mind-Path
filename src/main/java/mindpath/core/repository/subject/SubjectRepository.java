package mindpath.core.repository.subject;

import mindpath.core.domain.subject.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Subject s WHERE s.id = :subjectId")
    void deleteSubjectById(@Param("subjectId") Long subjectId);
}
