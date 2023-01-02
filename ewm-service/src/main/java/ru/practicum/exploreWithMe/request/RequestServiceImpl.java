package ru.practicum.exploreWithMe.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.event.Event;
import ru.practicum.exploreWithMe.event.EventRepository;
import ru.practicum.exploreWithMe.exception.ErrorStatus;
import ru.practicum.exploreWithMe.exception.NotFoundException;
import ru.practicum.exploreWithMe.exception.ValidationException;
import ru.practicum.exploreWithMe.request.dto.ParticipationRequestDto;
import ru.practicum.exploreWithMe.user.User;
import ru.practicum.exploreWithMe.user.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto saveNewRequest(long userId, long eventId) {
        Request request = new Request();
        request.setCreated(Timestamp.from(Instant.now()));
        request.setStatus(RequestStates.PENDING);
        setRequester(request, userId);
        setEvent(request, eventId);
        return RequestMapper.makeRequestDto(repository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestByUserId(long userId) {
        return repository.findAllByRequester_Id(userId).stream().map(RequestMapper::makeRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto canceledRequestByRequester(long userId, long requestId) {
        Optional<Request> requestOptional = repository.findById(requestId);
        if (requestOptional.isPresent()) {
            Request request = requestOptional.get();
            if (request.getRequester().getId() == userId) {
                request.setStatus(RequestStates.CANCELED);
                return RequestMapper.makeRequestDto(repository.save(request));
            }
            throw new ValidationException(null, ErrorStatus.CONFLICT, "This requests belongs to another user.",
                    String.format("Request with id=%s belongs to another user.", requestId),
                    LocalDateTime.now());
        }
        throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The request object was not found.",
                String.format("Request with id=%s was not found.", requestId),
                LocalDateTime.now());
    }

    private void setRequester(Request request, long userId) {
        Optional<User> requesterOptional = userRepository.findById(userId);
        if (requesterOptional.isPresent()) {
            request.setRequester(requesterOptional.get());
        } else {
            throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The user object was not found.",
                    String.format("User with id=%s was not found.", userId),
                    LocalDateTime.now());
        }
    }

    private void setEvent(Request request, long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            request.setEvent(eventOptional.get());
        } else {
            throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The event object was not found.",
                    String.format("Event with id=%s was not found.", eventId),
                    LocalDateTime.now());
        }
    }


//    private final EventRepository repository;
//
//    private final CategoryRepository categoryRepository;
//
//    private final UserRepository userRepository;
//
//    @Override
//    public EventFullDto saveNewEvent(long userId, NewEventDto eventDto) {
//        Event event = EventMapper.makeEvent(eventDto);
//        event.setCategory(categoryRepository.findById(eventDto.getCategory()).get());
//        event.setInitiator(userRepository.findById(userId).get());
//        event.setCreatedOn(LocalDateTime.now());
//        event.setState(EventStates.PENDING);
//        return EventMapper.makeEventFullDto(repository.save(event));
//    }


//    @Override
//    public UserDto saveNewUser(NewUserRequest userRequest) {
//        return UserMapper.makeUserDto(repository.save(UserMapper.makeUser(userRequest)));
//    }
//
//    @Override
//    public List<UserDto> getUsers(Long[] ids, int from, int size) {
//        Iterable<Long> ids2 = Arrays.asList(ids);
//        List<UserDto> userDtos = repository.findAllByIds(ids2, PageRequest.of(from / size, size))
//                .stream()
//                .map(UserMapper::makeUserDto)
//                .collect(Collectors.toList());
//        return userDtos;
//    }
//
//    @Override
//    public void deleteUser(long userId) {
//        repository.deleteById(userId);
//    }
}
