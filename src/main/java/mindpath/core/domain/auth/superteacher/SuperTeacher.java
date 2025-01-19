package mindpath.core.domain.auth.superteacher;


import mindpath.core.domain.auth.teacher.Teacher;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.group.Group;
import mindpath.core.domain.offer.request.TeacherOfferRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(name = "super_teachers")
@DiscriminatorValue("SUPERTEACHER")
public class SuperTeacher  extends UserEntity {

    @OneToMany(mappedBy = "superTeacher",fetch = FetchType.EAGER)
    private List<Group> groups;

    @OneToMany(mappedBy = "superTeacher", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeacherOfferRequest> teacherOfferRequests;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "superteacher_teacher",
            joinColumns = @JoinColumn(name = "super_teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private Set<Teacher> teachers;

}
