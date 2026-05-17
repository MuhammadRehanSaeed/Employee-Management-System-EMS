package com.rehancode.ems.Dto.MapStruct;

import com.rehancode.ems.Dto.AttendanceHistoryDTO;
import com.rehancode.ems.Model.AttendanceModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    //att-model to dto
    @Mapping(target = "employeeId", source = "employee.id")
    AttendanceHistoryDTO toDTO (AttendanceModel attendanceModel);

}
