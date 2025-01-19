package mindpath.core.mapper.playlist.item;

import mindpath.core.domain.playlist.item.Item;
import mindpath.core.domain.playlist.item.ItemDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ItemDTOMapper implements Function<Item, ItemDTO> {
    @Override
    public ItemDTO apply(Item item) {
        return new ItemDTO(
                item.getId(),
                item.getTitle(),
                item.getUrl(),
                item.isCompleted(),
                item.isFailed()
        );
    }
}
