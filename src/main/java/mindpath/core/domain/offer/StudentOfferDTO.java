package mindpath.core.domain.offer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class StudentOfferDTO {
        Long id;

        @Size(min = 5, max = 30, message = "Le titre doit comporter entre 5 et 30 caractères")
        @NotNull(message = "Le titre est obligatoire")
        String title;

        @Size(min = 5, max = 30, message = "Le sous-titre doit comporter entre 5 et 30 caractères")
        @NotNull(message = "Le sous-titre est obligatoire")
        String subTitle;

        @Size(min = 5, max = 150, message = "La description doit comporter entre 1 et 150 caractères")
        @NotNull(message = "La description est obligatoire")
        String description;

        @Positive(message = "Le prix doit être positif")
        @NotNull(message = "Le prix est obligatoire")
        Double price;

        @Positive(message = "La période mensuelle doit être positive")
        @NotNull(message = "La période mensuelle est obligatoire")
        Integer monthlyPeriod;

        String imageUrl;

        String offerDetails;

        LocalDateTime createdAt;

        Long classId;

        boolean isSubscribed;
}
