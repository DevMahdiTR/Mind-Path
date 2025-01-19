package mindpath.core.domain.subject;

import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.group.Group;
import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.subject.section.Section;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "subjects")
public class Subject {

    @SequenceGenerator(
            name = "subject_sequence",
            sequenceName = "subject_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "subject_sequence")
    @Id
    private Long id;

    @Column(name = "speciality", nullable = false)
    private String speciality;

    @Column(name = "level" , nullable = false)
    private String level;

    @Column(name = "background_image_url", nullable = false)
    private String backgroundImageUrl;

    @Column(name = "main_image_url", nullable = false)
    private String mainImageUrl;

    @OneToMany(mappedBy = "subject", fetch = FetchType.EAGER, cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Section> sections;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private UserEntity teacher;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    @OneToMany(mappedBy = "subject", fetch = FetchType.EAGER, cascade = CascadeType.ALL , orphanRemoval = true)
    private List<PlayList> playLists;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(id, subject.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
