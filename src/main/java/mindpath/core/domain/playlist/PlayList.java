package mindpath.core.domain.playlist;

import mindpath.core.domain.playlist.item.correction.Correction;
import mindpath.core.domain.playlist.item.exercice.Exercice;
import mindpath.core.domain.playlist.item.fiche.Fiche;
import mindpath.core.domain.playlist.item.qcm.Qcm;
import mindpath.core.domain.playlist.item.video.Video;
import mindpath.core.domain.subject.Subject;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "playlists")
public class PlayList {

    @SequenceGenerator(
            name = "playlist_sequence",
            sequenceName = "playlist_sequence",
            allocationSize = 1
    )

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playlist_sequence")
    @Id
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "playList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Video> videos;

    @OneToMany(mappedBy = "playList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Fiche> fiches;

    @OneToMany(mappedBy = "playList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Exercice> exercices;

    @OneToMany(mappedBy = "playList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Correction> corrections;

    @OneToMany(mappedBy = "playList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Qcm> qcms;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private Subject subject;
    


}
