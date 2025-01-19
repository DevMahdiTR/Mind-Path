package mindpath.core.domain.playlist.item.qcm;


import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.playlist.item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "qcms")
public class Qcm extends Item {
    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private PlayList playList;
}
