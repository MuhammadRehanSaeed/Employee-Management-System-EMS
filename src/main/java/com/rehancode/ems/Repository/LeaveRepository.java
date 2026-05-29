package com.rehancode.ems.Repository;

import com.rehancode.ems.Model.AttendanceModel;
import com.rehancode.ems.Model.LeaveModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveModel,Long> {
    List<LeaveModel> findByEmployee_Id(Long empId);
}
