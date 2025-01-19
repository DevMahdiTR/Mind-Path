package mindpath.core.mapper;

import mindpath.core.domain.offer.request.TeacherOfferRequest;
import mindpath.core.domain.offer.request.TeacherOfferRequestDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TeacherOfferRequestDTOMapper implements Function<TeacherOfferRequest, TeacherOfferRequestDTO> {
    @Override
    public TeacherOfferRequestDTO apply(TeacherOfferRequest teacherOfferRequest) {
        return new TeacherOfferRequestDTO(
                teacherOfferRequest.getId(),
                teacherOfferRequest.getPaymentImageUrl(),
                teacherOfferRequest.getStatus(),
                teacherOfferRequest.getRequestDate(),
                teacherOfferRequest.getStartDate(),
                teacherOfferRequest.getEndDate(),
                teacherOfferRequest.getSuperTeacher() == null ? null :teacherOfferRequest.getSuperTeacher().getId(),
                teacherOfferRequest.getSuperTeacher() == null ? null : teacherOfferRequest.getSuperTeacher().getFullName(),
                teacherOfferRequest.getTeacherOffer() == null ? null :new TeacherOfferDTOMapper().apply(teacherOfferRequest.getTeacherOffer())
        );
    }
}
