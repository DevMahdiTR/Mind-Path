package mindpath.core.domain.offer.request;

import mindpath.core.domain.offer.StudentOfferDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentOfferRequestDTO (
    Long id,
    String paymentImageUrl,
    String status,
    LocalDateTime requestDate,
    LocalDateTime startDate,
    LocalDateTime endDate,
    UUID studentId,
    String studentName,
    StudentOfferDTO studentOffer
){
}
