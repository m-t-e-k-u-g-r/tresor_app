package ch.bbw.pr.tresorbackend.service.impl;

import ch.bbw.pr.tresorbackend.dto.CustomUserDetails;
import ch.bbw.pr.tresorbackend.dto.UserDto;
import ch.bbw.pr.tresorbackend.entities.User;
import ch.bbw.pr.tresorbackend.entities.UserRole;
import ch.bbw.pr.tresorbackend.repository.UserRepository;
import ch.bbw.pr.tresorbackend.repository.UserRoleRepository;
import ch.bbw.pr.tresorbackend.service.SafeDbCall;
import ch.bbw.pr.tresorbackend.service.UserService;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * UserServiceImpl
 * @author Peter Rutschmann
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
   private final UserRoleRepository userRoleRepository;
   private UserRepository userRepository;

   @Override
   public User createUser(User user) {
      return SafeDbCall.safeDbCall(() -> userRepository.save(user), null);
   }

   @Override
   public User getUserById(Integer userId) {
      Optional<User> user = SafeDbCall.safeDbCall(() -> userRepository.findById(userId), Optional.empty());
       return user.orElse(null);
   }

   @Override
   public User findByEmail(String email) {
      Optional<User> user = SafeDbCall.safeDbCall(() -> userRepository.findByEmail(email), Optional.empty());
       return user.orElse(null);
   }

   @Override
   public List<User> getAllUsers() {
      return SafeDbCall.safeDbCall(() -> userRepository.findAll(), List.of());
   }

   @Override
   public User updateUser(Integer userId, UserDto userDto) {
      User user = SafeDbCall.safeDbCall(() ->
              userRepository.findById(userId).orElse(null), null);
      if (user == null) return null;

      if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
      if (userDto.getFirstName() != null) user.setFirstName(userDto.getFirstName());
      if (userDto.getLastName() != null) user.setLastName(userDto.getLastName());

      if (userDto.getRole() != null) {
         UserRole role = SafeDbCall.safeDbCall(() ->
                 userRoleRepository.findByRole(userDto.getRole()).orElse(null), null);
         if (role == null) return null;

         user.setUserRole(role);
      }

      return userRepository.save(user);
   }

   @Override
   public boolean deleteUser(Integer userId) {
      return SafeDbCall.safeDbCall(() -> userRepository.deleteById(userId));
   }

   @Override
   public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
      User user = findByEmail(email);
      if (user == null) throw new UsernameNotFoundException("No user with email " + email + " found");

      return new CustomUserDetails(user);
   }
}
