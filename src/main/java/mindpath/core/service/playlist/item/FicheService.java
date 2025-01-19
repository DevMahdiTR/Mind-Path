package mindpath.core.service.playlist.item;

import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.playlist.item.fiche.Fiche;
import mindpath.core.repository.playlist.item.FicheRepository;
import org.springframework.stereotype.Service;

@Service
public class FicheService  implements ItemService<Fiche> {

    private final FicheRepository ficheRepository;

    public FicheService(FicheRepository ficheRepository) {
        this.ficheRepository = ficheRepository;
    }

    @Override
    public Fiche addItemToPlayList(Fiche fiche, PlayList playList) {
        fiche.setPlayList(playList);
        return ficheRepository.save(fiche);
    }

    @Override
    public Fiche deleteItemFromPlayList(Fiche item) {
        ficheRepository.deleteById(item.getId());
        return item;
    }

    @Override
    public Fiche updateItem(Fiche item) {
        return ficheRepository.save(item);
    }

    @Override
    public Fiche getItemById(Long itemId) {
        return ficheRepository.findFicheById(itemId).orElseThrow(() -> new IllegalArgumentException("La fiche n'existe pas"));
    }
}
