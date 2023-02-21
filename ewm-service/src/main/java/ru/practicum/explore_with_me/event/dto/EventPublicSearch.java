package ru.practicum.explore_with_me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import ru.practicum.explore_with_me.event.SortVariants;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EventPublicSearch {
    private String text;
    private Set<Long> categories;
    private Boolean paid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp rangeStart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp rangeEnd;
    private boolean onlyAvailable;
    private SortVariants sort;
    private Pageable pageable;
}
