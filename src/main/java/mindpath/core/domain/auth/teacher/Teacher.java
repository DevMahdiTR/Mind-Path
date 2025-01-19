package mindpath.core.domain.auth.teacher;


import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.auth.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(name = "teachers")
@DiscriminatorValue("TEACHER")
public class Teacher extends UserEntity {

    @ManyToMany(mappedBy = "teachers", fetch = FetchType.EAGER)
    private Set<SuperTeacher> superTeachers;
}
