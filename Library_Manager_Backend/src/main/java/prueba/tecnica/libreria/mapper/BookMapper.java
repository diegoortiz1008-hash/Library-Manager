package prueba.tecnica.libreria.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import prueba.tecnica.libreria.model.dto.request.BookRequestDTO;
import prueba.tecnica.libreria.model.dto.response.BookCopyResponseDTO;
import prueba.tecnica.libreria.model.dto.response.BookResponseDTO;
import prueba.tecnica.libreria.model.entity.Book;
import prueba.tecnica.libreria.model.entity.BookCopy;

@Mapper(componentModel = "spring")
public interface BookMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "copies", ignore = true)
  Book toEntity(BookRequestDTO dto);

  BookResponseDTO toDto(Book entity);

  List<BookResponseDTO> toDtoList(List<Book> entities);

  @Mapping(source = "book.id", target = "bookId")
  BookCopyResponseDTO toDto(BookCopy entity);

  List<BookCopyResponseDTO> toCopyDtoList(List<BookCopy> entities);

}
