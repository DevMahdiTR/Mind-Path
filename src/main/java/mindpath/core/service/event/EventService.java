package mindpath.core.service.event;

import mindpath.core.domain.event.Event;
import mindpath.core.domain.event.EventDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    Event getEventById(long eventId);
    Event save(Event event);
    Event saveAndFlush(Event event);
    EventDTO mapToDTO(Event event);
    List<EventDTO> mapToDTO(List<Event> events);
    void validateEventTiming(Long groupId,LocalDateTime startTime, LocalDateTime endTime, Long eventId);

}
