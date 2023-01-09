package com.epam.mentoring.multithreading.salary.api;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import com.epam.mentoring.multithreading.salary.model.Employee;

@Slf4j
public class EmployeeApi {

    public List<Employee> hiredEmployees() {
        log.info("Called api to retrieve all hired employees");
        // logic to call external endpoint and load employees data
        return Collections.emptyList();
    }

    public BigDecimal getSalary(Long employeeId) {
        log.info("Called api to retrieve employee's salary, id: {}", employeeId);
        // logic to call external endpoint and load employees salary
        return BigDecimal.ONE;
    }

}
