package mindpath.core.service.group;

import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.event.EventDTO;
import mindpath.core.domain.group.Group;
import mindpath.core.domain.group.GroupDTO;
import mindpath.core.domain.group.GroupWithStatusDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface GroupService {

    GroupDTO createGroup(
            @NotNull UserDetails userDetails,
            @NotNull final MultipartFile backgroundImage,
            @NotNull final MultipartFile mainImage,
            @NotNull String groupDetailsJson) throws IOException;

    GroupDTO updateGroup(
            @NotNull final UserDetails userDetails,
            @NotNull final Long groupId,
            final MultipartFile backgroundImage,
            final MultipartFile mainImage,
            @NotNull String groupDetailsJson) throws IOException;

    String addStudentToGroup(
            @NotNull final UserDetails userDetails,
            @NotNull final Long groupId,
            @NotNull final UUID studentId
    );

    String removeStudentFromGroup(
            @NotNull UserDetails userDetails,
            @NotNull Long groupId,
            @NotNull UUID studentId);

    Group fetchGroupById(long classId);

    GroupDTO getGroupById(UserDetails userDetails,long groupId);

    String makeGroupPublic(UserDetails userDetails, long groupId , boolean isPublic);

    List<GroupWithStatusDTO> getAllUserGroups(UserDetails userDetails);
    Group save (Group group);
    List<GroupDTO> getAllPublicGroups();


    EventDTO addEventToGroup(
            @NotNull UserDetails userDetails,
            @NotNull Long groupId,
            @NotNull EventDTO eventDTO
    );

    EventDTO updateEvent(
            @NotNull UserDetails userDetails,
            @NotNull Long groupId,
            @NotNull Long eventId,
            @NotNull EventDTO eventDTO
    );

    String deleteEvent(
            @NotNull UserDetails userDetails,
            @NotNull Long groupId,
            @NotNull Long eventId
    );

    List<EventDTO> getGroupEvents(
            @NotNull UserDetails userDetails,
            @NotNull Long groupId
    );

    void deleteGroup(UserDetails userDetails, Long groupId);
    void deleteGroupAsync(Group group, SuperTeacher currentSuperTeacher);

}
