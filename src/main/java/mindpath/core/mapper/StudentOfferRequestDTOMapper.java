package mindpath.core.mapper;

import mindpath.core.domain.offer.request.StudentOfferRequest;
import mindpath.core.domain.offer.request.StudentOfferRequestDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class StudentOfferRequestDTOMapper implements Function<StudentOfferRequest, StudentOfferRequestDTO> {
    @Override
    public StudentOfferRequestDTO apply(StudentOfferRequest studentOfferRequest) {
        return new StudentOfferRequestDTO(
                studentOfferRequest.getId(),
                studentOfferRequest.getPaymentImageUrl(),
                studentOfferRequest.getStatus(),
                studentOfferRequest.getRequestDate(),
                studentOfferRequest.getStartDate(),
                studentOfferRequest.getEndDate(),
                studentOfferRequest.getStudent() == null ? null : studentOfferRequest.getStudent().getId(),
                studentOfferRequest.getStudent() == null ? null : studentOfferRequest.getStudent().getFullName(),
                studentOfferRequest.getStudentOffer() == null ? null :new StudentOfferDTOMapper().apply(studentOfferRequest.getStudentOffer())
        );
    }
}
