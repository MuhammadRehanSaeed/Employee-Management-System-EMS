package com.rehancode.ems.Repository;

import com.rehancode.ems.Dto.AttendanceHistoryDTO;
import com.rehancode.ems.Model.AttendanceModel;
import com.rehancode.ems.Model.EmployeeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceModel,Long> {

    Optional<List<AttendanceModel>> findByEmployee_Id(Long emp_id);
    Optional<AttendanceModel> findByEmployeeAndAttendanceDate(EmployeeModel employee, LocalDate attendanceDate);
    Optional<AttendanceModel> findByemployee_Id(Long empid);
    // REPOSITORY

    List<AttendanceModel> findByAttendanceDate(LocalDate attendanceDate);

}
