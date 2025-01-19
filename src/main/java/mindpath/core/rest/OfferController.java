package mindpath.core.rest;

import mindpath.config.APIRouters;
import mindpath.core.domain.offer.StudentOfferDTO;
import mindpath.core.domain.offer.TeacherOfferDTO;
import mindpath.core.domain.offer.request.StudentOfferRequestDTO;
import mindpath.core.domain.offer.request.TeacherOfferRequestDTO;
import mindpath.core.service.offer.OfferService;
import mindpath.core.utility.CustomerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(APIRouters.OFFER_ROUTER)
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PutMapping("/teacher-request/{teacherOfferRequestId}")
    public CustomerResponse<TeacherOfferRequestDTO> respondToTeacherOfferRequest(
            @PathVariable(name = "teacherOfferRequestId") final Long teacherOfferRequestId,
            @RequestParam(name = "status") final String status) {
        return new CustomerResponse<>(offerService.respondToTeacherOfferRequest(teacherOfferRequestId, status), HttpStatus.OK);
    }

    @PutMapping("/student-request/{studentOfferRequestId}")
    public CustomerResponse<StudentOfferRequestDTO> respondToStudentOfferRequest(
            @PathVariable(name = "studentOfferRequestId") final Long studentOfferRequestId,
            @RequestParam(name = "status") final String status) {
        return new CustomerResponse<>(offerService.respondToStudentOfferRequest(studentOfferRequestId, status), HttpStatus.OK);
    }

    @GetMapping("/teacher-requests")
    public CustomerResponse<List<TeacherOfferRequestDTO>> fetchAllTeacherOfferRequests() {
        return new CustomerResponse<>(offerService.fetchAllTeacherOfferRequests(), HttpStatus.OK);
    }

    @GetMapping("/student-requests")
    public CustomerResponse<List<StudentOfferRequestDTO>> fetchAllStudentOfferRequests() {
        return new CustomerResponse<>(offerService.fetchAllStudentOfferRequests(), HttpStatus.OK);
    }

    @PostMapping("/teacher-request/{teacherOfferId}")
    public CustomerResponse<TeacherOfferRequestDTO> sendTeacherOfferRequest(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(name = "paymentImage") final MultipartFile paymentImage,
            @PathVariable(name = "teacherOfferId") final Long teacherOfferId) throws IOException {
        return new CustomerResponse<>(offerService.sendTeacherOfferRequest(userDetails, paymentImage, teacherOfferId), HttpStatus.OK);
    }

    @PostMapping("/student-request/{studentOfferId}")
    public CustomerResponse<StudentOfferRequestDTO> sendStudentOfferRequest(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(name = "paymentImage") final MultipartFile paymentImage,
            @PathVariable(name = "studentOfferId") final Long studentOfferId) throws IOException {
        return new CustomerResponse<>(offerService.sendStudentOfferRequest(userDetails, paymentImage, studentOfferId), HttpStatus.OK);
    }

    @PostMapping("/student")
    public CustomerResponse<StudentOfferDTO> createStudentOffer(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(name = "image") final MultipartFile image,
            @RequestParam(name = "studentOfferDTOJson") final String studentOfferDTOJson) throws IOException{
        return new CustomerResponse<>(offerService.createStudentOffer(userDetails,image, studentOfferDTOJson), HttpStatus.OK);
    }

    @PostMapping("/teacher")
    public CustomerResponse<TeacherOfferDTO> createTeacherOffer(
            @RequestParam(name = "image") final MultipartFile image,
            @RequestParam(name = "teacherOfferDTOJson") final String teacherOfferDTOJson) throws IOException{
        return new CustomerResponse<>(offerService.createTeacherOffer(image, teacherOfferDTOJson), HttpStatus.OK);
    }

    @PutMapping("/student/{studentOfferId}")
    public CustomerResponse<StudentOfferDTO> updateStudentOfferById(
            @PathVariable(name = "studentOfferId") final Long studentOfferId,
            @RequestParam(name = "image", required = false) final MultipartFile newImage,
            @RequestParam(name = "studentOfferDTOJson") final String studentOfferDTOJson) throws IOException{
        return new CustomerResponse<>(offerService.updatedStudentOfferById(studentOfferId, newImage, studentOfferDTOJson), HttpStatus.OK);
    }

    @PutMapping("/teacher/{teacherOfferId}")
    public CustomerResponse<TeacherOfferDTO> updateTeacherOfferById(
            @PathVariable(name = "teacherOfferId") final Long teacherOfferId,
            @RequestParam(name = "image", required = false) final MultipartFile newImage,
            @RequestParam(name = "teacherOfferDTOJson") final String teacherOfferDTOJson) throws IOException{
        return new CustomerResponse<>(offerService.updateTeacherOfferById(teacherOfferId, newImage, teacherOfferDTOJson), HttpStatus.OK);
    }

    @GetMapping("/student")
    public CustomerResponse<List<StudentOfferDTO>> fetchAllStudentOffers(@AuthenticationPrincipal final UserDetails userDetails){
        return new CustomerResponse<>(offerService.fetchAllStudentOffers(userDetails), HttpStatus.OK);
    }

    @GetMapping("/teacher")
    public CustomerResponse<List<TeacherOfferDTO>> fetchAllTeacherOffers(@AuthenticationPrincipal final UserDetails userDetails){
        return new CustomerResponse<>(offerService.fetchAllTeacherOffers(userDetails), HttpStatus.OK);
    }

    @GetMapping("/teacher/{teacherOfferId}")
    public CustomerResponse<TeacherOfferDTO> fetchTeacherOfferById(@PathVariable(name = "teacherOfferId") final long teacherOfferId){
        return new CustomerResponse<>(offerService.fetchTeacherOfferById(teacherOfferId), HttpStatus.OK);
    }

    @GetMapping("/student/{studentOfferId}")
    public CustomerResponse<StudentOfferDTO> fetchStudentOfferById(@PathVariable(name = "studentOfferId") final long studentOfferId){
        return new CustomerResponse<>(offerService.fetchStudentOfferById(studentOfferId), HttpStatus.OK);
    }

    @GetMapping("/student/current-requests")
    public CustomerResponse<List<StudentOfferRequestDTO>> fetchCurrentStudentOfferRequests(@AuthenticationPrincipal final UserDetails userDetails){
        return new CustomerResponse<>(offerService.fetchCurrentStudentOfferRequests(userDetails), HttpStatus.OK);
    }

    @GetMapping("/teacher/current-requests")
    public CustomerResponse<List<TeacherOfferRequestDTO>> fetchCurrentTeacherOfferRequests(@AuthenticationPrincipal final UserDetails userDetails){
        return new CustomerResponse<>(offerService.fetchCurrentTeacherOfferRequests(userDetails), HttpStatus.OK);
    }

    @DeleteMapping("/teacher/{teacherOfferId}")
    public CustomerResponse<String> deleteTeacherOfferById(@PathVariable(name = "teacherOfferId") final long teacherOfferId){
        offerService.deleteTeacherOfferById(teacherOfferId);
        return new CustomerResponse<>("Teacher offer deleted successfully", HttpStatus.OK);
    }

    @DeleteMapping("/student/{studentOfferId}")
    public CustomerResponse<String> deleteStudentOfferById(@PathVariable(name = "studentOfferId") final long studentOfferId){
        offerService.deleteStudentOfferById(studentOfferId);
        return new CustomerResponse<>("Student offer deleted successfully", HttpStatus.OK);
    }



}
