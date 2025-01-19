package mindpath.core.mapper;

import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.group.Group;
import mindpath.core.domain.group.GroupDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class GroupDTOMapper implements Function<Group, GroupDTO> {
    @Override
    public GroupDTO apply(Group group) {
        return new GroupDTO(
                group.getId(),
                group.getTitle(),
                group.getBackgroundImageUrl(),
                group.getMainImageUrl(),
                group.getEducationLevel(),
                group.isPublic(),
                group.getCreatedAt(),
                group.getUpdatedAt(),
                group.getStudents() == null ? null : group.getStudents().stream().map(UserEntity::getId).toList(),
                group.getSuperTeacher().getId(),
                group.getSuperTeacher().getFullName(),
                group.getSubjects() == null ? null : group.getSubjects().stream().map(new SubjectDTOMapper()).toList()
        );
    }
}
