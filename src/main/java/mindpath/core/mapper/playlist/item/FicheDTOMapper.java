package mindpath.core.mapper.playlist.item;

import mindpath.core.domain.playlist.item.fiche.Fiche;
import mindpath.core.domain.playlist.item.fiche.FicheDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class FicheDTOMapper implements Function<Fiche , FicheDTO> {
    @Override
    public FicheDTO apply(Fiche fiche) {
        return new FicheDTO(
                fiche.getId(),
                fiche.getTitle(),
                fiche.getUrl(),
                fiche.isCompleted(),
                fiche.isFailed()
        );
    }
}
