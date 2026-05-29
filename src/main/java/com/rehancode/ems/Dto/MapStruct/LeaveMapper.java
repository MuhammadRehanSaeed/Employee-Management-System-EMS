package com.rehancode.ems.Dto.MapStruct;

import com.rehancode.ems.Dto.LeaveRequestDTO;
import com.rehancode.ems.Dto.LeaveResponseDTO;
import com.rehancode.ems.Model.LeaveModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveMapper {

    LeaveModel leaveDtoToLeave(LeaveRequestDTO request);

    @Mapping(source = "leaveStatus", target = "status")
    @Mapping(source = "id",target = "id")
    LeaveResponseDTO leaveToLeaveResponseDTO(LeaveModel leave);
}
