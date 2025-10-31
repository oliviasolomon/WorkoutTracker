package edu.vt.workout.repo;

import edu.vt.workout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

//--
// extends the spring data jpa's jparepository to provide crud operations
// for the user entity without requiring manual sql.
//
// used by: usercontroller, userservice
//--

public interface UserRepository extends JpaRepository<User, Long> {
    // finder method (like SELECT * FROM users WHERE username = ?)
    Optional<User> findByUsername(String username);
}
