package com.epam.mentoring.multithreading.salary.api;

import java.math.BigDecimal;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeApiTest {

    private EmployeeApi employeeApi;

    @BeforeEach
    void init() {
        employeeApi = new EmployeeApi();
    }

    @Test
    void testHiredEmployees() {
        Assertions.assertThat(employeeApi.hiredEmployees()).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    void testGetSalary() {
        Assertions.assertThat(employeeApi.getSalary(1L)).isEqualTo(BigDecimal.ONE);
    }

}
