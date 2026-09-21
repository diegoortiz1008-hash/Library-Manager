package prueba.tecnica.libreria.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import prueba.tecnica.libreria.model.dto.request.LoanRequestDTO;
import prueba.tecnica.libreria.model.dto.response.LoanResponseDTO;
import prueba.tecnica.libreria.model.entity.Loan;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BookMapper.class})
public interface LoanMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "bookCopy", ignore = true)
  @Mapping(target = "actualReturnDate", ignore = true)
  Loan toEntity(LoanRequestDTO dto);

  @Mapping(source = "bookCopy.id", target = "bookCopyId")
  @Mapping(source = "bookCopy.book", target = "book")
  LoanResponseDTO toDto(Loan entity);

  List<LoanResponseDTO> toDtoList(List<Loan> entities);

}
