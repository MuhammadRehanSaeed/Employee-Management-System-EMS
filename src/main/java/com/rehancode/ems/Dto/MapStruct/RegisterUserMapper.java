package com.rehancode.ems.Dto.MapStruct;

import com.rehancode.ems.Dto.UserRequestDTO;
import com.rehancode.ems.Dto.UserResponseDTO;
import com.rehancode.ems.Model.UsersModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegisterUserMapper {

    UserResponseDTO mapToDto(UsersModel usersModel);

    @Mapping(target = "password", ignore = true)
    UsersModel mapToEntity(UserRequestDTO userRequestDTO);


}