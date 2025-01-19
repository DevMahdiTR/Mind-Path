package mindpath.core.mapper.playlist.item;

import mindpath.core.domain.playlist.item.qcm.Qcm;
import mindpath.core.domain.playlist.item.qcm.QcmDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class QcmDTOMapper implements Function<Qcm, QcmDTO> {
    @Override
    public QcmDTO apply(Qcm qcm) {
        return new QcmDTO(
                qcm.getId(),
                qcm.getTitle(),
                qcm.getUrl(),
                qcm.isCompleted(),
                qcm.isFailed()
        );
    }
}
