package com.rehancode.ems.Dto.MapStruct;

import com.rehancode.ems.Dto.ChangePasswordDTO;
import com.rehancode.ems.Dto.EmpRequestDTO;
import com.rehancode.ems.Dto.EmpResponseDTO;
import com.rehancode.ems.Model.EmployeeModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    //ToEntity
    @Mapping(target = "user", ignore = true)
    EmployeeModel toEntity (EmpRequestDTO empRequestDTO);

    //ToDTO
    EmpResponseDTO toDTO(EmployeeModel employeeModel);



}
