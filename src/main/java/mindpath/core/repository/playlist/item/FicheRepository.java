package mindpath.core.repository.playlist.item;

import mindpath.core.domain.playlist.item.fiche.Fiche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FicheRepository extends JpaRepository<Fiche, Integer> {

    @Query("select f from Fiche f where f.id = :id")
    Optional<Fiche> findFicheById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("delete from Fiche f where f.id = :id")
    void deleteById(@Param("id") Long id);
}
