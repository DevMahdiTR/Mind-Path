package mindpath.core.repository.offer;

import mindpath.core.domain.offer.StudentOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentOfferRepository extends JpaRepository<StudentOffer,Long> {


    @Query(value = "SELECT T FROM StudentOffer T WHERE T.id = :offerId")
    Optional<StudentOffer> fetchStudentOfferById(@Param("offerId") Long offerId);



}
