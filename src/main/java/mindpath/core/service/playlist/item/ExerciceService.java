package mindpath.core.service.playlist.item;

import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.playlist.item.exercice.Exercice;
import mindpath.core.repository.playlist.item.ExerciceRepository;
import org.springframework.stereotype.Service;

@Service
public class ExerciceService implements  ItemService<Exercice> {

    private final ExerciceRepository exerciceRepository;

    public ExerciceService(ExerciceRepository exerciceRepository) {
        this.exerciceRepository = exerciceRepository;
    }

    @Override
    public Exercice addItemToPlayList(Exercice exercice, PlayList playList) {
        exercice.setPlayList(playList);
        return exerciceRepository.save(exercice);
    }

    @Override
    public Exercice deleteItemFromPlayList(Exercice item) {
        exerciceRepository.deleteById(item.getId());
        return item;
    }

    @Override
    public Exercice updateItem(Exercice item) {
        return exerciceRepository.save(item);
    }

    @Override
    public Exercice getItemById(Long itemId) {
        return exerciceRepository.findExerciceById(itemId).orElseThrow(() -> new IllegalArgumentException("L'exercice n'existe pas"));
    }
}
