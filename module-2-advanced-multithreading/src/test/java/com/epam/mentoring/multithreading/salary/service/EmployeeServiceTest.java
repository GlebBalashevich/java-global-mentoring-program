package com.epam.mentoring.multithreading.salary.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.epam.mentoring.multithreading.salary.TestDataProvider;
import com.epam.mentoring.multithreading.salary.api.EmployeeApi;
import com.epam.mentoring.multithreading.salary.model.Employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeServiceTest {

    @Mock
    private EmployeeApi employeeApi;

    @Test
    void employeeTest() {
        employeeApi = mock(EmployeeApi.class);
        EmployeeService employeeService = new EmployeeService(employeeApi);
        List<Employee> hiredEmployees = TestDataProvider.getHiredEmployeesStub();

        when(employeeApi.hiredEmployees()).thenReturn(hiredEmployees);
        when(employeeApi.getSalary(hiredEmployees.get(0).getId())).thenReturn(BigDecimal.valueOf(100));
        when(employeeApi.getSalary(hiredEmployees.get(1).getId())).thenReturn(BigDecimal.valueOf(200));
        when(employeeApi.getSalary(hiredEmployees.get(2).getId())).thenReturn(BigDecimal.valueOf(300));
        when(employeeApi.getSalary(hiredEmployees.get(3).getId())).thenReturn(BigDecimal.valueOf(400));
        when(employeeApi.getSalary(hiredEmployees.get(4).getId())).thenReturn(BigDecimal.valueOf(500));
        when(employeeApi.getSalary(hiredEmployees.get(5).getId())).thenReturn(BigDecimal.valueOf(600));
        when(employeeApi.getSalary(hiredEmployees.get(6).getId())).thenReturn(BigDecimal.valueOf(700));
        when(employeeApi.getSalary(hiredEmployees.get(7).getId())).thenReturn(BigDecimal.valueOf(800));
        when(employeeApi.getSalary(hiredEmployees.get(8).getId())).thenReturn(BigDecimal.valueOf(900));
        when(employeeApi.getSalary(hiredEmployees.get(9).getId())).thenReturn(BigDecimal.valueOf(1000));

        employeeService.getHiredEmployees();

        verify(employeeApi).hiredEmployees();
        verify(employeeApi, times(10)).getSalary(any());
    }

}
