package mindpath.core.service.subject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.auth.teacher.Teacher;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.group.Group;
import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.subject.Subject;
import mindpath.core.domain.subject.SubjectDTO;
import mindpath.core.domain.subject.section.Section;
import mindpath.core.exceptions.custom.ResourceNotFoundException;
import mindpath.core.mapper.SubjectDTOMapper;
import mindpath.core.repository.GroupRepository;
import mindpath.core.repository.subject.SectionRepository;
import mindpath.core.repository.subject.SubjectRepository;
import mindpath.core.service.firstbasestorage.AzureStorageService;
import mindpath.core.service.playlist.PlayListService;
import mindpath.core.service.user.UserEntityService;
import mindpath.core.utility.annotation.IsAllowedToEnterSubject;
import mindpath.core.utility.annotation.IsSuperTeacherOrAdmin;
import mindpath.core.utility.annotation.IsUserAllowedToEnterGroup;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final UserEntityService userEntityService;
    private final AzureStorageService azureStorageService;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectDTOMapper subjectDTOMapper;
    private final ObjectMapper objectMapper;
    private final PlayListService playListService;
    private final GroupRepository groupRepository;

    public SubjectServiceImpl(UserEntityService userEntityService, AzureStorageService azureStorageService, SectionRepository sectionRepository, SubjectRepository subjectRepository, SubjectDTOMapper subjectDTOMapper, ObjectMapper objectMapper, PlayListService playListService, GroupRepository groupRepository) {
        this.userEntityService = userEntityService;
        this.azureStorageService = azureStorageService;
        this.sectionRepository = sectionRepository;
        this.subjectRepository = subjectRepository;
        this.subjectDTOMapper = subjectDTOMapper;
        this.objectMapper = objectMapper;
        this.playListService = playListService;
        this.groupRepository = groupRepository;
    }


    @Override
    @IsSuperTeacherOrAdmin
    @Transactional
    public SubjectDTO createSubject(
            @NotNull UserDetails userDetails,
            @NotNull Long groupId,
            @NotNull String subjectDTOJson,
            @NotNull MultipartFile mainImage,
            @NotNull MultipartFile backgroundImage
    ) throws IOException {
        if(!isValidImageFile(mainImage.getContentType()) || !isValidImageFile(backgroundImage.getContentType())){
            throw new IllegalArgumentException("Les fichiers d'image doivent être de type image.");
        }

        UserEntity user = userEntityService.fetchUserByEmail(userDetails.getUsername());
        final SuperTeacher superTeacher = userEntityService.validateSuperTeacher(user);
        final SubjectDTO subjectDTO = parseSubjectDTO(subjectDTOJson);

        final UserEntity teacher = userEntityService.fetchUserByUUID(subjectDTO.teacherId());
        final Group group = fetchGroupById(groupId);

        if(superTeacher.getId() != teacher.getId()){
            Teacher teacherToAssign = userEntityService.validateTeacher(teacher);
            if(!superTeacher.getTeachers().contains(teacherToAssign)){
                throw new IllegalArgumentException("L'enseignant n'est pas membre des enseignants du Super Enseignant.");
            }
            user = teacher;
        }



        final String mainImageUrl = uploadImage(mainImage, "subject");
        final String backgroundImageUrl = uploadImage(backgroundImage, "subject");


        final Subject subject = buildSubject(subjectDTO, user, group, mainImageUrl, backgroundImageUrl);
        subjectRepository.saveAndFlush(subject);

        final List<Section> sections = createSections(subjectDTO, subject);
        sectionRepository.saveAllAndFlush(sections);
        subject.setSections(sections);

        return mapSubjectToSubjectDTO(subject);
    }

    @Override
    @IsAllowedToEnterSubject
    public SubjectDTO updateSubject(
            @NotNull UserDetails userDetails,
            @NotNull Long subjectId,
            @NotNull String subjectDTOJson,
             MultipartFile mainImage,
             MultipartFile backgroundImage
    ) throws IOException {
        UserEntity user = userEntityService.fetchUserByEmail(userDetails.getUsername());
        final SuperTeacher superTeacher = userEntityService.validateSuperTeacher(user);
        final SubjectDTO subjectDTO = parseSubjectDTO(subjectDTOJson);
        final UserEntity teacher = userEntityService.fetchUserByUUID(subjectDTO.teacherId());

        final Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Sujet non trouvé"));


        if(!superTeacher.getId().equals(teacher.getId())){
            Teacher teacherToAssign = userEntityService.validateTeacher(teacher);
            if(!superTeacher.getTeachers().contains(teacherToAssign)){
                throw new IllegalArgumentException("L'enseignant n'est pas membre des enseignants du Super Enseignant.");
            }
            user = teacher;
        }


        if(mainImage != null) {
            if(!isValidImageFile(mainImage.getContentType())){
                throw new IllegalArgumentException("Le fichier d'image doit être de type image.");
            }
           azureStorageService.deleteFile(subject.getMainImageUrl());
           subject.setMainImageUrl(azureStorageService.uploadFile(mainImage, "subject"));
        }
        if(backgroundImage != null) {
            if(!isValidImageFile(backgroundImage.getContentType())) {
                throw new IllegalArgumentException("Le fichier d'image doit être de type image.");
            }
           azureStorageService.deleteFile(subject.getBackgroundImageUrl());
           subject.setBackgroundImageUrl(azureStorageService.uploadFile(backgroundImage, "subject"));
        }

        subject.setSpeciality(subjectDTO.speciality());
        subject.setLevel(subjectDTO.level());
        subject.setTeacher(user);



        final List<Section> newSections = createSections(subjectDTO, subject);
        subject.setSections(newSections);
        subjectRepository.saveAndFlush(subject);
        return mapSubjectToSubjectDTO(subject);
    }

    @Override
    @IsAllowedToEnterSubject
    public SubjectDTO getSubjectById(UserDetails userDetails,Long subjectId) {
        return mapSubjectToSubjectDTO(fetchSubjectById(subjectId));
    }

    @Override
    @IsUserAllowedToEnterGroup
    public List<SubjectDTO> getSubjectsByGroupId(UserDetails userDetails, Long groupId) {
        final Group group = fetchGroupById(groupId);
        return  group.getSubjects().stream().sorted(
                (subject1,subject2) -> subject1.getId() > subject2.getId() ? 1 : -1
        ).distinct().map(this::mapSubjectToSubjectDTO).toList();
    }

    @Override
    public List<SubjectDTO> allSubjects(@NotNull UserDetails userDetails) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());

        if(currentUser instanceof SuperTeacher superTeacher){
            List<Group> groups = superTeacher.getGroups();
            return groups.stream()
                    .flatMap(group -> group.getSubjects().stream())
                    .map(subjectDTOMapper)
                    .distinct()
                    .toList();
        }
        if(currentUser instanceof Teacher teacher){
            return teacher.getSubjects().stream()
                    .map(subjectDTOMapper)
                    .distinct()
                    .toList();
        }
        return List.of();
    }

    @Override
    public List<SubjectDTO> findAllSubjects() {
        return subjectRepository.findAll().stream().map(subjectDTOMapper).toList();
    }

    @Override
    public Subject fetchSubjectById(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Sujet non trouvé"));
    }

    @Override
    public void deleteSubject(UserDetails userDetails, Long subjectId) {
        final UserEntity user = userEntityService.fetchUserByEmail(userDetails.getUsername());
        final Subject subject = fetchSubjectById(subjectId);

        if(!user.getId().equals(subject.getGroup().getSuperTeacher().getId())){
            throw new IllegalArgumentException("L'utilisateur n'est pas autorisé à supprimer ce sujet.");
        }

        deleteSubjectAsync(subject);
        subjectRepository.deleteSubjectById(subjectId);
    }

    @Override
    @Async("uploadTaskExecutor")
    public void deleteSubjectAsync(Subject subject) {
        azureStorageService.deleteFile(subject.getMainImageUrl());
        azureStorageService.deleteFile(subject.getBackgroundImageUrl());
        for(PlayList playList : subject.getPlayLists()){
            playListService.deletePlayList(playList);
        }
    }
    public void deleteSubject(Subject subject) {
        azureStorageService.deleteFile(subject.getMainImageUrl());
        azureStorageService.deleteFile(subject.getBackgroundImageUrl());
        for(PlayList playList : subject.getPlayLists()){
            playListService.deletePlayList(playList);
        }
    }


    private SubjectDTO parseSubjectDTO(String subjectDTOJson){
        try {
            return objectMapper.readValue(subjectDTOJson, SubjectDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Format JSON invalide pour SubjectDTO.", e);
        }
    }

    private String uploadImage(MultipartFile image, String folder) throws IOException {
        try {
            return azureStorageService.uploadFile(image, folder);
        } catch (IOException e) {
            throw new IOException("Échec du téléchargement de l'image : " + image.getOriginalFilename(), e);
        }
    }

    private Subject buildSubject(SubjectDTO subjectDTO, UserEntity user, Group group, String mainImageUrl, String backgroundImageUrl) {
        return Subject.builder()
                .speciality(subjectDTO.speciality())
                .level(subjectDTO.level())
                .mainImageUrl(mainImageUrl)
                .backgroundImageUrl(backgroundImageUrl)
                .teacher(user)
                .group(group)
                .build();
    }

    private List<Section> createSections(SubjectDTO subjectDTO, Subject subject) {
        if(subjectDTO.sections() == null) {
            return List.of();
        }
        return subjectDTO.sections().stream()
                .map(sectionDTO -> Section.builder()
                        .sectionName(sectionDTO.sectionName())
                        .sectionColor(sectionDTO.sectionColor())
                        .subject(subject)
                        .build())
                .toList();
    }

    private SubjectDTO mapSubjectToSubjectDTO(Subject subject) {
        return subjectDTOMapper.apply(subject);
    }

    private Group fetchGroupById(long classId) {
        return groupRepository.fetchGroupById(classId).orElseThrow(
                () -> new ResourceNotFoundException("Le groupe avec l'ID : %d non trouvé".formatted(classId))
        );
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
