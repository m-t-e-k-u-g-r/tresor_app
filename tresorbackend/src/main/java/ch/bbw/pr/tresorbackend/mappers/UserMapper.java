package ch.bbw.pr.tresorbackend.mappers;

import ch.bbw.pr.tresorbackend.dto.RegisterUser;
import ch.bbw.pr.tresorbackend.dto.UserDto;
import ch.bbw.pr.tresorbackend.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterUser registerUser);
    UserDto toDto(User user);
}
