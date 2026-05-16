package com.rehancode.ems.Repository;

import com.rehancode.ems.Model.EmployeeModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpRepository extends JpaRepository<EmployeeModel,Long> {


    boolean existsByUser_Id(Long userId);

    Optional<EmployeeModel> findByUser_Id(Long userId);
}
