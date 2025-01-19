package mindpath.core.repository.playlist.item;

import mindpath.core.domain.playlist.item.exercice.Exercice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ExerciceRepository extends JpaRepository<Exercice, Integer> {


    @Query("select e from Exercice e where e.id = :id")
    Optional<Exercice> findExerciceById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("delete from Exercice e where e.id = :id")
    void deleteById(@Param("id") Long id);
}
