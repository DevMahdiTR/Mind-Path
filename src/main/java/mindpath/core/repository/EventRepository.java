package mindpath.core.repository;

import mindpath.core.domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event , Long> {

    @Query(value = "SELECT E FROM Event E WHERE E.id = :eventId")
    Optional<Event> fetchEventById(@Param("eventId") final long eventId);

    @Query("SELECT e FROM Event e WHERE " +
            "e.group.id = :groupId AND " +
            "(:eventId IS NULL OR e.id != :eventId) AND " +
            "e.startTime < :endTime AND " +
            "e.endTime > :startTime")
    List<Event> findConflictingEvents(
            @Param("groupId") long groupId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("eventId") Long eventId);


    @Modifying
    @Query("DELETE FROM Event e WHERE e.id = :eventId")
    void deleteEventById(@Param("eventId") long eventId);


}
