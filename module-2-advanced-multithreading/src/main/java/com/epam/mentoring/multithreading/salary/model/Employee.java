package com.epam.mentoring.multithreading.salary.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Employee {

    private Long id;

    private String name;

    private BigDecimal salary;

}
