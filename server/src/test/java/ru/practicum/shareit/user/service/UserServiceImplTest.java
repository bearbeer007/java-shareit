package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private final UserMapper mapper = new UserMapperImpl();
    private User user;
    private User secondUser;


    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, mapper);
        user = new User();
        user.setId(1L);
        user.setName("testName");
        user.setEmail("testEmail@mail.ru");

        secondUser = new User();
        secondUser.setId(2L);
        secondUser.setName("testSecondName");
        secondUser.setEmail("testSecondEmail@mail.ru");


    }

    @Test
    public void shouldCreateUser() {
        UserDto expectedUser = new UserDto(1L, "testName", "testEmail@mail.ru");


        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        userService.create(expectedUser);
        assertEquals(mapper.toUserDto(user), expectedUser);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldGetUserById() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        userService.getUserById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldNotFoundGetUserById() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> userService.getUserById(1L));
    }

    @Test
    public void shouldReturnFindAllEmpty() {
        when(userRepository.findAll())
                .thenReturn(List.of());

        assertEquals(0, userService.getAllUsers().size());
        verify(userRepository).findAll();
    }

    @Test
    public void shouldGetAllUsers() {

        when(userRepository.findAll())
                .thenReturn(List.of(user, secondUser));

        assertEquals(2, userService.getAllUsers().size());
        verify(userRepository).findAll();
    }
}
