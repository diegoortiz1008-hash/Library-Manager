package prueba.tecnica.libreria.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import prueba.tecnica.libreria.model.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
    
}
