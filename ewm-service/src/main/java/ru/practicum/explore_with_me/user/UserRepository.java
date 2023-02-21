package ru.practicum.explore_with_me.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.id IN :ids")
    Page<User> findAllByIds(Iterable<Long> ids, Pageable pageable);
}
