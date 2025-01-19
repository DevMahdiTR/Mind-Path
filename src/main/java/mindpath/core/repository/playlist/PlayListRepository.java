package mindpath.core.repository.playlist;

import mindpath.core.domain.playlist.PlayList;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface  PlayListRepository extends JpaRepository<PlayList,Integer> {


    @Query(value = "SELECT pl FROM PlayList pl WHERE pl.id = :playListId")
    Optional<PlayList> fetchPlayListById(@Param("playListId") final Long playListId);



    @Transactional
    @Modifying
    @Query(value = "DELETE FROM PlayList pl WHERE pl.id = :playListId")
    void deletePlayListById(@Param("playListId") final Long playListId);
}
