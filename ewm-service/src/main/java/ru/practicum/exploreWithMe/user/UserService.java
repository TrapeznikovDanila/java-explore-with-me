package ru.practicum.exploreWithMe.user;

import ru.practicum.exploreWithMe.user.dto.NewUserRequest;
import ru.practicum.exploreWithMe.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveNewUser(NewUserRequest userRequest);

    List<UserDto> getUsers(Long[] ids, int from, int size);

    void deleteUser(long userId);

}
