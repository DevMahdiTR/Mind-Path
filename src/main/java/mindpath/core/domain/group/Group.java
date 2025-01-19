package mindpath.core.domain.group;

import mindpath.core.domain.auth.student.Student;
import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.auth.user.EducationLevel;
import mindpath.core.domain.event.Event;
import mindpath.core.domain.subject.Subject;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "groups")
public class Group {

    @SequenceGenerator(
            name = "group_sequence",
            sequenceName = "group_sequence",
            allocationSize = 1
    )

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_sequence")
    @Id
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "updated_at" , nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "background_image_url", nullable = false)
    private String backgroundImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "education_level", nullable = false)
    private EducationLevel educationLevel;

    @Column(name = "created_at" , nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "main_image_url", nullable = false)
    private String mainImageUrl;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "group_student",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "super_teacher_id", referencedColumnName = "id")
    private SuperTeacher superTeacher;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subject> subjects;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Event> events;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
