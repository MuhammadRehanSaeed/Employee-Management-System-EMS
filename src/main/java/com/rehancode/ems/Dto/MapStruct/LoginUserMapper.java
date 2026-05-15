package com.rehancode.ems.Dto.MapStruct;

import com.rehancode.ems.Dto.LoginResponseDTO;
import com.rehancode.ems.Model.UsersModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginUserMapper {

    LoginResponseDTO mapToDto(UsersModel usersModel);
}
