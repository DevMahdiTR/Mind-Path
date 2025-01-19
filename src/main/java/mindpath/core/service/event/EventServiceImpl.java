package mindpath.core.service.event;

import mindpath.core.domain.event.Event;
import mindpath.core.domain.event.EventDTO;
import mindpath.core.exceptions.custom.DatabaseException;
import mindpath.core.exceptions.custom.ResourceNotFoundException;
import mindpath.core.mapper.EventDTOMapper;
import mindpath.core.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventDTOMapper eventDTOMapper;


    public EventServiceImpl(EventRepository eventRepository, EventDTOMapper eventDTOMapper) {
        this.eventRepository = eventRepository;
        this.eventDTOMapper = eventDTOMapper;
    }

    @Override
    public Event getEventById(long eventId) {
        return eventRepository.fetchEventById(eventId).orElseThrow(
                () -> new ResourceNotFoundException("Événement avec l'ID " + eventId + " non trouvé")
        );
    }

    @Override
    public Event save(Event event) {
        try{
            log.debug("Saving event: {}", event);
            Event savedEvent = eventRepository.save(event);
            log.info("Event saved successfully: {}", savedEvent);
            return savedEvent;
        }catch (Exception e){
            log.error("Error saving event: {}", event, e);
            throw new DatabaseException("Erreur lors de l'exécution de la requête de base de données");
        }
    }

    @Override
    public Event saveAndFlush(Event event) {
        try{
            log.debug("Saving and flushing event: {}", event);
            Event savedEvent = eventRepository.saveAndFlush(event);
            log.info("Event saved and flushed successfully: {}", savedEvent);
            return savedEvent;
        }catch (Exception e){
            log.error("Error saving and flushing event: {}", event, e);
            throw new DatabaseException("Erreur lors de l'exécution de la requête de base de données");
        }
    }

    @Override
    public EventDTO mapToDTO(Event event) {
        return eventDTOMapper.apply(event);
    }

    @Override
    public List<EventDTO> mapToDTO(List<Event> events) {
        return events.stream()
                .map(eventDTOMapper)
                .toList();
    }

    @Override
    public void validateEventTiming(Long groupId, LocalDateTime startTime, LocalDateTime endTime, Long eventId) {
        if (!startTime.toLocalDate().isEqual(endTime.toLocalDate())) {
            throw new IllegalArgumentException("L'heure de début et l'heure de fin doivent être le même jour.");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("L'heure de début doit être antérieure à l'heure de fin.");
        }
        List<Event> conflictingEvents = eventRepository.findConflictingEvents(groupId, startTime, endTime, eventId);
        if (!conflictingEvents.isEmpty()) {
            throw new IllegalArgumentException("L'événement entre en conflit avec un autre événement dans le même groupe.");
        }
    }


}
