package ua.com.tracktor.kombine.data;

import org.springframework.data.repository.CrudRepository;
import ua.com.tracktor.kombine.entity.User;

import java.sql.Timestamp;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Timestamp> {
    Optional<User> findByAccountId(String account);
}
