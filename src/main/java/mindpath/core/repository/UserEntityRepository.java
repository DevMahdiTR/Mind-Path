package mindpath.core.repository;


import mindpath.core.domain.auth.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {

    @Query(value = "SELECT U FROM UserEntity U WHERE U.email = :email")
    Optional<UserEntity> fetchUserByEmail(@Param("email") final String email);

    @Query(value = "SELECT U FROM UserEntity U WHERE U.id = :id")
    Optional<UserEntity> fetchUserById(@Param("id") final UUID id);

    @Query(value = "SELECT EXISTS(SELECT U FROM UserEntity U WHERE U.email = :email) ")
    boolean isEmailRegistered(@Param("email") final String email);

    @Query(value = "SELECT EXISTS(SELECT U FROM UserEntity U WHERE U.phoneNumber = :phoneNumber) ")
    boolean isPhoneNumberRegistered(@Param("phoneNumber") final String phoneNumber);

    @Query(
            "SELECT U FROM UserEntity U WHERE " +
                    "(coalesce(LOWER(:fullName), '') = '' OR LOWER(U.fullName) LIKE CONCAT('%', LOWER(:fullName), '%')) AND " +
                    "(coalesce(LOWER(:email), '') = '' OR LOWER(U.email) LIKE CONCAT('%', LOWER(:email), '%')) AND " +
                    "(coalesce(LOWER(:role), '') = '' OR LOWER(U.role.name) LIKE CONCAT('%', LOWER(:role), '%')) AND " +
                    "(coalesce(:phoneNumber, '') = '' OR U.phoneNumber LIKE CONCAT('%', :phoneNumber, '%')) AND " +
                    "(coalesce(:isEnabled, U.isEnabled) = U.isEnabled)"
    )
    List<UserEntity> findUsers(
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("role") String role,
            @Param("phoneNumber") String phoneNumber,
            @Param("isEnabled") Boolean isEnabled
    );

    @Query("SELECT COUNT(u) FROM UserEntity u")
    long countUsers();

    @Query("SELECT COUNT(t) FROM TeacherOfferRequest t " +
            "WHERE t.endDate > CURRENT_TIMESTAMP AND t.status = 'ACCEPTED'")
    long countActiveTeacherOrders();

    @Query("SELECT COUNT(s) FROM StudentOfferRequest s " +
            "WHERE s.endDate > CURRENT_TIMESTAMP AND s.status = 'ACCEPTED'")
    long countActiveStudentOrders();



    @Query("SELECT COALESCE((SELECT SUM(s.studentOffer.price) FROM StudentOfferRequest s WHERE s.status = 'ACCEPTED'), 0) + COALESCE((SELECT SUM(t.teacherOffer.price) FROM TeacherOfferRequest t WHERE t.status = 'ACCEPTED'), 0)")
    double totalPrice();

    @Query("SELECT (SELECT COUNT(s) FROM StudentOfferRequest s WHERE s.status = 'PENDING') + (SELECT COUNT(t) FROM TeacherOfferRequest t WHERE t.status = 'PENDING')")
    long totalPendingOrders();

}
