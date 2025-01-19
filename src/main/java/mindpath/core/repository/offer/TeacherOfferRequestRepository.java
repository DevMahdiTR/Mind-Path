package mindpath.core.repository.offer;

import mindpath.core.domain.offer.request.TeacherOfferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherOfferRequestRepository extends JpaRepository<TeacherOfferRequest, Long> {

    @Query(value = "SELECT TOR FROM TeacherOfferRequest TOR WHERE TOR.id = :teacherOfferRequestId")
    Optional<TeacherOfferRequest> fetchTeacherOfferRequestById(@Param("teacherOfferRequestId") long teacherOfferRequestId);
}
