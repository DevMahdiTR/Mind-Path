package mindpath.core.repository.playlist.item;

import mindpath.core.domain.playlist.item.correction.Correction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CorrectionRepository extends JpaRepository<Correction, Integer> {

    @Query("select c from Correction c where c.id = :id")
    Optional<Correction> findCorrectionById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("delete from Correction c where c.id = :id")
    void deleteById(@Param("id") Long id);
}
