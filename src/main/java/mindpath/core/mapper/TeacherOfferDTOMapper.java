package mindpath.core.mapper;

import mindpath.core.domain.offer.TeacherOffer;
import mindpath.core.domain.offer.TeacherOfferDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TeacherOfferDTOMapper implements Function<TeacherOffer, TeacherOfferDTO> {
    @Override
    public TeacherOfferDTO apply(TeacherOffer teacherOffer) {
        return new TeacherOfferDTO(
                teacherOffer.getId(),
                teacherOffer.getTitle(),
                teacherOffer.getSubTitle(),
                teacherOffer.getDescription(),
                teacherOffer.getPrice(),
                teacherOffer.getMonthlyPeriod(),
                teacherOffer.getClassNumber(),
                teacherOffer.getImageUrl(),
                teacherOffer.getOfferDetails(),
                teacherOffer.getCreatedAt(),
                false
        );
    }
}
