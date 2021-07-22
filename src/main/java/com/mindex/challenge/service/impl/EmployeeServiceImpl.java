package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        // Check and update all employees in the repository that have the specified employee listed in directReports,
        // necessary to keep all employee and reportingStructure read requests accurate if an employee update involving
        // directReports occurs
        String employeeId = employee.getEmployeeId();
        List<Employee> employees = employeeRepository.findAll();
        for(Employee potentialEmployeeToUpdate : employees) {
            List<Employee> directReports = potentialEmployeeToUpdate.getDirectReports();
            if(directReports != null) {
                for (Employee report : directReports) {
                    String reportId = report.getEmployeeId();
                    if (employeeId.equals(reportId)) {
                        directReports.remove(report);
                        directReports.add(employee);
                        potentialEmployeeToUpdate.setDirectReports(directReports);
                        update(potentialEmployeeToUpdate);
                        break;
                    }
                }
            }
        }
        return employeeRepository.save(employee);
    }
}
