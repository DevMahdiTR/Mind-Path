package mindpath.core.domain.offer.request;

import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.offer.TeacherOffer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "teacher_offer_requests")
public class TeacherOfferRequest {

    @SequenceGenerator(
            name = "teacher_request_sequence",
            sequenceName = "teacher_request_sequence",
            allocationSize = 1
    )

    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "teacher_request_sequence")
    @Id
    private Long id;

    @Column(name = "payment_image_url", nullable = false, columnDefinition = "TEXT")
    private String paymentImageUrl;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "start_date", nullable = true)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "super_teacher_id", referencedColumnName = "id", nullable = false)
    private SuperTeacher superTeacher;

    @OneToOne
    @JoinColumn(name = "teacher_offer_id", referencedColumnName = "id", nullable = false)
    private TeacherOffer teacherOffer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeacherOfferRequest offerRequest = (TeacherOfferRequest) o;
        return id.equals(offerRequest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
