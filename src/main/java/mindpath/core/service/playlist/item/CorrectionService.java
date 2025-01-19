package mindpath.core.service.playlist.item;

import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.playlist.item.correction.Correction;
import mindpath.core.repository.playlist.item.CorrectionRepository;
import org.springframework.stereotype.Service;

@Service
public class CorrectionService  implements ItemService<Correction> {

    private final CorrectionRepository correctionRepository;

    public CorrectionService(CorrectionRepository correctionRepository) {
        this.correctionRepository = correctionRepository;
    }

    @Override
    public Correction addItemToPlayList(Correction correction, PlayList playList) {
        correction.setPlayList(playList);
        return correctionRepository.save(correction);
    }

    @Override
    public Correction deleteItemFromPlayList(Correction item) {
        correctionRepository.deleteById(item.getId());
        return item;
    }

    @Override
    public Correction updateItem(Correction item) {
        return correctionRepository.save(item);
    }

    @Override
    public Correction getItemById(Long itemId) {
        return correctionRepository.findCorrectionById(itemId).orElseThrow(() -> new IllegalArgumentException("La correction n'existe pas"));
    }
}
