package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String employeeIdUrl;
    private String employeeId;
    private String compensationCreateUrl;
    private String compensationReadUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        employeeId = "03aa1462-ffa9-4978-901b-7c001562cf6f";
        compensationCreateUrl = "http://localhost:" + port + "/compensation";
        compensationReadUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void testCreateRead() {
        // Read in employee for testing purposes
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, employeeId).getBody();

        // Compensation create checks
        Compensation testCompensation = new Compensation();
        testCompensation.setSalary(50000);
        LocalDate effectiveDate = LocalDate.parse("2020-05-04");
        testCompensation.setEffectiveDate(effectiveDate);
        testCompensation.setEmployeeId(readEmployee.getEmployeeId());
        testCompensation.setEmployee(readEmployee);
        Compensation createdCompensation = restTemplate.postForEntity(compensationCreateUrl, testCompensation,
                Compensation.class).getBody();
        assertNotNull(createdCompensation);
        assertCompensationEquivalence(testCompensation, createdCompensation);

        // Compensation read checks
        Compensation readCompensation = restTemplate.getForEntity(compensationReadUrl, Compensation.class,
                createdCompensation.getEmployeeId()).getBody();
        assertNotNull(readCompensation);
        assertCompensationEquivalence(createdCompensation, readCompensation);
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }
}
