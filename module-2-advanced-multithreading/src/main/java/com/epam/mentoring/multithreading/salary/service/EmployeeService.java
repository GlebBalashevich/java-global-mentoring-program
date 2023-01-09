package com.epam.mentoring.multithreading.salary.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.epam.mentoring.multithreading.salary.api.EmployeeApi;
import com.epam.mentoring.multithreading.salary.model.Employee;

@Slf4j
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeApi employeeApi;

    public void getHiredEmployees() {
        CompletionStage<List<Employee>> fetchedEmployees = fetchHiredEmployees();
        List<CompletableFuture<Employee>> employeeStages = fetchedEmployees.thenApplyAsync(this::fillEmployeesSalary)
                .toCompletableFuture().join();
        CompletableFuture<Void> allStagesComplete = CompletableFuture.allOf(
                employeeStages.toArray(new CompletableFuture[0]));
        allStagesComplete.thenAcceptAsync(
                allDone -> employeeStages.forEach(employeeCompletableFuture -> employeeCompletableFuture
                        .thenAccept(employee -> log.info("{}", employee))));
    }

    private CompletionStage<List<Employee>> fetchHiredEmployees() {
        log.info("Request to fetch hired employees");
        return CompletableFuture.supplyAsync(employeeApi::hiredEmployees);
    }

    private List<CompletableFuture<Employee>> fillEmployeesSalary(List<Employee> employees) {
        return employees.stream().map(e -> fetchEmployeeSalary(e.getId())
                .thenApply(salary -> fillEmployeeSalary(e, salary)).toCompletableFuture())
                .toList();
    }

    private CompletionStage<BigDecimal> fetchEmployeeSalary(Long employeeId) {
        log.info("Request to fetch hired employee's with id: {} salary", employeeId);
        return CompletableFuture.supplyAsync(() -> employeeApi.getSalary(employeeId));
    }

    private Employee fillEmployeeSalary(Employee employee, BigDecimal salary) {
        employee.setSalary(salary);
        return employee;
    }

}
