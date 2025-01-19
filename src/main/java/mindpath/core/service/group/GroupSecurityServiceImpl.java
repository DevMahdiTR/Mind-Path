package mindpath.core.service.group;

import mindpath.config.AuthenticationRoles;
import mindpath.core.domain.auth.student.Student;
import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.auth.teacher.Teacher;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.group.Group;
import mindpath.core.domain.subject.Subject;
import mindpath.core.service.subject.SubjectService;
import mindpath.core.service.user.UserEntityService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class GroupSecurityServiceImpl implements GroupSecurityService {

    private final UserEntityService userEntityService;
    private final SubjectService  subjectService;
    private final GroupService groupService;

    public GroupSecurityServiceImpl(UserEntityService userEntityService, SubjectService subjectService, GroupService groupService) {
        this.userEntityService = userEntityService;
        this.subjectService = subjectService;
        this.groupService = groupService;
    }

    @Override
    public Boolean isSuperTeacherAllowedToEditGroup(Long groupId, String userEmail) {
        SuperTeacher currentUser = (SuperTeacher) userEntityService.fetchUserByEmail(userEmail);

        if (!isSuperTeacherGroupValid(currentUser)) {
            return false;
        }

        return currentUser.getGroups().stream()
                .anyMatch(group -> group.getId() == groupId);
    }



    @Override
    public Boolean isUserAllowedToEnterSubject(Long subjectId, String userEmail) {
        UserEntity user = userEntityService.fetchUserByEmail(userEmail);
        Subject subject = subjectService.fetchSubjectById(subjectId);

        switch (user.getRole().getName()) {
            case AuthenticationRoles.ROLE_SUPER_TEACHER -> {
                return isSuperTeacherAllowedForSubject((SuperTeacher) user, subject);
            }
            case AuthenticationRoles.ROLE_TEACHER -> {
                return isTeacherAllowedForSubject((Teacher) user, subject);
            }
            case AuthenticationRoles.ROLE_STUDENT -> {
                return isStudentAllowedForSubject((Student) user, subject);
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public Boolean isUserAllowedToEditSubject(Long subjectId, String userEmail) {
        UserEntity currentUser = userEntityService.fetchUserByEmail(userEmail);
        Subject subject = subjectService.fetchSubjectById(subjectId);

        if (!isSuperTeacherGroupValid(subject.getGroup().getSuperTeacher())) {
            return false;
        }

        return isSubjectTeacherOrSuperTeacher(currentUser, subject);
    }

    @Override
    public Boolean isUserAllowedToEnterGroup(Long groupId, String userEmail) {
        UserEntity user = userEntityService.fetchUserByEmail(userEmail);
        Group group = groupService.fetchGroupById(groupId);
        if(user instanceof SuperTeacher superTeacher){
            if(!isSuperTeacherGroupValid(superTeacher)){
                return false;
            }
            return superTeacher.getGroups().stream().anyMatch(g -> g.getId() == groupId);
        }
        if(user instanceof Teacher teacher){
            return teacher.getSubjects().stream().anyMatch(subject -> subject.getGroup().getId() == groupId);
        }
        if(user instanceof Student student){
            return student.getGroups().stream().anyMatch(g -> g.getId() == groupId);
        }
        return false;
    }


    private @NotNull Boolean isSuperTeacherAllowedForSubject(SuperTeacher superTeacher, @NotNull Subject subject) {
        if (!isSuperTeacherGroupValid(subject.getGroup().getSuperTeacher())) {
            return false;
        }
        return superTeacher.getGroups().stream()
                .anyMatch(group -> group.getSubjects().stream()
                        .anyMatch(s -> s.getId().equals(subject.getId())));
    }

    private @NotNull Boolean isTeacherAllowedForSubject(Teacher teacher, @NotNull Subject subject) {
        if (!isSuperTeacherGroupValid(subject.getGroup().getSuperTeacher())) {
            return false;
        }
        return subject.getTeacher().getId().equals(teacher.getId());
    }

    private @NotNull Boolean isStudentAllowedForSubject(@NotNull Student student, Subject subject) {
        Set<Group> groups = student.getGroups();

        if(groups.stream().noneMatch(group -> group.getId() == subject.getGroup().getId())){
            return false;
        }

        if(subject.getGroup().getSuperTeacher().getRole().getName().equals(AuthenticationRoles.ROLE_ADMIN)){
            if(student.getStudentOfferRequests().stream().noneMatch(
                    studentOfferRequest -> isValidOfferRequest(studentOfferRequest.getStudentOffer().getGroup().getId(), subject.getGroup().getId(), studentOfferRequest.getStatus(), studentOfferRequest.getEndDate())
            )){
                return false;
            }
        }
        if(subject.getGroup().getSuperTeacher().getRole().getName().equals(AuthenticationRoles.ROLE_SUPER_TEACHER)) {
            if(subject.getGroup().getSuperTeacher().getTeacherOfferRequests().stream().noneMatch(
                    teacherOfferRequest -> teacherOfferRequest.getStatus().equals("ACCEPTED") && teacherOfferRequest.getEndDate().isAfter(LocalDateTime.now())
            )){
                return false;
            }
        }

        return true;
    }

    private @NotNull Boolean isStudentInGroup(Student student, @NotNull Subject subject) {
        return subject.getGroup().getStudents().stream()
                .anyMatch(s -> s.getId().equals(student.getId()));
    }

    private @NotNull Boolean isValidOfferRequest(@NotNull Long requestGroupId, Long subjectGroupId, String status, LocalDateTime endDate) {
        return requestGroupId.equals(subjectGroupId) &&
                "ACCEPTED".equals(status) &&
                endDate.isAfter(LocalDateTime.now());
    }

    private @NotNull Boolean isSubjectTeacherOrSuperTeacher(@NotNull UserEntity user, @NotNull Subject subject) {
        return subject.getTeacher().getId().equals(user.getId()) ||
                subject.getGroup().getSuperTeacher().getId().equals(user.getId());
    }

    private @NotNull Boolean isSuperTeacherGroupValid(@NotNull SuperTeacher superTeacher) {
        if(superTeacher.getRole().getName().equals(AuthenticationRoles.ROLE_ADMIN)){
            return true;
        }
        return superTeacher.getTeacherOfferRequests().stream()
                .anyMatch(teacherOfferRequest -> "ACCEPTED".equals(teacherOfferRequest.getStatus()) &&
                        teacherOfferRequest.getEndDate().isAfter(LocalDateTime.now()));
    }
}
