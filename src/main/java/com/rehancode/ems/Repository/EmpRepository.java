package com.rehancode.ems.Repository;

import com.rehancode.ems.Model.EmployeeModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpRepository extends JpaRepository<EmployeeModel,Long> {


    boolean existsByUser_Id(Long userId);
}
