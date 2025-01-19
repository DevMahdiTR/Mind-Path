package mindpath.core.domain.auth.student;


import mindpath.core.domain.auth.user.EducationLevel;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.group.Group;
import mindpath.core.domain.offer.request.StudentOfferRequest;
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
@Table(name = "students")
@DiscriminatorValue("STUDENT")
public class Student extends UserEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "education_level", nullable = false)
    private EducationLevel educationLevel;

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentOfferRequest> studentOfferRequests;

    @ManyToMany(mappedBy = "students",fetch = FetchType.EAGER)
    private Set<Group> groups;



}
