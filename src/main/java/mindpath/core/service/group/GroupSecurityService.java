package mindpath.core.service.group;

public interface GroupSecurityService {


    Boolean isSuperTeacherAllowedToEditGroup(Long groupId, String userEmail);
    Boolean isUserAllowedToEnterSubject(Long subjectId, String userEmail);
    Boolean isUserAllowedToEditSubject(Long subjectId, String userEmail);
    Boolean isUserAllowedToEnterGroup(Long groupId, String userEmail);
}
