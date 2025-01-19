package mindpath.core.service.offer;

import mindpath.config.AuthenticationRoles;
import mindpath.core.domain.auth.superteacher.SuperTeacher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OfferSecurityServiceImpl implements OfferSecurityService {

    public void isTeacherOfferStillActive(SuperTeacher superTeacher) {
        if (superTeacher.getRole().getName().equals(AuthenticationRoles.ROLE_ADMIN)) {
            return;
        }
        if (superTeacher.getRole().getName().equals(AuthenticationRoles.ROLE_SUPER_TEACHER)) {
            boolean isAnyActive = superTeacher
                    .getTeacherOfferRequests()
                    .stream()
                    .anyMatch(
                            teacherOfferRequest ->
                                    teacherOfferRequest.getStatus().equals("ACCEPTED") &&
                                    teacherOfferRequest.getEndDate().isAfter(LocalDateTime.now())
                    );
            if (!isAnyActive) {
                throw new IllegalArgumentException("Vous n'avez aucune offre active");
            }
        }
    }
}
