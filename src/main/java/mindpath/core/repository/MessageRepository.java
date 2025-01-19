package mindpath.core.repository;

import mindpath.core.domain.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.sender.email = :sender AND m.receiver.email = :receiver) OR (m.sender.email = :receiver AND m.receiver.email = :sender) ORDER BY m.timestamp")
    List<Message> findBySenderAndReceiver(@Param("sender") String sender, @Param("receiver")String receiver);

    @Query("SELECT m FROM Message m WHERE (m.sender.email = :sender AND m.receiver.email = :receiver) OR (m.sender.email = :receiver AND m.receiver.email = :sender) ORDER BY m.timestamp DESC LIMIT 1")
    Optional<Message> findLastMessageBySenderAndReceiver(@Param("sender")String sender, @Param("receiver")String receiver);
}
