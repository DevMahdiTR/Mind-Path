package mindpath.core.mapper;

import mindpath.core.domain.subject.section.Section;
import mindpath.core.domain.subject.section.SectionDTO;

import java.util.function.Function;

public class SectionDTOMapper implements Function<Section, SectionDTO> {
    @Override
    public SectionDTO apply(Section section) {
        return new SectionDTO(
                section.getId(),
                section.getSectionName(),
                section.getSectionColor()
        );
    }
}
