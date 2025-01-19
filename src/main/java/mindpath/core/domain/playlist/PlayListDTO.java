package mindpath.core.domain.playlist;

import mindpath.core.domain.playlist.item.correction.CorrectionDTO;
import mindpath.core.domain.playlist.item.exercice.ExerciceDTO;
import mindpath.core.domain.playlist.item.fiche.FicheDTO;
import mindpath.core.domain.playlist.item.qcm.QcmDTO;
import mindpath.core.domain.playlist.item.video.VideoDTO;

import java.util.List;

public record PlayListDTO (
        Long id,
        String title,
        String description,
        List<VideoDTO> videos,
        List<FicheDTO> fiches,
        List<ExerciceDTO> exercices,
        List<CorrectionDTO> corrections,
        List<QcmDTO> qcms,
        Long subjectId
){
}
