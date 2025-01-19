package mindpath.core.repository.playlist.item;

import mindpath.core.domain.playlist.item.qcm.Qcm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface QcmRepository extends JpaRepository<Qcm, Integer> {

    @Query("select q from Qcm q where q.id = :id")
    Optional<Qcm> findQcmById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("delete from Qcm q where q.id = :id")
    void deleteById(@Param("id") Long id);
}
