package prueba.tecnica.libreria.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import prueba.tecnica.libreria.mapper.UserMapper;
import prueba.tecnica.libreria.model.dto.request.UserRequestDTO;
import prueba.tecnica.libreria.model.dto.response.UserResponseDTO;
import prueba.tecnica.libreria.model.entity.User;
import prueba.tecnica.libreria.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @Test
    void createUserShouldReturnCreatedUser() {
        UserRequestDTO request = userRequest("Ana", "Lopez", "ana@example.com", LocalDate.of(2000, 1, 10));
        User entity = user(1L, "Ana", "Lopez", "ana@example.com", LocalDate.of(2000, 1, 10));
        UserResponseDTO response = userResponse(1L, "Ana", "Lopez", "ana@example.com", LocalDate.of(2000, 1, 10));
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userService.createUser(entity)).thenReturn(entity);
        when(userMapper.toDto(entity)).thenReturn(response);

        var result = userController.createUser(request);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(userService).createUser(entity);
    }

    @Test
    void updateUserShouldReturnUpdatedUser() {
        UserRequestDTO request = userRequest("Ana Maria", "Lopez Diaz", "ana.maria@example.com", LocalDate.of(1999, 12, 31));
        User entity = user(1L, "Ana Maria", "Lopez Diaz", "ana.maria@example.com", LocalDate.of(1999, 12, 31));
        UserResponseDTO response = userResponse(1L, "Ana Maria", "Lopez Diaz", "ana.maria@example.com", LocalDate.of(1999, 12, 31));
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userService.updateUser(1L, entity)).thenReturn(entity);
        when(userMapper.toDto(entity)).thenReturn(response);

        var result = userController.updateUser(1L, request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(userService).updateUser(1L, entity);
    }

    @Test
    void deleteUserShouldReturnNoContent() {
        var result = userController.deleteUser(7L);

        assertEquals(204, result.getStatusCodeValue());
        verify(userService).deleteUser(7L);
    }

    @Test
    void getUserByIdShouldReturnUser() {
        User entity = user(1L, "Ana", "Lopez", "ana@example.com", LocalDate.of(2000, 1, 10));
        UserResponseDTO response = userResponse(1L, "Ana", "Lopez", "ana@example.com", LocalDate.of(2000, 1, 10));
        when(userService.getUserById(1L)).thenReturn(entity);
        when(userMapper.toDto(entity)).thenReturn(response);

        var result = userController.getUserById(1L);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    @Test
    void getAllUsersShouldReturnMappedUsers() {
        List<User> users = List.of(user(1L, "Ana", "Lopez", "ana@example.com", LocalDate.of(2000, 1, 10)));
        List<UserResponseDTO> responses = List.of(userResponse(1L, "Ana", "Lopez", "ana@example.com", LocalDate.of(2000, 1, 10)));
        when(userService.getAllUsers()).thenReturn(users);
        when(userMapper.toDtoList(users)).thenReturn(responses);

        var result = userController.getAllUsers();

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(responses, result.getBody());
    }

    private UserRequestDTO userRequest(String firstName, String lastName, String email, LocalDate birthDate) {
        return UserRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .birthDate(birthDate)
                .build();
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

    private UserResponseDTO userResponse(Long id, String firstName, String lastName, String email, LocalDate birthDate) {
        return UserResponseDTO.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .birthDate(birthDate)
                .build();
    }
}