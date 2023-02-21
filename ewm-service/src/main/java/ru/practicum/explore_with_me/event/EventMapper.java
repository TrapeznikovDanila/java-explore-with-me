package ru.practicum.explore_with_me.event;

import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.category.CategoryMapper;
import ru.practicum.explore_with_me.event.comment.CommentMapper;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.request.RequestStates;
import ru.practicum.explore_with_me.user.UserMapper;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    public static Event makeEvent(NewEventDto eventDto) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .locationLat(eventDto.getLocation().getLat())
                .locationLon(eventDto.getLocation().getLon())
                .paid(eventDto.isPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.isRequestModeration())
                .title(eventDto.getTitle()).build();
    }

    public static EventFullDto makeEventFullDto(Event event) {
        EventFullDto eventFullDto = EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.makeCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.makeUserShortDto(event.getInitiator()))
                .location(new Location(event.getLocationLat(), event.getLocationLon()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
        if (event.getComments() != null) {
            eventFullDto.setComments(event.getComments().stream()
                    .map(CommentMapper::makeCommentDto).collect(Collectors.toList()));
        } else {
            eventFullDto.setComments(null);
        }
        if (event.getRequests() != null) {
            eventFullDto.setConfirmedRequests((int) event.getRequests()
                    .stream().filter(r -> r.getStatus().equals(RequestStates.CONFIRMED)).count());
        }
        return eventFullDto;
    }

    public static EventShortDto makeEventShortDto(Event event) {
        EventShortDto eventShortDto = EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.makeCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.makeUserShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(event.getViews()).build();
        if (event.getRequests() != null) {
            eventShortDto.setConfirmedRequests((int) event.getRequests()
                    .stream().filter(r -> r.getStatus().equals(RequestStates.CONFIRMED)).count());
        }
        return eventShortDto;
    }
}
