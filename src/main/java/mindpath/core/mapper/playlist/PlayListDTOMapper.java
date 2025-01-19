package mindpath.core.mapper.playlist;

import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.playlist.PlayListDTO;
import mindpath.core.mapper.playlist.item.*;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PlayListDTOMapper implements Function<PlayList, PlayListDTO> {
    @Override
    public PlayListDTO apply(PlayList playList) {
        return new PlayListDTO(
                playList.getId(),
                playList.getTitle(),
                playList.getDescription(),
                playList.getVideos() == null ? null : playList.getVideos().stream().map(new VideoDTOMapper()).toList(),
                playList.getFiches() == null ? null : playList.getFiches().stream().map(new FicheDTOMapper()).toList(),
                playList.getExercices() == null ? null : playList.getExercices().stream().map(new ExerciceDTOMapper()).toList(),
                playList.getCorrections() == null ? null : playList.getCorrections().stream().map(new CorrectionDTOMapper()).toList(),
                playList.getQcms() == null ? null : playList.getQcms().stream().map(new QcmDTOMapper()).toList(),
                playList.getSubject() == null ? null : playList.getSubject().getId()
        );
    }
}
