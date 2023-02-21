package ru.practicum.explore_with_me.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.event.Event;
import ru.practicum.explore_with_me.event.EventRepository;
import ru.practicum.explore_with_me.exception.ErrorStatus;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.exception.ValidationException;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.user.User;
import ru.practicum.explore_with_me.user.UserRepository;

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
    public ParticipationRequestDto saveNewRequest(Long userId, Long eventId) {
        Request request = Request.builder()
                .created(Timestamp.from(Instant.now()))
                .status(RequestStates.PENDING).build();
        setRequester(request, userId);
        setEvent(request, eventId);
        return RequestMapper.makeRequestDto(repository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestByUserId(Long userId) {
        return repository.findAllByRequester_Id(userId).stream().map(RequestMapper::makeRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto canceledRequestByRequester(Long userId, Long requestId) {
        Optional<Request> requestOptional = repository.findById(requestId);
        if (requestOptional.isPresent()) {
            Request request = requestOptional.get();
            if (request.getRequester().getId().equals(userId)) {
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

    private void setRequester(Request request, Long userId) {
        Optional<User> requesterOptional = userRepository.findById(userId);
        if (requesterOptional.isPresent()) {
            request.setRequester(requesterOptional.get());
        } else {
            throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The user object was not found.",
                    String.format("User with id=%s was not found.", userId),
                    LocalDateTime.now());
        }
    }

    private void setEvent(Request request, Long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            request.setEvent(eventOptional.get());
        } else {
            throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The event object was not found.",
                    String.format("Event with id=%s was not found.", eventId),
                    LocalDateTime.now());
        }
    }
}
