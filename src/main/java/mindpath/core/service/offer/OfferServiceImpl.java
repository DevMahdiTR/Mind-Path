package mindpath.core.service.offer;

import com.fasterxml.jackson.databind.ObjectMapper;
import mindpath.config.AuthenticationRoles;
import mindpath.core.domain.auth.student.Student;
import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.group.Group;
import mindpath.core.domain.offer.StudentOffer;
import mindpath.core.domain.offer.StudentOfferDTO;
import mindpath.core.domain.offer.TeacherOffer;
import mindpath.core.domain.offer.TeacherOfferDTO;
import mindpath.core.domain.offer.request.StudentOfferRequest;
import mindpath.core.domain.offer.request.StudentOfferRequestDTO;
import mindpath.core.domain.offer.request.TeacherOfferRequest;
import mindpath.core.domain.offer.request.TeacherOfferRequestDTO;
import mindpath.core.exceptions.custom.ResourceNotFoundException;
import mindpath.core.mapper.StudentOfferDTOMapper;
import mindpath.core.mapper.StudentOfferRequestDTOMapper;
import mindpath.core.mapper.TeacherOfferDTOMapper;
import mindpath.core.mapper.TeacherOfferRequestDTOMapper;
import mindpath.core.repository.offer.StudentOfferRepository;
import mindpath.core.repository.offer.StudentOfferRequestRepository;
import mindpath.core.repository.offer.TeacherOfferRepository;
import mindpath.core.repository.offer.TeacherOfferRequestRepository;
import mindpath.core.service.group.GroupService;
import mindpath.core.service.firstbasestorage.AzureStorageService;
import mindpath.core.service.user.UserEntityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final ObjectMapper objectMapper;
    private final GroupService groupService;
    private final AzureStorageService azureStorageService;
    private final UserEntityService userEntityService;

    private final StudentOfferRepository studentOfferRepository;
    private final StudentOfferDTOMapper studentOfferDTOMapper;
    private final TeacherOfferRepository teacherOfferRepository;
    private final TeacherOfferDTOMapper teacherOfferDTOMapper;


    private final StudentOfferRequestRepository studentOfferRequestRepository;
    private final StudentOfferRequestDTOMapper studentOfferRequestDTOMapper;
    private final TeacherOfferRequestRepository teacherOfferRequestRepository;
    private final TeacherOfferRequestDTOMapper teacherOfferRequestDTOMapper;

    public StudentOfferRequestDTO respondToStudentOfferRequest(@NotNull final Long studentOfferRequestId, @NotNull final String status) {
        StudentOfferRequest studentOfferRequest = getStudentOfferRequestById(studentOfferRequestId);
        validateStatus(status);
        if (status.equalsIgnoreCase("ACCEPTED")) {
            studentOfferRequest.setStartDate(LocalDateTime.now());
            studentOfferRequest.setEndDate(LocalDateTime.now().plusMonths(studentOfferRequest.getStudentOffer().getMonthlyPeriod()));
            if (!studentOfferRequest.getStudentOffer().getGroup().getStudents().contains(studentOfferRequest.getStudent())) {
                studentOfferRequest.getStudentOffer().getGroup().getStudents().add(studentOfferRequest.getStudent());
                groupService.save(studentOfferRequest.getStudentOffer().getGroup());
            }
        }
        if (status.equalsIgnoreCase("REJECTED")) {
            studentOfferRequest.setEndDate(LocalDateTime.now().minusDays(1));
        }
        studentOfferRequest.setStatus(status);
        studentOfferRequestRepository.saveAndFlush(studentOfferRequest);
        log.debug("Student offer request with ID: {} has been {}", studentOfferRequestId, status);

        return studentOfferRequestDTOMapper.apply(studentOfferRequest);
    }

    public TeacherOfferRequestDTO respondToTeacherOfferRequest(@NotNull final Long teacherOfferRequestId, @NotNull final String status) {
        TeacherOfferRequest teacherOfferRequest = getTeacherOfferRequestById(teacherOfferRequestId);
        validateStatus(status);
        if (status.equalsIgnoreCase("ACCEPTED")) {
            teacherOfferRequest.setStartDate(LocalDateTime.now());
            teacherOfferRequest.setEndDate(LocalDateTime.now().plusMonths(teacherOfferRequest.getTeacherOffer().getMonthlyPeriod()));
        }
        if (status.equalsIgnoreCase("REJECTED")) {
            teacherOfferRequest.setEndDate(LocalDateTime.now().minusDays(1));
        }
        teacherOfferRequest.setStatus(status);
        teacherOfferRequestRepository.saveAndFlush(teacherOfferRequest);
        log.debug("Teacher offer request with ID: {} has been {}", teacherOfferRequestId, status);

        return teacherOfferRequestDTOMapper.apply(teacherOfferRequest);
    }

    public List<TeacherOfferRequestDTO> fetchAllTeacherOfferRequests() {
        return teacherOfferRequestRepository.findAll().stream().map(teacherOfferRequestDTOMapper).toList();
    }

    public List<StudentOfferRequestDTO> fetchAllStudentOfferRequests() {
        return studentOfferRequestRepository.findAll().stream().map(studentOfferRequestDTOMapper).toList();
    }

    public List<StudentOfferRequestDTO> fetchCurrentStudentOfferRequests(@NotNull final UserDetails userDetails) {
        UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        Student student = validateUserIsStudent(currentUser);
        return student.getStudentOfferRequests().stream().distinct().map(studentOfferRequestDTOMapper).toList();
    }

    public List<TeacherOfferRequestDTO> fetchCurrentTeacherOfferRequests(@NotNull final UserDetails userDetails) {
        UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        SuperTeacher superTeacher = validateUserIsTeacher(currentUser);
        return superTeacher.getTeacherOfferRequests().stream().distinct().map(teacherOfferRequestDTOMapper).distinct().toList();
    }


    public StudentOfferRequestDTO sendStudentOfferRequest(@NotNull final UserDetails userDetails, @NotNull final MultipartFile paymentImage, @NotNull final Long studentOfferId) throws IOException {
        if (!isValidImageFile(paymentImage.getContentType())) {
            throw new IllegalArgumentException("Le type de fichier n'est pas valide");
        }

        UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        Student student = validateUserIsStudent(currentUser);
        StudentOffer studentOffer = getStudentOfferById(studentOfferId);
        StudentOfferRequest currentStudentOfferRequest = null;
        if (student.getStudentOfferRequests().stream().anyMatch(studentOfferRequest -> studentOfferRequest.getStudentOffer().getId().equals(studentOffer.getId()))) {

            StudentOfferRequest checkStudentOfferRequest = student.getStudentOfferRequests().stream().filter(studentOfferRequest -> studentOfferRequest.getStudentOffer().getId().equals(studentOffer.getId())).findFirst().get();
            if (checkStudentOfferRequest.getStatus().equalsIgnoreCase("PENDING") || checkStudentOfferRequest.getStatus().equalsIgnoreCase("ACCEPTED")) {
                throw new IllegalArgumentException("Offre avec l'ID : %d est déjà %s.".formatted(studentOffer.getId(), checkStudentOfferRequest.getStatus()));
            }
            if (checkStudentOfferRequest.getEndDate().isAfter(LocalDateTime.now().plusDays(1))) {
                throw new IllegalArgumentException("Offre avec l'ID : %d déjà demandée.".formatted(studentOffer.getId()));
            }
            currentStudentOfferRequest = checkStudentOfferRequest;
        }
        String paymentImageUrl = azureStorageService.uploadFile(paymentImage, "payments");
        StudentOfferRequest studentOfferRequest = StudentOfferRequest.builder()
                .id(currentStudentOfferRequest != null ? currentStudentOfferRequest.getId() : null)
                .student(student)
                .studentOffer(studentOffer)
                .paymentImageUrl(paymentImageUrl)
                .status("PENDING")
                .requestDate(LocalDateTime.now())
                .build();
        studentOfferRequestRepository.saveAndFlush(studentOfferRequest);
        log.debug("Offer request created with ID: {}", studentOfferRequest.getId());

        return studentOfferRequestDTOMapper.apply(studentOfferRequest);
    }

    public TeacherOfferRequestDTO sendTeacherOfferRequest(@NotNull final UserDetails userDetails, @NotNull final MultipartFile paymentImage, @NotNull final Long teacherOfferId) throws IOException {
        if (!isValidImageFile(paymentImage.getContentType())) {
            throw new IllegalArgumentException("Le type de fichier n'est pas valide");
        }

        UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        SuperTeacher superTeacher = validateUserIsTeacher(currentUser);
        TeacherOffer teacherOffer = getTeacherOfferById(teacherOfferId);

        TeacherOfferRequest currentTeacherOfferRequest = null;

        if (superTeacher.getTeacherOfferRequests().stream().anyMatch(teacherOfferRequest -> teacherOfferRequest.getTeacherOffer().getId().equals(teacherOffer.getId()))) {
            TeacherOfferRequest checkTeacherOfferRequest = superTeacher.getTeacherOfferRequests().stream().filter(teacherOfferRequest -> teacherOfferRequest.getTeacherOffer().getId().equals(teacherOffer.getId())).findFirst().get();
            if (checkTeacherOfferRequest.getStatus().equalsIgnoreCase("PENDING") || checkTeacherOfferRequest.getStatus().equalsIgnoreCase("ACCEPTED")) {
                throw new IllegalArgumentException("Offre avec l'ID : %d est déjà %s.".formatted(teacherOffer.getId(), checkTeacherOfferRequest.getStatus()));
            }
            if (checkTeacherOfferRequest.getEndDate().isAfter(LocalDateTime.now().plusDays(1))) {
                throw new IllegalArgumentException("Offre avec l'ID : %d déjà demandée.".formatted(teacherOffer.getId()));
            }
            currentTeacherOfferRequest = checkTeacherOfferRequest;
        }

        String paymentImageUrl = azureStorageService.uploadFile(paymentImage, "payments");
        TeacherOfferRequest teacherOfferRequest = TeacherOfferRequest.builder()
                .id(currentTeacherOfferRequest != null ? currentTeacherOfferRequest.getId() : null)
                .superTeacher(superTeacher)
                .teacherOffer(teacherOffer)
                .paymentImageUrl(paymentImageUrl)
                .status("PENDING")
                .requestDate(LocalDateTime.now())
                .build();
        teacherOfferRequestRepository.saveAndFlush(teacherOfferRequest);
        log.debug("Offer request created with ID: {}", teacherOfferRequest.getId());

        return teacherOfferRequestDTOMapper.apply(teacherOfferRequest);
    }

    public StudentOfferDTO createStudentOffer(@NotNull UserDetails userDetails, @NotNull final MultipartFile image, @NotNull final String studentOfferDTOJson) throws IOException {
        if (!isValidImageFile(image.getContentType())) {
            throw new IllegalArgumentException("Le type de fichier n'est pas valide");
        }

        final StudentOfferDTO studentOfferDTO = objectMapper.readValue(studentOfferDTOJson, StudentOfferDTO.class);
        validatedStudentOffer(studentOfferDTO);
        UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        SuperTeacher superTeacher = userEntityService.validateSuperTeacher(currentUser);
        log.debug("Creating offer with details: {}", studentOfferDTO);

        if (superTeacher.getGroups().stream().noneMatch(group -> group.getId() == studentOfferDTO.getClassId())) {
            throw new IllegalArgumentException("L'ID du groupe n'est pas valide");
        }

        final Group group = groupService.fetchGroupById(studentOfferDTO.getClassId());

        final String imageUrl = azureStorageService.uploadFile(image, "offers");
        StudentOffer studentOffer = StudentOffer.builder()
                .title(studentOfferDTO.getTitle())
                .subTitle(studentOfferDTO.getSubTitle())
                .description(studentOfferDTO.getDescription())
                .price(studentOfferDTO.getPrice())
                .monthlyPeriod(studentOfferDTO.getMonthlyPeriod())
                .createdAt(LocalDateTime.now())
                .imageUrl(imageUrl)
                .group(group)
                .offerDetails(studentOfferDTO.getOfferDetails())
                .build();

        studentOfferRepository.saveAndFlush(studentOffer);
        log.debug("Offer created with ID: {}", studentOffer.getId());

        return mapStudentOfferToDTO(studentOffer);
    }

    public TeacherOfferDTO createTeacherOffer(@NotNull final MultipartFile image, @NotNull final String teacherOfferDTOJson) throws IOException {
        if (!isValidImageFile(image.getContentType())) {
            throw new IllegalArgumentException("Le type de fichier n'est pas valide");
        }

        final TeacherOfferDTO teacherOfferDTO = objectMapper.readValue(teacherOfferDTOJson, TeacherOfferDTO.class);
        validatedTeacherOffer(teacherOfferDTO);
        log.debug("Creating offer with details: {}", teacherOfferDTO);

        final String imageUrl = azureStorageService.uploadFile(image, "offers");
        TeacherOffer teacherOffer = TeacherOffer.builder()
                .title(teacherOfferDTO.getTitle())
                .subTitle(teacherOfferDTO.getSubTitle())
                .description(teacherOfferDTO.getDescription())
                .price(teacherOfferDTO.getPrice())
                .monthlyPeriod(teacherOfferDTO.getMonthlyPeriod())
                .createdAt(LocalDateTime.now())
                .imageUrl(imageUrl)
                .offerDetails(teacherOfferDTO.getOfferDetails())
                .classNumber(teacherOfferDTO.getClassNumber())
                .build();

        teacherOfferRepository.saveAndFlush(teacherOffer);
        log.debug("Offer created with ID: {}", teacherOffer.getId());

        return mapTeacherOfferToDTO(teacherOffer);
    }

    public StudentOfferDTO updatedStudentOfferById(@NotNull Long studentOfferId, MultipartFile newImage, @NotNull String studentOfferDTOJson) throws IOException {
        StudentOfferDTO studentOfferDTO = objectMapper.readValue(studentOfferDTOJson, StudentOfferDTO.class);
        validatedStudentOffer(studentOfferDTO);
        log.debug("OfferDTO: {}", studentOfferDTO);

        StudentOffer savedStudentOffer = getStudentOfferById(studentOfferId);
        Group group = groupService.fetchGroupById(studentOfferDTO.getClassId());

        if (newImage != null) {
            if (!isValidImageFile(newImage.getContentType())) {
                throw new IllegalArgumentException("Le type de fichier n'est pas valide");
            }
            azureStorageService.deleteFile(savedStudentOffer.getImageUrl());
            final String imageUrl = azureStorageService.uploadFile(newImage, "offers");
            savedStudentOffer.setImageUrl(imageUrl);
            log.debug("Image updated for offer with ID: {}", studentOfferId);
        }

        updateStudentOffer(savedStudentOffer, studentOfferDTO);

        studentOfferRepository.saveAndFlush(savedStudentOffer);
        log.debug("Offer updated with ID: {}", studentOfferId);

        return mapStudentOfferToDTO(savedStudentOffer);
    }

    public TeacherOfferDTO updateTeacherOfferById(@NotNull Long teacherOfferId, MultipartFile newImage, @NotNull String teacherOfferDTOJson) throws IOException {
        TeacherOfferDTO teacherOfferDTO = objectMapper.readValue(teacherOfferDTOJson, TeacherOfferDTO.class);
        validatedTeacherOffer(teacherOfferDTO);
        log.debug("OfferDTO: {}", teacherOfferDTO);

        TeacherOffer savedTeacherOffer = getTeacherOfferById(teacherOfferId);

        if (newImage != null) {
            if (!isValidImageFile(newImage.getContentType())) {
                throw new IllegalArgumentException("Le type de fichier n'est pas valide");
            }
            azureStorageService.deleteFile(savedTeacherOffer.getImageUrl());
            final String imageUrl = azureStorageService.uploadFile(newImage, "offers");
            savedTeacherOffer.setImageUrl(imageUrl);
            log.debug("Image updated for offer with ID: {}", teacherOfferId);
        }

        updateTeacherOffer(savedTeacherOffer, teacherOfferDTO);

        teacherOfferRepository.saveAndFlush(savedTeacherOffer);
        log.debug("Offer updated with ID: {}", teacherOfferId);

        return mapTeacherOfferToDTO(savedTeacherOffer);
    }

    public List<StudentOfferDTO> fetchAllStudentOffers(@NotNull final UserDetails userDetails) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        List<StudentOfferDTO> studentOffers = mapStudentOffersToDTO(studentOfferRepository.findAll());

        if (currentUser instanceof Student student) {
            studentOffers.forEach(studentOfferDTO -> {
                studentOfferDTO.setSubscribed(
                        student.getStudentOfferRequests().stream().anyMatch(
                                studentOfferRequest -> studentOfferRequest.getStudentOffer().getId().equals(studentOfferDTO.getId()) &&
                                        (
                                                        studentOfferRequest.getStatus().equalsIgnoreCase("PENDING") ||
                                                                (
                                                                        studentOfferRequest.getStatus().equalsIgnoreCase("ACCEPTED") &&
                                                                                (studentOfferRequest.getEndDate().isAfter(LocalDateTime.now()) &&
                                                                                        ChronoUnit.DAYS.between(LocalDateTime.now(), studentOfferRequest.getEndDate()) > 1)

                                                                )
                                        )
                        )
                );
            });
        }

        return studentOffers.stream().distinct().toList();
    }


    public List<TeacherOfferDTO> fetchAllTeacherOffers(@NotNull final UserDetails userDetails) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());

        List<TeacherOfferDTO> teacherOffers = mapTeacherOffersToDTO(teacherOfferRepository.findAll());

        if (currentUser instanceof SuperTeacher superTeacher) {
            teacherOffers.forEach(teacherOfferDTO -> {
                boolean isSubscribed = isOfferSubscribed(superTeacher, teacherOfferDTO);
                teacherOfferDTO.setSubscribed(isSubscribed);
            });
        }

        return teacherOffers.stream().distinct().toList();
    }

    private boolean isOfferSubscribed(SuperTeacher superTeacher, TeacherOfferDTO teacherOfferDTO) {
        return superTeacher.getTeacherOfferRequests().stream().anyMatch(teacherOfferRequest -> {
            boolean isSameOffer = teacherOfferRequest.getTeacherOffer().getId().equals(teacherOfferDTO.getId());
            String status = teacherOfferRequest.getStatus();

            if (!isSameOffer) {
                return false;
            }

            if ("PENDING".equalsIgnoreCase(status)) {
                return true;
            }

            if ("ACCEPTED".equalsIgnoreCase(status)) {
                LocalDateTime endDate = teacherOfferRequest.getEndDate();
                return endDate.isAfter(LocalDateTime.now()) && ChronoUnit.DAYS.between(LocalDateTime.now(), endDate) > 1;
            }

            return false;
        });
    }


    public TeacherOfferDTO fetchTeacherOfferById(final long teacherOfferId) {
        return mapTeacherOfferToDTO(getTeacherOfferById(teacherOfferId));
    }

    public StudentOfferDTO fetchStudentOfferById(final long studentOfferId) {
        return mapStudentOfferToDTO(getStudentOfferById(studentOfferId));
    }


    public void deleteTeacherOfferById(final long teacherOfferId) {
        TeacherOffer teacherOffer = getTeacherOfferById(teacherOfferId);
        azureStorageService.deleteFile(teacherOffer.getImageUrl());


        teacherOfferRepository.delete(teacherOffer);
        log.debug("Offer with ID: {} deleted", teacherOfferId);
    }

    public void deleteStudentOfferById(final long studentOfferId) {
        StudentOffer studentOffer = getStudentOfferById(studentOfferId);
        azureStorageService.deleteFile(studentOffer.getImageUrl());

        Group group = studentOffer.getGroup();
        List<Student> studentEnrolledWithStudentOfferRequest = studentOfferRequestRepository.findAll().stream()
                .filter(studentOfferRequest -> studentOfferRequest.getStudentOffer().getId().equals(studentOfferId))
                .map(StudentOfferRequest::getStudent)
                .toList();
        group.getStudents().removeAll(studentEnrolledWithStudentOfferRequest);
        groupService.save(group);
        studentOfferRepository.delete(studentOffer);
        log.debug("Offer with ID: {} deleted", studentOfferId);
    }


    public StudentOffer getStudentOfferById(final long studentOfferId) {
        return studentOfferRepository.fetchStudentOfferById(studentOfferId).orElseThrow(
                () -> new ResourceNotFoundException("Offre étudiante avec l'ID : " + studentOfferId + " non trouvée")
        );
    }


    public TeacherOffer getTeacherOfferById(final long teacherOfferId) {
        return teacherOfferRepository.fetchTeacherOfferById(teacherOfferId).orElseThrow(
                () -> new ResourceNotFoundException("Offre enseignant avec l'ID : " + teacherOfferId + " non trouvée")
        );
    }


    public StudentOfferRequest getStudentOfferRequestById(final long studentOfferRequestId) {
        return studentOfferRequestRepository.fetchStudentOfferRequestById(studentOfferRequestId).orElseThrow(
                () -> new ResourceNotFoundException("Demande d'offre étudiante avec l'ID : " + studentOfferRequestId + " non trouvée")
        );
    }


    public TeacherOfferRequest getTeacherOfferRequestById(final long teacherOfferRequestId) {
        return teacherOfferRequestRepository.fetchTeacherOfferRequestById(teacherOfferRequestId).orElseThrow(
                () -> new ResourceNotFoundException("Demande d'offre enseignant avec l'ID : " + teacherOfferRequestId + " non trouvée")
        );
    }


    private StudentOfferDTO mapStudentOfferToDTO(StudentOffer studentOffer) {
        return studentOfferDTOMapper.apply(studentOffer);
    }

    private TeacherOfferDTO mapTeacherOfferToDTO(TeacherOffer teacherOffer) {
        return teacherOfferDTOMapper.apply(teacherOffer);
    }

    private List<TeacherOfferDTO> mapTeacherOffersToDTO(@NotNull List<TeacherOffer> teacherOffers) {
        return teacherOffers.stream().map(teacherOfferDTOMapper).toList();
    }

    private List<StudentOfferDTO> mapStudentOffersToDTO(@NotNull List<StudentOffer> studentOffers) {
        return studentOffers.stream().map(studentOfferDTOMapper).toList();
    }

    private void updateTeacherOffer(@NotNull TeacherOffer teacherOffer, @NotNull TeacherOfferDTO teacherOfferDTO) {
        teacherOffer.setTitle(teacherOfferDTO.getTitle());
        teacherOffer.setSubTitle(teacherOfferDTO.getSubTitle());
        teacherOffer.setDescription(teacherOfferDTO.getDescription());
        teacherOffer.setPrice(teacherOfferDTO.getPrice());
        teacherOffer.setMonthlyPeriod(teacherOfferDTO.getMonthlyPeriod());
        teacherOffer.setOfferDetails(teacherOfferDTO.getOfferDetails());
        teacherOffer.setClassNumber(teacherOfferDTO.getClassNumber());
    }

    private void updateStudentOffer(@NotNull StudentOffer studentOffer, @NotNull StudentOfferDTO studentOfferDTO) {
        studentOffer.setTitle(studentOfferDTO.getTitle());
        studentOffer.setSubTitle(studentOfferDTO.getSubTitle());
        studentOffer.setDescription(studentOfferDTO.getDescription());
        studentOffer.setPrice(studentOfferDTO.getPrice());
        studentOffer.setMonthlyPeriod(studentOfferDTO.getMonthlyPeriod());
        studentOffer.setGroup(groupService.fetchGroupById(studentOfferDTO.getClassId()));
        studentOffer.setOfferDetails(studentOfferDTO.getOfferDetails());
    }

    private void validatedTeacherOffer(@NotNull final TeacherOfferDTO teacherOfferDTO) {
        if (teacherOfferDTO.getMonthlyPeriod() <= 0) {
            throw new IllegalArgumentException("La période mensuelle doit être positive");
        }
        if (teacherOfferDTO.getClassNumber() <= 0) {
            throw new IllegalArgumentException("Class Number must be positive");
        }
    }

    private void validatedStudentOffer(@NotNull final StudentOfferDTO studentOfferDTO) {
        if (studentOfferDTO.getMonthlyPeriod() <= 0) {
            throw new IllegalArgumentException("La période mensuelle doit être positive");
        }
    }

    private Student validateUserIsStudent(UserEntity user) {
        if (!(user instanceof Student student) || !user.getRole().getName().equalsIgnoreCase(AuthenticationRoles.ROLE_STUDENT)) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un étudiant");
        }
        return student;
    }

    private SuperTeacher validateUserIsTeacher(UserEntity user) {
        if (!(user instanceof SuperTeacher superTeacher) || !user.getRole().getName().equalsIgnoreCase(AuthenticationRoles.ROLE_SUPER_TEACHER)) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un enseignant");
        }
        return superTeacher;
    }

    private void validateStatus(@NotNull String status) {
        if (!status.equalsIgnoreCase("ACCEPTED") && !status.equalsIgnoreCase("REJECTED")) {
            throw new IllegalArgumentException("Le statut doit être soit 'ACCEPTED'soit 'REJECTED'");
        }
    }

    private boolean isValidImageFile(String contentType) {
        // Valid image MIME types
        List<String> allowedImageTypes = Arrays.asList(
                "image/jpeg",              // JPEG image
                "image/png",               // PNG image
                "image/gif",               // GIF image
                "image/bmp",               // BMP image
                "image/webp"               // WebP image
        );
        return allowedImageTypes.contains(contentType);
    }
}
