package mindpath.core.repository;

import mindpath.core.domain.auth.superteacher.SuperTeacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperTeacherRepository extends JpaRepository<SuperTeacher, Integer> {
}
