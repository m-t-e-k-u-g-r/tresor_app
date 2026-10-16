package ch.bbw.pr.tresorbackend.mappers;

import ch.bbw.pr.tresorbackend.dto.RegisterUser;
import ch.bbw.pr.tresorbackend.dto.UserDto;
import ch.bbw.pr.tresorbackend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterUser registerUser);
    @Mapping(source = "userRole.role", target = "role")
    UserDto toDto(User user);
}
