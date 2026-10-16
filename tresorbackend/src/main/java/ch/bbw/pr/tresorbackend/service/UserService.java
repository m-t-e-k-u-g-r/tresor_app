package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.dto.UserDto;
import ch.bbw.pr.tresorbackend.entities.User;

import java.util.List;

/**
 * UserService
 * @author Peter Rutschmann
 */
public interface UserService {
   User createUser(User user);

   User getUserById(Integer userId);

   User findByEmail(String email);

   List<User> getAllUsers();

   User updateUser(Integer userId, UserDto userDto);

   boolean deleteUser(Integer userId);
}
