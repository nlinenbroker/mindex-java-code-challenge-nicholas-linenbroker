package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;
    private String lennonId;
    private String mccartneyId;
    private String starrId;
    private String bestId;
    private String harrisonId;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
        lennonId = "16a596ae-edd3-4847-99fe-c4518e82c86f";
        mccartneyId = "b7839309-3348-463b-a7e3-5de1c168beb3";
        starrId = "03aa1462-ffa9-4978-901b-7c001562cf6f";
        bestId = "62c1084e-6e34-4630-93fd-9153afb65309";
        harrisonId = "c0c2293d-16bd-4603-8e08-638a9d18b22c";
    }

    @Test
    public void testRead() {
        // Initial test using data from employee_database.json
        ReportingStructure lennonReportStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, lennonId).getBody();
        assertNotNull(lennonReportStructure);
        assertEquals(4, lennonReportStructure.getNumberOfReports());

        ReportingStructure mccartneyReportStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, mccartneyId).getBody();
        assertNotNull(mccartneyReportStructure);
        assertEquals(0, mccartneyReportStructure.getNumberOfReports());

        ReportingStructure starrReportStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, starrId).getBody();
        assertNotNull(starrReportStructure);
        assertEquals(2, starrReportStructure.getNumberOfReports());

        ReportingStructure bestReportStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, bestId).getBody();
        assertNotNull(bestReportStructure);
        assertEquals(0, bestReportStructure.getNumberOfReports());

        ReportingStructure harrisonReportStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, harrisonId).getBody();
        assertNotNull(harrisonReportStructure);
        assertEquals(0, harrisonReportStructure.getNumberOfReports());

        // Adding new employees and assigning them to others as directReports to test on the fly functionality if
        // employees are updated
        Employee casablancas = new Employee();
        casablancas.setFirstName("Julian");
        casablancas.setLastName("Casablancas");
        casablancas.setPosition("Intern");
        casablancas.setDepartment("Engineering");

        Employee turner = new Employee();
        turner.setFirstName("Alex");
        turner.setLastName("Turner");
        turner.setPosition("Intern");
        turner.setDepartment("Engineering");

        // Create + read new employees
        casablancas = restTemplate.postForEntity(employeeUrl, casablancas, Employee.class).getBody();
        String casablancasId = casablancas.getEmployeeId();
        casablancas = restTemplate.getForEntity(employeeIdUrl, Employee.class, casablancasId).getBody();

        turner = restTemplate.postForEntity(employeeUrl, turner, Employee.class).getBody();
        String turnerId = turner.getEmployeeId();
        turner = restTemplate.getForEntity(employeeIdUrl, Employee.class, turnerId).getBody();

        // Add new employees as direct reports to already existing employees
        Employee mccartney = restTemplate.getForEntity(employeeIdUrl, Employee.class, mccartneyId).getBody();
        List<Employee> mccartneyReports = mccartney.getDirectReports() != null
                ? mccartney.getDirectReports()
                : new ArrayList<>();
        mccartneyReports.add(casablancas);
        mccartney.setDirectReports(mccartneyReports);

        Employee best = restTemplate.getForEntity(employeeIdUrl, Employee.class, bestId).getBody();
        List<Employee> bestReports = best.getDirectReports() != null
                ? best.getDirectReports()
                : new ArrayList<>();
        bestReports.add(turner);
        best.setDirectReports(bestReports);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<>(mccartney, headers),
                Employee.class,
                mccartneyId);

        restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<>(best, headers),
                Employee.class,
                bestId);

        lennonReportStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, lennonId).getBody();
        assertNotNull(lennonReportStructure);
        assertEquals(6, lennonReportStructure.getNumberOfReports());

        mccartneyReportStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, mccartneyId).getBody();
        assertNotNull(mccartneyReportStructure);
        assertEquals(1, mccartneyReportStructure.getNumberOfReports());

        starrReportStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, starrId).getBody();
        assertNotNull(starrReportStructure);
        assertEquals(3, starrReportStructure.getNumberOfReports());

        bestReportStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, bestId).getBody();
        assertNotNull(bestReportStructure);
        assertEquals(1, bestReportStructure.getNumberOfReports());

        harrisonReportStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, harrisonId).getBody();
        assertNotNull(harrisonReportStructure);
        assertEquals(0, harrisonReportStructure.getNumberOfReports());

        ReportingStructure casablancasReportStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, casablancasId).getBody();
        assertNotNull(casablancasReportStructure);
        assertEquals(0, casablancasReportStructure.getNumberOfReports());

        ReportingStructure turnerReportStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, turnerId).getBody();
        assertNotNull(turnerReportStructure);
        assertEquals(0, turnerReportStructure.getNumberOfReports());
    }
}
