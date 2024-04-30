package de.openapi.petstore.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.openapi.petstore.db.UserRepository;
import de.openapi.petstore.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  private UserService userService;

  @BeforeEach
  public void setup() throws NoSuchAlgorithmException {
    userService = new UserService(userRepository, "mock");
    Mockito.reset(userRepository);
  }

  @Test
  public void shouldAddUser() {
    var username = "User1";
    var user = new User().username(username).password("test");
    var savedUser = new User().id(1L).username(username);
    when(userRepository.save(any())).thenReturn(savedUser);

    var addedUser = userService.addUser(user);

    verify(userRepository, times(1)).save(any());
    assertThat(addedUser.getUsername(), equalTo(username));
    assertThat(addedUser.getId(), equalTo(savedUser.getId()));
  }

  @Test
  public void shouldDeleteUser() {
    var username = "user1";
    userService.deleteUser(username);
    verify(userRepository, times(1)).deleteUserByUsername(username);
  }

  @Test
  public void shouldReturnUser() {
    var username = "user1";
    when(userRepository.findUserByUsername(anyString())).thenReturn(new User().username(username));

    var user = userService.findUserByName(username);

    verify(userRepository, times(1)).findUserByUsername(username);
    assertThat(user.getUsername(), equalTo(username));
  }

  @Test
  public void shouldLoginUser() {
    var foundUser = new User().id(3L).username("user 1")
        .password(userService.encodePassword("password"));
    when(userRepository.findUserByUsername(any())).thenReturn(foundUser);

    var login = userService.loginUser("username", "password");
    assertThat(login, is(nullValue()));
  }

  @Test
  public void shouldLogoutUser() {
    var login = userService.logoutUser();

    assertThat(login, containsString("User logged out"));
  }

  @Test
  public void shouldUpdateNewUser() {
    var existingUsername = "user3";
    var newUsername = "user2";
    var foundUsername = "user1";
    var newUser = new User().id(3L).username(newUsername).password("test");
    var savedUser = new User().id(2L).username(existingUsername);
    var foundUser = new User().id(1L).username(foundUsername);
    when(userRepository.findUserByUsername(anyString())).thenReturn(foundUser);
    when(userRepository.save(any())).thenReturn(savedUser);

    var updatedUser = userService.updateUser(existingUsername, newUser);

    verify(userRepository, times(1)).findUserByUsername(existingUsername);
    verify(userRepository, times(1)).save(any());
    assertThat(updatedUser.getId(), equalTo(savedUser.getId()));
  }

}
