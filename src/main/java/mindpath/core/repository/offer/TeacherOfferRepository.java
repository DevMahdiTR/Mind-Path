package mindpath.core.repository.offer;

import mindpath.core.domain.offer.TeacherOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TeacherOfferRepository extends JpaRepository<TeacherOffer, Long> {

    @Query(value = "SELECT S FROM TeacherOffer S WHERE S.id = :offerId")
    Optional<TeacherOffer> fetchTeacherOfferById(@Param("offerId") Long offerId);


}
