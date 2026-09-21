package prueba.tecnica.libreria.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import prueba.tecnica.libreria.model.dto.request.UserRequestDTO;
import prueba.tecnica.libreria.model.dto.response.UserResponseDTO;
import prueba.tecnica.libreria.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "loans", ignore = true)
  User toEntity(UserRequestDTO dto);

  UserResponseDTO toDto(User entity);

  List<UserResponseDTO> toDtoList(List<User> userList);

}
