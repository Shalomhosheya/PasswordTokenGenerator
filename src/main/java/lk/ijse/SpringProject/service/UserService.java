package lk.ijse.SpringProject.service;

import lk.ijse.SpringProject.dto.UserDto;

public interface UserService {
    int saveUser(UserDto userDto);
    UserDto searhUser(String username);
}
