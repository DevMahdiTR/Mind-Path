package mindpath.core.repository.offer;

import mindpath.core.domain.offer.request.StudentOfferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentOfferRequestRepository extends JpaRepository<StudentOfferRequest , Long> {

    @Query(value = "SELECT SOR FROM StudentOfferRequest SOR WHERE SOR.id = :studentOfferRequestId")
    Optional<StudentOfferRequest> fetchStudentOfferRequestById(@Param("studentOfferRequestId") long studentOfferRequestId);
}
