package ru.practicum.exploreWithMe.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAll(Pageable pageable);

    @Query("select u from User u where u.id in :ids")
    Page<User> findAllByIds(Iterable<Long> ids, Pageable pageable);
}
