package mindpath.core.service.playlist.item;

import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.playlist.item.Item;

public interface ItemService <T extends Item> {
    T addItemToPlayList(T item, PlayList playList);
    T deleteItemFromPlayList(T item);
    T updateItem(T item);
    T getItemById(Long itemId);
}
