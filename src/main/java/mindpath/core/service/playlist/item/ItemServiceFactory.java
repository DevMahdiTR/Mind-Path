package mindpath.core.service.playlist.item;

import mindpath.core.domain.playlist.item.Item;
import mindpath.core.domain.playlist.item.correction.Correction;
import mindpath.core.domain.playlist.item.exercice.Exercice;
import mindpath.core.domain.playlist.item.fiche.Fiche;
import mindpath.core.domain.playlist.item.qcm.Qcm;
import mindpath.core.domain.playlist.item.video.Video;
import org.springframework.stereotype.Component;

@Component
public class ItemServiceFactory {

    private final VideoService videoService;
    private final QcmService qcmService;
    private final FicheService ficheService;
    private final ExerciceService exerciceService;
    private final CorrectionService correctionService;

    public ItemServiceFactory(CorrectionService correctionService, VideoService videoService, QcmService qcmService, FicheService ficheService, ExerciceService exerciceService) {
        this.correctionService = correctionService;
        this.videoService = videoService;
        this.qcmService = qcmService;
        this.ficheService = ficheService;
        this.exerciceService = exerciceService;
    }


    public ItemService<? extends Item> getItemService(Item item) {
        if (item instanceof Video) {
            return videoService;
        } else if (item instanceof Qcm) {
            return qcmService;
        } else if (item instanceof Fiche) {
            return ficheService;
        } else if (item instanceof Exercice) {
            return exerciceService;
        } else if (item instanceof Correction) {
            return correctionService;
        } else {
            throw new IllegalArgumentException("Type d'élément inconnu :" + item.getClass());
        }
    }

    public Item getItemServiceById(Long id, String type) {
        if (id == null) {
            throw new IllegalArgumentException("L'id ne peut pas être nul");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type ne peut pas être nul ou vide");
        }
        if (type.equals("video")) {
            return videoService.getItemById(id);
        } else if (type.equals("qcm")) {
            return qcmService.getItemById(id);
        } else if (type.equals("fiche")) {
            return ficheService.getItemById(id);
        } else if (type.equals("exercice")) {
            return exerciceService.getItemById(id);
        } else if (type.equals("correction")) {
            return correctionService.getItemById(id);
        } else {
            throw new IllegalArgumentException("Type d'élément inconnu :" + type);
        }

    }

}
