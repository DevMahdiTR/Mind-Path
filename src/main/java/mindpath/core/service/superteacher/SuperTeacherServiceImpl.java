package mindpath.core.service.superteacher;

import mindpath.core.domain.auth.student.Student;
import mindpath.core.domain.auth.student.StudentDTO;
import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.auth.teacher.Teacher;
import mindpath.core.domain.auth.teacher.TeacherDTO;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.group.Group;
import mindpath.core.domain.subject.Subject;
import mindpath.core.mapper.StudentDTOMapper;
import mindpath.core.mapper.TeacherDTOMapper;
import mindpath.core.repository.SuperTeacherRepository;
import mindpath.core.repository.subject.SubjectRepository;
import mindpath.core.service.user.UserEntityService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class SuperTeacherServiceImpl implements SuperTeacherService{

    private final UserEntityService userEntityService;
    private final SuperTeacherRepository superTeacherRepository;
    private final TeacherDTOMapper teacherDTOMapper;
    private final StudentDTOMapper studentDTOMapper;
    private final SubjectRepository subjectRepository;

    public SuperTeacherServiceImpl(UserEntityService userEntityService, SuperTeacherRepository superTeacherRepository, TeacherDTOMapper teacherDTOMapper, StudentDTOMapper studentDTOMapper, SubjectRepository subjectRepository) {
        this.userEntityService = userEntityService;
        this.superTeacherRepository = superTeacherRepository;
        this.teacherDTOMapper = teacherDTOMapper;
        this.studentDTOMapper = studentDTOMapper;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public List<TeacherDTO> getAllTeacher(UserDetails userDetails) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        SuperTeacher superTeacher = userEntityService.validateSuperTeacher(currentUser);
        Set<Teacher> teachers = superTeacher.getTeachers();
        return teachers.stream().map(teacherDTOMapper).toList();
    }

    @Override
    public List<StudentDTO> getAllStudent(UserDetails userDetails) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        SuperTeacher superTeacher = userEntityService.validateSuperTeacher(currentUser);
        List<Student> students = new ArrayList<>();
        for(Group group : superTeacher.getGroups()){
            students.addAll(group.getStudents());
        }

        return students.stream().map(studentDTOMapper).toList();
    }

    @Override
    public String addTeacherToSuperTeacher(UserDetails userDetails, UUID teacherId) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        final UserEntity user = userEntityService.fetchUserByUUID(teacherId);
        SuperTeacher superTeacher = userEntityService.validateSuperTeacher(currentUser);
        Teacher teacher = userEntityService.validateTeacher(user);

        if(superTeacher.getTeachers().contains(teacher)){
            throw new IllegalStateException("L'enseignant est déjà ajouté au Super Enseignant.");
        }
        superTeacher.getTeachers().add(teacher);

        userEntityService.saveAndFlush(superTeacher);

        return "L'enseignant a été ajouté au Super Enseignant.";
    }

    @Override
    public String removeTeacherFromSuperTeacher(UserDetails userDetails, UUID teacherId) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        final UserEntity user = userEntityService.fetchUserByUUID(teacherId);
        SuperTeacher superTeacher = userEntityService.validateSuperTeacher(currentUser);
        Teacher teacher = userEntityService.validateTeacher(user);

        if(!superTeacher.getTeachers().contains(teacher)){
            throw new IllegalStateException("L'enseignant n'a pas été ajouté au Super Enseignant.");
        }

        List<Subject> teachersSubjects = teacher.getSubjects();
        for(Subject subject : teachersSubjects){
            if(subject.getGroup().getSuperTeacher().getId().equals(superTeacher.getId())){
                subject.setTeacher(superTeacher);
            }
        }
        subjectRepository.saveAll(teachersSubjects);
        superTeacher.getTeachers().remove(teacher);

        userEntityService.saveAndFlush(superTeacher);

        return "L'enseignant a été retiré du Super Enseignant";
    }
}
