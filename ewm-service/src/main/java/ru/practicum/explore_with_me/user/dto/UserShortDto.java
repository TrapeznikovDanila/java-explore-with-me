package ru.practicum.explore_with_me.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserShortDto {
    private Long id;
    private String name;
}
