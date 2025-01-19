package mindpath.core.mapper;


import mindpath.core.domain.event.Event;
import mindpath.core.domain.event.EventDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EventDTOMapper implements Function<Event , EventDTO> {
    @Override
    public EventDTO apply(Event event) {
        return new EventDTO(
                event.getId() == null ? null : event.getId(),
                event.getTitle(),
                event.getStartTime(),
                event.getEndTime(),
                event.getBackgroundColor(),
                event.getGroup() == null ? null : event.getGroup().getId()
        );
    }
}
