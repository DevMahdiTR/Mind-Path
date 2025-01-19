package mindpath.core.service.stats;


import mindpath.core.domain.stats.Stats;
import mindpath.core.repository.UserEntityRepository;
import org.springframework.stereotype.Service;

@Service
public class StatsServiceImpl  implements StatsService{


    private final UserEntityRepository userEntityRepository;

    public StatsServiceImpl(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public Stats getStats() {
        return Stats.builder()
                .totalUsers(userEntityRepository.count())
                .totalOrders(userEntityRepository.countActiveStudentOrders() + userEntityRepository.countActiveTeacherOrders())
                .totalPrice(userEntityRepository.totalPrice())
                .totalPendingOrders(userEntityRepository.totalPendingOrders())
                .build();
    }

}
