package ru.practicum.explore_with_me.compilation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT c " +
            "FROM Compilation c " +
            "WHERE c.pinned = :pinned")
    Page<Compilation> findAll(Boolean pinned, Pageable pageable);

    Page<Compilation> findAll(Pageable pageable);
}
