package mindpath.core.service.offer;

import mindpath.core.domain.auth.superteacher.SuperTeacher;

public interface OfferSecurityService {
    void isTeacherOfferStillActive(SuperTeacher superTeacher);
}
