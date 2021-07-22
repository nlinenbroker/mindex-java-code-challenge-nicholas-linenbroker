package com.mindex.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataBootstrap {
    private static final String DATASTORE_LOCATION = "/static/employee_database.json";
    private static final String COMPENSATION_DATASTORE_LOCATION = "/static/compensation_database.json";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    CompensationRepository compensationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        InputStream inputStream = this.getClass().getResourceAsStream(DATASTORE_LOCATION);

        Employee[] employees;

        try {
            employees = objectMapper.readValue(inputStream, Employee[].class);

            // Ensure that the employees in all directReports fields are complete (previously, the program would assign
            // the directReports employees as Employee objects that have all fields except employeeId set to null
            for (Employee employee : employees) {
                List<Employee> directReports = employee.getDirectReports();
                if (directReports != null) {
                    List<Employee> updatedDirectReports = new ArrayList<>();
                    for (Employee report : directReports) {
                        String id = report.getEmployeeId();
                        Employee updatedReport = Arrays.stream(employees)
                                .filter(reportEmployee -> id.equals(reportEmployee.getEmployeeId()))
                                .findFirst()
                                .orElse(null);
                        updatedDirectReports.add(updatedReport);
                    }
                    employee.setDirectReports(updatedDirectReports);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Employee employee : employees) {
            employeeRepository.insert(employee);
        }

        // Initialize compensation repository as well
        InputStream compensationInputStream = this.getClass().getResourceAsStream(COMPENSATION_DATASTORE_LOCATION);

        Compensation[] compensations;

        try {
            compensations = objectMapper.readValue(compensationInputStream, Compensation[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Compensation compensation : compensations) {
            String employeeId = compensation.getEmployeeId();
            compensation.setEmployee(employeeRepository.findByEmployeeId(employeeId));
            compensationRepository.insert(compensation);
        }
    }
}
