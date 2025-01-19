package mindpath.core.rest;

import mindpath.config.APIRouters;
import mindpath.core.domain.event.EventDTO;
import mindpath.core.domain.group.GroupDTO;
import mindpath.core.domain.group.GroupWithStatusDTO;
import mindpath.core.service.group.GroupService;
import mindpath.core.utility.CustomerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(APIRouters.GROUP_ROUTER)
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }


    @PostMapping()
    public CustomerResponse<GroupDTO> createGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "backgroundImage") MultipartFile backgroundImage,
            @RequestParam(name = "mainImage") MultipartFile mainImage,
            @RequestParam(name = "groupeDetailsJson") String groupDetailsJson) throws IOException {
        return new CustomerResponse<>(groupService.createGroup(userDetails, backgroundImage, mainImage, groupDetailsJson), HttpStatus.CREATED);
    }

    @PutMapping("/{groupId}")
    public CustomerResponse<GroupDTO> updateGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestParam(name = "backgroundImage", required = false) MultipartFile backgroundImage,
            @RequestParam(name = "mainImage", required = false) MultipartFile mainImage,
            @RequestParam(name = "groupDetailsJson") String groupDetailsJson) throws IOException {
        return new CustomerResponse<>(groupService.updateGroup(userDetails, groupId, backgroundImage, mainImage, groupDetailsJson), HttpStatus.OK);
    }

    @PutMapping("/{groupId}/add-student/{studentId}")
    public CustomerResponse<String> addStudentToGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("studentId") UUID studentId) {
        return new CustomerResponse<>(groupService.addStudentToGroup(userDetails, groupId, studentId), HttpStatus.OK);
    }
    @PutMapping("/{groupId}/remove-student/{studentId}")
    public CustomerResponse<String> removeStudentToGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("studentId") UUID studentId) {
        return new CustomerResponse<>(groupService.removeStudentFromGroup(userDetails, groupId, studentId), HttpStatus.OK);
    }

    @PutMapping("/{groupId}/make-public")
    public CustomerResponse<String> makeGroupPublic(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestParam(name = "isPublic") boolean isPublic) {
        return new CustomerResponse<>(groupService.makeGroupPublic(userDetails, groupId, isPublic), HttpStatus.OK);
    }

    @GetMapping("/public")
    public CustomerResponse<List<GroupDTO>> getAllPublicGroups() {
        return new CustomerResponse<>(groupService.getAllPublicGroups(), HttpStatus.OK);
    }

    @GetMapping()
    public CustomerResponse<List<GroupWithStatusDTO>> getAllUserGroups(@AuthenticationPrincipal UserDetails userDetails) {
        return new CustomerResponse<>(groupService.getAllUserGroups(userDetails), HttpStatus.OK);
    }

    @GetMapping("/{groupId}")
    public CustomerResponse<GroupDTO> getGroupById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("groupId") Long groupId) {
        return new CustomerResponse<>(groupService.getGroupById(userDetails, groupId), HttpStatus.OK);
    }

    @GetMapping("/{groupId}/events")
    public CustomerResponse<List<EventDTO>> getGroupEvents(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("groupId") Long groupId) {
        return new CustomerResponse<>(groupService.getGroupEvents(userDetails, groupId), HttpStatus.OK);
    }

    @PostMapping("/{groupId}/events")
    public CustomerResponse<EventDTO> addEventToGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestBody EventDTO eventDTO) {
        return new CustomerResponse<>(groupService.addEventToGroup(userDetails, groupId, eventDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{groupId}/events/{eventId}")
    public CustomerResponse<EventDTO> updateEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("eventId") Long eventId,
            @RequestBody EventDTO eventDTO) {
        return new CustomerResponse<>(groupService.updateEvent(userDetails, groupId, eventId, eventDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{groupId}/events/{eventId}")
    public CustomerResponse<String> deleteEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("eventId") Long eventId) {
        return new CustomerResponse<>(groupService.deleteEvent(userDetails, groupId, eventId), HttpStatus.OK);
    }

    @DeleteMapping("/{groupId}")
    public CustomerResponse<String> deleteGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("groupId") Long groupId) {
        groupService.deleteGroup(userDetails, groupId);
        return new CustomerResponse<>("Group deleted", HttpStatus.OK);
    }
}
