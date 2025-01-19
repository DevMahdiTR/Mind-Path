package mindpath.core.service.offer;


import mindpath.core.domain.offer.StudentOfferDTO;
import mindpath.core.domain.offer.TeacherOfferDTO;
import mindpath.core.domain.offer.request.StudentOfferRequestDTO;
import mindpath.core.domain.offer.request.TeacherOfferRequestDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OfferService {


    StudentOfferRequestDTO respondToStudentOfferRequest(@NotNull final Long studentOfferRequestId, @NotNull final String status);
    TeacherOfferRequestDTO respondToTeacherOfferRequest(@NotNull final Long teacherOfferRequestId, @NotNull final String status);
    List<TeacherOfferRequestDTO> fetchAllTeacherOfferRequests();
    List<StudentOfferRequestDTO> fetchAllStudentOfferRequests();
    List<StudentOfferRequestDTO> fetchCurrentStudentOfferRequests(@NotNull final UserDetails userDetails);
    List<TeacherOfferRequestDTO> fetchCurrentTeacherOfferRequests(@NotNull final UserDetails userDetails);

    TeacherOfferRequestDTO sendTeacherOfferRequest(@NotNull final UserDetails userDetails, @NotNull final MultipartFile paymentImage, @NotNull final Long teacherOfferId) throws IOException;
    StudentOfferRequestDTO sendStudentOfferRequest(@NotNull final UserDetails userDetails, @NotNull final MultipartFile paymentImage, @NotNull final Long studentOfferId) throws IOException;

    StudentOfferDTO createStudentOffer(@NotNull UserDetails userDetails ,@NotNull final MultipartFile image, @NotNull final String studentOfferDTOJson) throws IOException;
    TeacherOfferDTO createTeacherOffer(@NotNull final MultipartFile image, @NotNull final String teacherOfferDTOJson) throws IOException;

    StudentOfferDTO updatedStudentOfferById(@NotNull Long studentOfferId, MultipartFile newImage, @NotNull String studentOfferDTOJson) throws IOException;
    TeacherOfferDTO updateTeacherOfferById(@NotNull Long teacherOfferId, MultipartFile newImage, @NotNull String teacherOfferDTOJson) throws IOException;

    List<StudentOfferDTO> fetchAllStudentOffers(@NotNull final UserDetails userDetails);
    List<TeacherOfferDTO> fetchAllTeacherOffers(@NotNull final UserDetails userDetails);

    TeacherOfferDTO fetchTeacherOfferById(final long teacherOfferId);
    StudentOfferDTO fetchStudentOfferById(final long studentOfferId);

    void deleteTeacherOfferById(final long teacherOfferId);
    void deleteStudentOfferById(final long studentOfferId);


}
