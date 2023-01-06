package ru.practicum.explore_with_me.user;

import ru.practicum.explore_with_me.user.dto.NewUserRequest;
import ru.practicum.explore_with_me.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveNewUser(NewUserRequest userRequest);

    List<UserDto> getUsers(Long[] ids, int from, int size);

    void deleteUser(long userId);

}
