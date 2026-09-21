package prueba.tecnica.libreria.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import prueba.tecnica.libreria.model.entity.User;
import prueba.tecnica.libreria.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    //Create a User
    @Transactional
    public User createUser(User user) {
        User createdUser = User.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .loans(user.getLoans())
                .build();

        User savedUser = userRepository.save(createdUser);
        return savedUser;
    }

    //Update a User
    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setBirthDate(user.getBirthDate());

        User updatedUser = userRepository.save(existingUser);
        return updatedUser;
    }

    //Delete a User
    @Transactional
    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(existingUser);
    }

    //Get a User by ID
    @Transactional
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    //Get all Users
    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    
}
