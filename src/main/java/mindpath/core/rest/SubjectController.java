package mindpath.core.rest;

import mindpath.config.APIRouters;
import mindpath.core.domain.subject.SubjectDTO;
import mindpath.core.service.subject.SubjectService;
import mindpath.core.utility.CustomerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(APIRouters.SUBJECT_ROUTER)
public class SubjectController {

    private final SubjectService subjectService;


    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping("/all")
    public CustomerResponse<List<SubjectDTO>> findAllSubjects() {
        return new CustomerResponse<>(subjectService.findAllSubjects(), HttpStatus.OK);
    }

    @PostMapping()
    public CustomerResponse<SubjectDTO> createSubject(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(value = "groupId", required = true) final Long groupId,
            @RequestParam(value = "subjectDTOJson" , required = true) final String subjectDTOJson,
            @RequestParam(value = "mainImage", required = true) final MultipartFile mainImage,
            @RequestParam(value = "backgroundImage", required = true) final MultipartFile backgroundImage
            ) throws IOException {
        return new CustomerResponse<>(subjectService.createSubject(userDetails, groupId, subjectDTOJson, mainImage, backgroundImage), HttpStatus.CREATED);
    }

    @PutMapping("/{subjectId}")
    public CustomerResponse<SubjectDTO> updateSubject(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable(value = "subjectId") final Long subjectId,
            @RequestParam(value = "subjectDTOJson" , required = true) final String subjectDTOJson,
            @RequestParam(value = "mainImage", required = false) final MultipartFile mainImage,
            @RequestParam(value = "backgroundImage", required = false) final MultipartFile backgroundImage
    ) throws IOException {
        return new CustomerResponse<>(subjectService.updateSubject(userDetails, subjectId, subjectDTOJson, mainImage, backgroundImage), HttpStatus.OK);
    }

    @GetMapping("/{subjectId}")
    public CustomerResponse<SubjectDTO> getSubjectById(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable(value = "subjectId") final Long subjectId
    ) {
        return new CustomerResponse<>(subjectService.getSubjectById(userDetails, subjectId), HttpStatus.OK);
    }

    @GetMapping("/group/{groupId}")
    public CustomerResponse<List<SubjectDTO>> getSubjectsByGroupId(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable(value = "groupId") final Long groupId
    ) {
        return new CustomerResponse<>(subjectService.getSubjectsByGroupId(userDetails, groupId), HttpStatus.OK);
    }

    @GetMapping()
    public CustomerResponse<List<SubjectDTO>> allSubjects(
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return new CustomerResponse<>(subjectService.allSubjects(userDetails), HttpStatus.OK);
    }

    @DeleteMapping("/{subjectId}")
    public CustomerResponse<String> deleteSubject(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable(value = "subjectId") final Long subjectId
    ) {
        subjectService.deleteSubject(userDetails, subjectId);
        return new CustomerResponse<>("Subject deleted successfully", HttpStatus.OK);
    }

}
