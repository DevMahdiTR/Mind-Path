package mindpath.core.repository.playlist.item;

import mindpath.core.domain.playlist.item.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Integer> {

    @Query("select v from Video v where v.id = :id")
    Optional<Video> findVideoById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("delete from Video v where v.id = :id")
    void deleteById(@Param("id") Long id);
}
