package mindpath.core.service.playlist.item;

import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.playlist.item.qcm.Qcm;
import mindpath.core.repository.playlist.item.QcmRepository;
import org.springframework.stereotype.Service;

@Service
public class QcmService implements ItemService<Qcm> {

    private final QcmRepository qcmRepository;

    public QcmService(QcmRepository qcmRepository) {
        this.qcmRepository = qcmRepository;
    }

    @Override
    public Qcm addItemToPlayList(Qcm qcm, PlayList playList) {
        qcm.setPlayList(playList);
        return qcmRepository.save(qcm);
    }

    @Override
    public Qcm deleteItemFromPlayList(Qcm item) {
        qcmRepository.deleteById(item.getId());
        return item;
    }

    @Override
    public Qcm updateItem(Qcm item) {
        return qcmRepository.save(item);
    }

    @Override
    public Qcm getItemById(Long itemId) {
        return qcmRepository.findQcmById(itemId).orElseThrow(() -> new IllegalArgumentException("Le Qcm n'existe pas"));
    }
}
