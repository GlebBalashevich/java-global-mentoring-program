package com.epam.mentoring.multithreading.salary;

import java.util.List;

import com.epam.mentoring.multithreading.salary.model.Employee;

public class TestDataProvider {

    private TestDataProvider() {
    }

    public static List<Employee> getHiredEmployeesStub() {
        Employee employee1 = Employee.builder().id(1L).name("name1").build();
        Employee employee2 = Employee.builder().id(2L).name("name2").build();
        Employee employee3 = Employee.builder().id(3L).name("name3").build();
        Employee employee4 = Employee.builder().id(4L).name("name4").build();
        Employee employee5 = Employee.builder().id(5L).name("name5").build();
        Employee employee6 = Employee.builder().id(6L).name("name6").build();
        Employee employee7 = Employee.builder().id(7L).name("name7").build();
        Employee employee8 = Employee.builder().id(8L).name("name8").build();
        Employee employee9 = Employee.builder().id(9L).name("name9").build();
        Employee employee10 = Employee.builder().id(10L).name("name10").build();
        return List.of(employee1, employee2, employee3, employee4, employee5, employee6, employee7, employee8,
                employee9, employee10);
    }

}
