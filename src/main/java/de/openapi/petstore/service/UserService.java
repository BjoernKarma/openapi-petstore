package de.openapi.petstore.service;

import de.openapi.petstore.db.UserRepository;
import de.openapi.petstore.model.User;
import io.micrometer.observation.annotation.Observed;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// tag::javadoc[]

/**
 * A service that provides users.
 */
// end::javadoc[]
@Component
@Observed(name = "de.openapi.petstore.UserService")
public class UserService {

  private static final String LOGOUT_MESSAGE = "User logged out";
  private static final String LOGIN_ERROR_MESSAGE = "Invalid user data!";
  private final UserRepository userRepository;
  private final MessageDigest md;

  public UserService(@Autowired UserRepository userRepository,
                     @Value("${openapi.user.enabled}") String enabled)
          throws NoSuchAlgorithmException {
    this.md = MessageDigest.getInstance("SHA-512");
    this.userRepository = userRepository;
    if (enabled.equals("mock") && userRepository.findAll().isEmpty()) {
      // initialization of db
      for (long i = 1; i <= 10; i++) {
        this.userRepository.save(new User()
            .id(i)
            .username("testuser" + i)
            .password(encodePassword("pass" + i))
            .firstName("Test")
            .lastName("User " + i)
            .email("testuser" + i + "@github.com")
            .userStatus(1)
            .phone("0228/181345" + (i - 1))
        );
      }
    }
  }

  public User addUser(final User user) {
    user.setId(new SecureRandom().nextLong());
    user.setUsername(user.getUsername().toLowerCase());
    user.setPassword(encodePassword(user.getPassword()));
    return userRepository.save(user);
  }

  public void deleteUser(String username) {
    userRepository.deleteUserByUsername(username.toLowerCase());
  }

  public User findUserByName(String username) {
    return userRepository.findUserByUsername(username.toLowerCase());
  }

  public String loginUser(String username, String password) {
    if (username.isEmpty() && password.isEmpty()) {
      return LOGIN_ERROR_MESSAGE;
    }
    User user = findUserByName(username);
    if (user == null || !user.getPassword().equals(encodePassword(password))) {
      return LOGIN_ERROR_MESSAGE;
    }

    return null; // null means login okay
  }

  public String logoutUser() {
    return LOGOUT_MESSAGE;
  }

  public User updateUser(String username, User user) {
    User existingUser = userRepository.findUserByUsername(username);
    if (user == null) {
      return null;
    }
    user.setId(existingUser.getId());
    user.setPassword(encodePassword(user.getPassword()));
    return userRepository.save(user);
  }

  public String encodePassword(CharSequence rawPassword) {
    byte[] bytes = rawPassword.toString().getBytes(StandardCharsets.UTF_8);
    return new String(md.digest(bytes), StandardCharsets.UTF_8);
  }

}
