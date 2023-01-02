package ru.practicum.exploreWithMe.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.user.dto.NewUserRequest;
import ru.practicum.exploreWithMe.user.dto.UserDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto saveNewUser(NewUserRequest userRequest) {
        return UserMapper.makeUserDto(repository.save(UserMapper.makeUser(userRequest)));
    }

    @Override
    public List<UserDto> getUsers(Long[] ids, int from, int size) {
        Iterable<Long> ids2 = Arrays.asList(ids);
        List<UserDto> userDtos = repository.findAllByIds(ids2, PageRequest.of(from / size, size))
                .stream()
                .map(UserMapper::makeUserDto)
                .collect(Collectors.toList());
        return userDtos;
    }

    @Override
    public void deleteUser(long userId) {
        repository.deleteById(userId);
    }
}
