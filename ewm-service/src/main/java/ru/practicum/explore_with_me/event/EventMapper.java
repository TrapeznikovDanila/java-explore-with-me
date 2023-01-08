package ru.practicum.explore_with_me.event;

import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.category.CategoryMapper;
import ru.practicum.explore_with_me.event.comment.CommentMapper;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.user.UserMapper;

import java.util.stream.Collectors;

@Component
public class EventMapper {

    public static Event makeEvent(NewEventDto eventDto) {
        Event event = new Event();
        event.setAnnotation(eventDto.getAnnotation());
        event.setDescription(eventDto.getDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setLocationLat(eventDto.getLocation().getLat());
        event.setLocationLon(eventDto.getLocation().getLon());
        event.setPaid(eventDto.isPaid());
        event.setParticipantLimit(eventDto.getParticipantLimit());
        event.setRequestModeration(eventDto.isRequestModeration());
        event.setTitle(eventDto.getTitle());
        return event;
    }

    public static EventFullDto makeEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(CategoryMapper.makeCategoryDto(event.getCategory()));
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(UserMapper.makeUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(new Location(event.getLocationLat(), event.getLocationLon()));
        eventFullDto.setPaid(event.isPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setRequestModeration(event.isRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());
        if (event.getComments() != null) {
            eventFullDto.setComments(event.getComments().stream()
                    .map(CommentMapper::makeCommentDto).collect(Collectors.toList()));
        } else {
            eventFullDto.setComments(null);
        }
        return eventFullDto;
    }

    public static EventShortDto makeEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.makeCategoryDto(event.getCategory()));
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setId(event.getId());
        eventShortDto.setInitiator(UserMapper.makeUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.isPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setViews(event.getViews());
        return eventShortDto;
    }
}
