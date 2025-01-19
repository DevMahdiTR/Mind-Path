package mindpath.core.service.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import mindpath.config.AuthenticationRoles;
import mindpath.core.domain.auth.student.Student;
import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.auth.teacher.Teacher;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.event.Event;
import mindpath.core.domain.event.EventDTO;
import mindpath.core.domain.group.Group;
import mindpath.core.domain.group.GroupDTO;
import mindpath.core.domain.group.GroupWithStatusDTO;
import mindpath.core.domain.offer.StudentOffer;
import mindpath.core.domain.subject.Subject;
import mindpath.core.exceptions.custom.ResourceNotFoundException;
import mindpath.core.mapper.GroupDTOMapper;
import mindpath.core.repository.EventRepository;
import mindpath.core.repository.GroupRepository;
import mindpath.core.service.event.EventService;
import mindpath.core.service.firstbasestorage.AzureStorageService;
import mindpath.core.service.offer.OfferSecurityService;
import mindpath.core.service.subject.SubjectService;
import mindpath.core.service.user.UserEntityService;
import mindpath.core.utility.annotation.IsSuperTeacherOrAdmin;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {

    private final UserEntityService userEntityService;
    private final OfferSecurityService offerSecurityService;
    private final EventService eventService;
    private final AzureStorageService azureStorageService;

    private final GroupRepository groupRepository;
    private final GroupDTOMapper groupDTOMapper;
    private final ObjectMapper objectMapper;
    private final SubjectService subjectService;
    private final EventRepository eventRepository;

    public GroupServiceImpl(UserEntityService userEntityService, OfferSecurityService offerSecurityService, GroupRepository groupRepository, GroupDTOMapper groupDTOMapper, AzureStorageService azureStorageService, ObjectMapper objectMapper, EventService eventService, SubjectService subjectService, EventRepository eventRepository) {
        this.userEntityService = userEntityService;
        this.offerSecurityService = offerSecurityService;
        this.groupRepository = groupRepository;
        this.groupDTOMapper = groupDTOMapper;
        this.azureStorageService = azureStorageService;
        this.objectMapper = objectMapper;
        this.eventService = eventService;
        this.subjectService = subjectService;
        this.eventRepository = eventRepository;
    }

    @Override
    public GroupDTO createGroup(
            @NotNull UserDetails userDetails,
            @NotNull final MultipartFile backgroundImage,
            @NotNull final MultipartFile mainImage,
            @NotNull String groupDetailsJson) throws IOException {

        if (!isValidImageFile(backgroundImage.getContentType())) {
            throw new IllegalArgumentException("Le fichier doit être une image.");
        }
        if (!isValidImageFile(mainImage.getContentType())) {
            throw new IllegalArgumentException("Le fichier doit être une image.");
        }
        UserEntity user = userEntityService.fetchUserByEmail(userDetails.getUsername());
        SuperTeacher superTeacher = validateSuperTeacher(user);
        offerSecurityService.isTeacherOfferStillActive(superTeacher);
        validateGroupCreation(superTeacher);

        String backgroundImageUrl = azureStorageService.uploadFile(backgroundImage, "groups");
        String mainImageUrl = azureStorageService.uploadFile(mainImage, "groups");

        GroupDTO groupDetails = objectMapper.readValue(groupDetailsJson, GroupDTO.class);
        Group newGroup = buildGroup(groupDetails, superTeacher, backgroundImageUrl, mainImageUrl);

        groupRepository.saveAndFlush(newGroup);

        return groupDTOMapper.apply(newGroup);
    }


    @Override
    @IsSuperTeacherOrAdmin
    public GroupDTO updateGroup(
            @NotNull final UserDetails userDetails,
            @NotNull final Long groupId,
            final MultipartFile backgroundImage,
            final MultipartFile mainImage,
            @NotNull String groupDetailsJson) throws IOException {

        UserEntity user = userEntityService.fetchUserByEmail(userDetails.getUsername());
        SuperTeacher superTeacher = validateSuperTeacher(user);
        offerSecurityService.isTeacherOfferStillActive(superTeacher);
        Group existingGroup = fetchGroupById(groupId);

        GroupDTO groupDetails = objectMapper.readValue(groupDetailsJson, GroupDTO.class);

        if (groupDetails.title() != null) {
            existingGroup.setTitle(groupDetails.title());
        }
        if (groupDetails.educationLevel() != null) {
            existingGroup.setEducationLevel(groupDetails.educationLevel());
        }

        if (backgroundImage != null && !backgroundImage.isEmpty()) {
            if (!isValidImageFile(backgroundImage.getContentType())) {
                throw new IllegalArgumentException("Le fichier doit être une image.");
            }
            azureStorageService.deleteFile(existingGroup.getBackgroundImageUrl());
            String backgroundImageUrl = azureStorageService.uploadFile(backgroundImage, "groups");
            existingGroup.setBackgroundImageUrl(backgroundImageUrl);
        }
        if (mainImage != null && !mainImage.isEmpty()) {
            if (!isValidImageFile(mainImage.getContentType())) {
                throw new IllegalArgumentException("Le fichier doit être une image.");
            }
            azureStorageService.deleteFile(existingGroup.getMainImageUrl());
            String mainImageUrl = azureStorageService.uploadFile(mainImage, "groups");
            existingGroup.setMainImageUrl(mainImageUrl);
        }

        existingGroup.setUpdatedAt(LocalDateTime.now());

        groupRepository.saveAndFlush(existingGroup);

        return groupDTOMapper.apply(existingGroup);
    }

    @Override
    @IsSuperTeacherOrAdmin
    public String addStudentToGroup(@NotNull UserDetails userDetails, @NotNull Long groupId, @NotNull UUID studentId) {
        UserEntity user = userEntityService.fetchUserByEmail(userDetails.getUsername());
        SuperTeacher superTeacher = validateSuperTeacher(user);
        offerSecurityService.isTeacherOfferStillActive(superTeacher);
        Group group = fetchGroupById(groupId);
        Student student = validateStudent(userEntityService.fetchUserByUUID(studentId));

        if (group.getStudents().stream().anyMatch(s -> s.getId().equals(student.getId()))) {
            throw new IllegalArgumentException("L'étudiant est déjà dans le groupe");
        }

        group.getStudents().add(student);
        groupRepository.saveAndFlush(group);

        return "Étudiant ajouté au groupe avec succès";
    }

    @Override
    @IsSuperTeacherOrAdmin
    public String removeStudentFromGroup(@NotNull UserDetails userDetails, @NotNull Long groupId, @NotNull UUID studentId) {
        UserEntity user = userEntityService.fetchUserByEmail(userDetails.getUsername());
        SuperTeacher superTeacher = validateSuperTeacher(user);
        offerSecurityService.isTeacherOfferStillActive(superTeacher);
        Group group = fetchGroupById(groupId);
        Student student = validateStudent(userEntityService.fetchUserByUUID(studentId));

        if (group.getStudents().stream().noneMatch(s -> s.getId().equals(student.getId()))) {
            throw new IllegalArgumentException("L'étudiant n'est pas dans le groupe");
        }

        group.getStudents().remove(student);
        groupRepository.saveAndFlush(group);

        return "Étudiant retiré du groupe avec succès";
    }

    @Override
    public Group fetchGroupById(long classId) {
        return groupRepository.fetchGroupById(classId).orElseThrow(
                () -> new ResourceNotFoundException("Groupe avec l'ID : %d non trouvé".formatted(classId))
        );
    }

    @Override
    public GroupDTO getGroupById(UserDetails userDetails, long groupId) {
        List<GroupWithStatusDTO> groups = getAllUserGroups(userDetails);
        GroupWithStatusDTO group = groups.stream()
                .filter(g -> g.getId() == groupId && g.isActive())
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Accès refusé"));
        return groupDTOMapper.apply(fetchGroupById(groupId));
    }

    @Override
    public String makeGroupPublic(UserDetails userDetails, long groupId, boolean isPublic) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        final SuperTeacher currentSuperTeacher = userEntityService.validateSuperTeacher(currentUser);

        Group groupToMakePublic = validateGroupBelongToSuperTeacher(currentSuperTeacher, groupId);
        groupToMakePublic.setPublic(isPublic);
        groupRepository.save(groupToMakePublic);

        return "Le groupe est maintenant %s".formatted(isPublic ? "public" : "privé");
    }

    @Override
    public List<GroupDTO> getAllPublicGroups() {
        return groupRepository.fetchAllByIsPublicTrue().stream().map(groupDTOMapper).toList();
    }


    @Override
    public List<GroupWithStatusDTO> getAllUserGroups(UserDetails userDetails) {
        UserEntity user = userEntityService.fetchUserByEmail(userDetails.getUsername());
        if (user instanceof SuperTeacher superTeacher) {
            return getActiveGroupsForTeacher(superTeacher);
        }
        if (user instanceof Student student) {
            return getGroupsForStudentWithStatus(student);
        }
        if (user instanceof Teacher teacher) {
            List<GroupWithStatusDTO> groupsSet = new ArrayList<>();
            for (SuperTeacher superTeacher1 : teacher.getSuperTeachers()) {
                for (Group g : superTeacher1.getGroups()) {
                    for (Subject s : g.getSubjects()) {
                        if (teacher.getId().equals(s.getTeacher().getId())) {
                            GroupWithStatusDTO groupWithStatusDTO = GroupWithStatusDTO.builder()
                                    .id(g.getId())
                                    .title(g.getTitle())
                                    .backgroundImageUrl(g.getBackgroundImageUrl())
                                    .mainImageUrl(g.getMainImageUrl())
                                    .isActive(true)
                                    .updatedAt(g.getUpdatedAt())
                                    .createdAt(g.getCreatedAt())
                                    .educationLevel(g.getEducationLevel())
                                    .isPublic(g.isPublic())
                                    .superTeacherFullName(g.getSuperTeacher().getFullName())
                                    .build();

                            groupsSet.add(groupWithStatusDTO);
                        }
                    }
                }
            }
            return groupsSet.stream().distinct().toList();
        }
        throw new IllegalArgumentException("L'utilisateur n'est ni un étudiant ni un super enseignant");
    }

    @Override
    public Group save(Group group) {
        return groupRepository.save(group);
    }


    @Override
    public EventDTO addEventToGroup(@NotNull UserDetails userDetails, @NotNull Long groupId, @NotNull EventDTO eventDTO) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        final SuperTeacher currentSuperTeacher = userEntityService.validateSuperTeacher(currentUser);

        Group group = validateGroupBelongToSuperTeacher(currentSuperTeacher, groupId);
        eventService.validateEventTiming(group.getId(), eventDTO.startTime(), eventDTO.endTime(), null);
        Event newEvent = Event.builder()
                .title(eventDTO.title())
                .startTime(eventDTO.startTime())
                .endTime(eventDTO.endTime())
                .group(group)
                .backgroundColor(eventDTO.backgroundColor())
                .build();
        eventService.saveAndFlush(newEvent);

        return eventService.mapToDTO(newEvent);
    }


    @Override
    public EventDTO updateEvent(@NotNull UserDetails userDetails, @NotNull Long groupId, @NotNull Long eventId, @NotNull EventDTO eventDTO) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        final SuperTeacher currentSuperTeacher = userEntityService.validateSuperTeacher(currentUser);
        Group group = validateGroupBelongToSuperTeacher(currentSuperTeacher, groupId);
        Event eventToUpdate = group.getEvents().stream()
                .filter(event -> event.getId().equals(eventId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));
        eventService.validateEventTiming(groupId, eventDTO.startTime(), eventDTO.endTime(), eventId);
        eventToUpdate.setTitle(eventDTO.title());
        eventToUpdate.setStartTime(eventDTO.startTime());
        eventToUpdate.setEndTime(eventDTO.endTime());
        eventToUpdate.setBackgroundColor(eventDTO.backgroundColor());
        eventService.saveAndFlush(eventToUpdate);

        return eventService.mapToDTO(eventToUpdate);
    }

    @Override
    @Transactional
    public String deleteEvent(@NotNull UserDetails userDetails, @NotNull Long groupId, @NotNull Long eventId) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        final SuperTeacher currentSuperTeacher = userEntityService.validateSuperTeacher(currentUser);

        Group group = validateGroupBelongToSuperTeacher(currentSuperTeacher, groupId);

        Event eventToDelete = group.getEvents().stream()
                .filter(event -> event.getId().equals(eventId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));

        group.getEvents().remove(eventToDelete);
        log.info("Deleting event with ID: {}", eventId);
        log.info("Group events: {}", eventToDelete);
        eventRepository.deleteEventById(eventId);
        return "Event deleted successfully";
    }

    @Override
    public List<EventDTO> getGroupEvents(@NotNull UserDetails userDetails, @NotNull Long groupId) {
        List<GroupWithStatusDTO> groups = getAllUserGroups(userDetails);
        GroupWithStatusDTO group = groups.stream()
                .filter(g -> g.getId() == groupId)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Accès refusé ou groupe non trouvé"));
        List<Event> events = fetchGroupById(groupId).getEvents().stream().distinct().toList();

        return eventService.mapToDTO(events);
    }

    @Override
    public void deleteGroup(UserDetails userDetails, Long groupId) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        final SuperTeacher currentSuperTeacher = userEntityService.validateSuperTeacher(currentUser);
        Group group = validateGroupBelongToSuperTeacher(currentSuperTeacher, groupId);

        deleteGroupAsync(group, currentSuperTeacher);

        groupRepository.deleteGroupById(groupId);
    }

    @Async("uploadTaskExecutor")
    @Override
    public void deleteGroupAsync(Group group, SuperTeacher currentSuperTeacher) {
        for (Subject subject : group.getSubjects()) {
            subjectService.deleteSubject(subject);
        }
        azureStorageService.deleteFile(group.getBackgroundImageUrl());
        azureStorageService.deleteFile(group.getMainImageUrl());
    }

    private SuperTeacher validateSuperTeacher(UserDetails userDetails) {
        if (!(userDetails instanceof SuperTeacher superTeacher)) {
            throw new IllegalArgumentException("Seuls les super enseignants peuvent effectuer cette opération");
        }
        return superTeacher;
    }

    private Student validateStudent(UserEntity user) {
        if (!(user instanceof Student student)) {
            throw new IllegalArgumentException("L'utilisateur que vous essayez d'ajouter doit être un étudiant");
        }
        return student;
    }

    private void validateGroupCreation(@NotNull SuperTeacher superTeacher) {
        if (superTeacher.getRole().getName().equals(AuthenticationRoles.ROLE_ADMIN)) {
            return;
        }

        if (superTeacher.getRole().getName().equals(AuthenticationRoles.ROLE_SUPER_TEACHER)) {
            int numberOfGroups = superTeacher.getGroups().size();
            boolean canCreateMoreGroups = superTeacher.getTeacherOfferRequests().stream()
                    .filter(request -> request.getEndDate().isAfter(LocalDateTime.now()) && request.getStatus().equals("ACCEPTED"))
                    .anyMatch(request -> request.getTeacherOffer().getClassNumber() > numberOfGroups);

            if (!canCreateMoreGroups) {
                throw new IllegalArgumentException("Vous avez atteint le nombre maximum de groupes que vous pouvez créer.");
            }
        }
    }

    private Group buildGroup(GroupDTO groupDetails, SuperTeacher superTeacher, String backgroundImageUrl, String mainImageUrl) {
        return Group.builder()
                .title(groupDetails.title())
                .educationLevel(groupDetails.educationLevel())
                .backgroundImageUrl(backgroundImageUrl)
                .mainImageUrl(mainImageUrl)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isPublic(false)
                .superTeacher(superTeacher)
                .build();
    }

    private Group fetchGroupById(Long groupId) {
        return groupRepository.fetchGroupById(groupId).orElseThrow(
                () -> new ResourceNotFoundException("Group avec l'ID : %d non trouvé".formatted(groupId))
        );
    }


    private List<GroupWithStatusDTO> getGroupsForStudentWithStatus(@NotNull Student student) {
        Set<Long> activeGroupIds = student.getStudentOfferRequests().stream()
                .filter(request -> {
                    LocalDateTime endDate = request.getEndDate();
                    return endDate != null && endDate.isAfter(LocalDateTime.now()) && request.getStatus().equals("ACCEPTED");
                })
                .map(request -> {
                    StudentOffer studentOffer = request.getStudentOffer();
                    if (studentOffer != null && studentOffer.getGroup() != null) {
                        return studentOffer.getGroup().getId();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return student.getGroups().stream()
                .map(group -> {
                    boolean isActive = activeGroupIds.contains(group.getId()) ||
                            group.getSuperTeacher().getTeacherOfferRequests().stream()
                                    .anyMatch(request -> {
                                        LocalDateTime endDate = request.getEndDate();
                                        return endDate != null && endDate.isAfter(LocalDateTime.now()) && request.getStatus().equals("ACCEPTED");
                                    });

                    return GroupWithStatusDTO.builder()
                            .id(group.getId())
                            .title(group.getTitle())
                            .backgroundImageUrl(group.getBackgroundImageUrl())
                            .mainImageUrl(group.getMainImageUrl())
                            .isActive(isActive)
                            .updatedAt(group.getUpdatedAt())
                            .createdAt(group.getCreatedAt())
                            .educationLevel(group.getEducationLevel())
                            .isPublic(group.isPublic())
                            .superTeacherFullName(group.getSuperTeacher().getFullName())
                            .build();
                })
                .collect(Collectors.toCollection(HashSet::new)) // Collect to Set to ensure uniqueness
                .stream()
                .collect(Collectors.toList()); // Convert back to List
    }


    private List<GroupWithStatusDTO> getActiveGroupsForTeacher(@NotNull SuperTeacher superTeacher) {
        if (superTeacher.getRole().getName().equals(AuthenticationRoles.ROLE_ADMIN)) {
            return superTeacher.getGroups()
                    .stream()
                    .map(group -> GroupWithStatusDTO.builder()
                            .id(group.getId())
                            .title(group.getTitle())
                            .backgroundImageUrl(group.getBackgroundImageUrl())
                            .mainImageUrl(group.getMainImageUrl())
                            .isActive(true)
                            .updatedAt(group.getUpdatedAt())
                            .createdAt(group.getCreatedAt())
                            .educationLevel(group.getEducationLevel())
                            .superTeacherFullName(group.getSuperTeacher().getFullName())
                            .isPublic(group.isPublic())
                            .build())
                    .distinct()
                    .toList();
        }

        if (superTeacher.getGroups() == null || superTeacher.getGroups().isEmpty()) {
            return new ArrayList<>();
        }

        if (superTeacher.getTeacherOfferRequests().stream()
                .noneMatch(request -> request.getEndDate().isAfter(LocalDateTime.now()) && request.getStatus().equals("ACCEPTED"))) {
            return superTeacher.getGroups()
                    .stream()
                    .map(group -> GroupWithStatusDTO.builder()
                            .id(group.getId())
                            .title(group.getTitle())
                            .backgroundImageUrl(group.getBackgroundImageUrl())
                            .mainImageUrl(group.getMainImageUrl())
                            .isActive(false)
                            .updatedAt(group.getUpdatedAt())
                            .createdAt(group.getCreatedAt())
                            .educationLevel(group.getEducationLevel())
                            .superTeacherFullName(group.getSuperTeacher().getFullName())
                            .isPublic(group.isPublic())
                            .build())
                    .distinct()
                    .toList();
        } else {
            return superTeacher.getGroups()
                    .stream()
                    .map(group -> GroupWithStatusDTO.builder()
                            .id(group.getId())
                            .title(group.getTitle())
                            .backgroundImageUrl(group.getBackgroundImageUrl())
                            .mainImageUrl(group.getMainImageUrl())
                            .isActive(true)
                            .updatedAt(group.getUpdatedAt())
                            .createdAt(group.getCreatedAt())
                            .educationLevel(group.getEducationLevel())
                            .superTeacherFullName(group.getSuperTeacher().getFullName())
                            .isPublic(group.isPublic())
                            .build())
                    .distinct()
                    .toList();
        }
    }

    private Group validateGroupBelongToSuperTeacher(SuperTeacher currentSuperTeacher, Long groupId) {
        return currentSuperTeacher.getGroups().stream().filter(g -> g.getId() == groupId).findFirst().orElseThrow(() -> new ResourceNotFoundException("Accès refusé"));
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
