package com.mindex.challenge.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReportingStructure {
    private Employee employee;
    private int numberOfReports;

    public ReportingStructure() {

    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getNumberOfReports() { return numberOfReports; }

    public void setNumberOfReports() {
        // HashSet used to ensure no duplicates
        Set<Employee> distinctReports = new HashSet<>();
        distinctReports = getDistinctReports(employee, distinctReports);
        numberOfReports = distinctReports.size();
    }

    // Recursive helper function for setting the number of reports on the fly
    public Set<Employee> getDistinctReports(Employee employee, Set<Employee> distinctReports) {
        List<Employee> directReports = employee.getDirectReports();
        if (directReports != null) {
            for (Employee report : directReports) {
                int currentDistinctReportsCount = distinctReports.size();
                distinctReports.add(report);
                int newDistinctReportsCount = distinctReports.size();
                if (currentDistinctReportsCount != newDistinctReportsCount) {
                    distinctReports = getDistinctReports(report, distinctReports);
                }
            }
        }
        return distinctReports;
    }
}
