package mindpath.core.mapper.playlist.item;

import mindpath.core.domain.playlist.item.exercice.Exercice;
import mindpath.core.domain.playlist.item.exercice.ExerciceDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ExerciceDTOMapper implements Function<Exercice, ExerciceDTO> {
    @Override
    public ExerciceDTO apply(Exercice exercice) {
        return new ExerciceDTO(
                exercice.getId(),
                exercice.getTitle(),
                exercice.getUrl(),
                exercice.isCompleted(),
                exercice.isFailed()
        );
    }
}
