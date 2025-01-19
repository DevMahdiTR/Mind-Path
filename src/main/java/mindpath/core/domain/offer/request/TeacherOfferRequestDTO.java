package mindpath.core.domain.offer.request;

import mindpath.core.domain.offer.TeacherOfferDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record TeacherOfferRequestDTO (
    Long id,
    String paymentImageUrl,
    String status,
    LocalDateTime requestDate,
    LocalDateTime startDate,
    LocalDateTime endDate,
    UUID superTeacherId,
    String superTeacherName,
    TeacherOfferDTO teacherOffer
){
}
