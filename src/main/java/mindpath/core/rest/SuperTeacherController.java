package mindpath.core.rest;

import mindpath.config.APIRouters;
import mindpath.core.domain.auth.student.StudentDTO;
import mindpath.core.domain.auth.teacher.TeacherDTO;
import mindpath.core.service.superteacher.SuperTeacherService;
import mindpath.core.utility.CustomerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(APIRouters.SUPER_TEACHER_ROUTER)
public class SuperTeacherController {


    private final SuperTeacherService superTeacherService;

    public SuperTeacherController(SuperTeacherService superTeacherService) {
        this.superTeacherService = superTeacherService;
    }


    @GetMapping("/all-teacher")
    public CustomerResponse<List<TeacherDTO>> getAllTeacher(@AuthenticationPrincipal final UserDetails userDetails){
        return new CustomerResponse<>(superTeacherService.getAllTeacher(userDetails), HttpStatus.OK);
    }

    @GetMapping("/all-student")
    public CustomerResponse<List<StudentDTO>> getAllStudent(@AuthenticationPrincipal final UserDetails userDetails){
        return new CustomerResponse<>(superTeacherService.getAllStudent(userDetails), HttpStatus.OK);
    }

    @PutMapping("/add-teacher")
    public CustomerResponse<String> addTeacher(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam("teacherId") final UUID teacherId
            ){
        return new CustomerResponse<>(superTeacherService.addTeacherToSuperTeacher(userDetails, teacherId), HttpStatus.OK);
    }

    @PutMapping("/remove-teacher")
    public CustomerResponse<String> removeTeacher(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam("teacherId") final UUID teacherId
    ){
        return new CustomerResponse<>(superTeacherService.removeTeacherFromSuperTeacher(userDetails, teacherId), HttpStatus.OK);
    }
}
