package mindpath.core.mapper;

import mindpath.core.domain.offer.StudentOffer;
import mindpath.core.domain.offer.StudentOfferDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class StudentOfferDTOMapper implements Function<StudentOffer, StudentOfferDTO> {
    @Override
    public StudentOfferDTO apply(StudentOffer studentOffer) {
        return new StudentOfferDTO(
                studentOffer.getId(),
                studentOffer.getTitle(),
                studentOffer.getSubTitle(),
                studentOffer.getDescription(),
                studentOffer.getPrice(),
                studentOffer.getMonthlyPeriod(),
                studentOffer.getImageUrl(),
                studentOffer.getOfferDetails(),
                studentOffer.getCreatedAt(),
                studentOffer.getGroup() == null ? null : studentOffer.getGroup().getId(),
                false
        );
    }
}
