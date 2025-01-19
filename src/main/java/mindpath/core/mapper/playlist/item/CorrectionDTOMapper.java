package mindpath.core.mapper.playlist.item;

import mindpath.core.domain.playlist.item.correction.Correction;
import mindpath.core.domain.playlist.item.correction.CorrectionDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CorrectionDTOMapper implements Function<Correction, CorrectionDTO> {
    @Override
    public CorrectionDTO apply(Correction correction) {
        return new CorrectionDTO(
                correction.getId(),
                correction.getTitle(),
                correction.getUrl(),
                correction.isCompleted(),
                correction.isFailed()
        );
    }
}
