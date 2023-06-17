package com.think.reactor.repository;

import com.think.reactor.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * User repository
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月17日 09:42:00
 */
@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
