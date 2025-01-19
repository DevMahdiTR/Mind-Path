package mindpath.core.domain.playlist.item.exercice;


import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.playlist.item.Item;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "exercices")
public class Exercice extends Item {

    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private PlayList playList;
}
