package mindpath.core.domain.stats;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class Stats {

    long totalUsers;
    long totalOrders;
    double totalPrice;
    long totalPendingOrders;
}
