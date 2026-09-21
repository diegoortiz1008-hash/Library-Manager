package prueba.tecnica.libreria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import prueba.tecnica.libreria.model.entity.User;
import prueba.tecnica.libreria.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserShouldPersistCopiedUser() {
        User input = user(1L, "Ana", "Lopez", "ana@example.com", LocalDate.of(2000, 1, 10));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(input);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertNotSame(input, savedUser);
        assertEquals(input.getFirstName(), savedUser.getFirstName());
        assertEquals(input.getLastName(), savedUser.getLastName());
        assertEquals(input.getEmail(), savedUser.getEmail());
        assertEquals(input.getBirthDate(), savedUser.getBirthDate());
        assertEquals(savedUser, result);
    }

    @Test
    void updateUserShouldModifyExistingUser() {
        User existing = user(1L, "Ana", "Lopez", "ana@example.com", LocalDate.of(2000, 1, 10));
        User update = user(null, "Ana Maria", "Lopez Diaz", "ana.maria@example.com", LocalDate.of(1999, 12, 31));
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1L, update);

        verify(userRepository).save(existing);
        assertEquals("Ana Maria", result.getFirstName());
        assertEquals("Lopez Diaz", result.getLastName());
        assertEquals("ana.maria@example.com", result.getEmail());
        assertEquals(LocalDate.of(1999, 12, 31), result.getBirthDate());
    }

    @Test
    void deleteUserShouldRemoveExistingUser() {
        User existing = user(1L, "Ana", "Lopez", "ana@example.com", LocalDate.of(2000, 1, 10));
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        userService.deleteUser(1L);

        verify(userRepository).delete(existing);
    }

    @Test
    void getUserByIdShouldThrowWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(99L));

        assertEquals("User not found with id: 99", exception.getMessage());
    }

    @Test
    void getAllUsersShouldReturnRepositoryContent() {
        List<User> users = List.of(
                user(1L, "Ana", "Lopez", "ana@example.com", LocalDate.of(2000, 1, 10)),
                user(2L, "Luis", "Perez", "luis@example.com", LocalDate.of(1998, 5, 20)));
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users, result);
    }

    private User user(Long id, String firstName, String lastName, String email, LocalDate birthDate) {
        return User.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .birthDate(birthDate)
                .build();
    }
}