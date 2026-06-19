package com.healthtrack.bmi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthtrack.bmi.dto.BmiCalculateRequest;
import com.healthtrack.bmi.dto.LoginRequest;
import com.healthtrack.bmi.dto.RegisterRequest;
import com.healthtrack.bmi.dto.UnitSystem;
import com.healthtrack.bmi.repository.BmiCalculationRepository;
import com.healthtrack.bmi.repository.DoctorRepository;
import com.healthtrack.bmi.repository.PatientRepository;
import com.healthtrack.bmi.entity.Patient;
import com.healthtrack.bmi.security.JwtUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BmiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private BmiCalculationRepository bmiCalculationRepository;

    @BeforeEach
    void cleanDatabase() {
        bmiCalculationRepository.deleteAll();
        patientRepository.deleteAll();
        doctorRepository.deleteAll();
    }

    @Test
    void testDoctorRegistrationAndSingleDoctorEnforcement() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("doctor1@clinic.com")
                .password("password123")
                .name("Dr. Alice")
                .build();

        // 1. First registration must succeed and return Cookie
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.email").value("doctor1@clinic.com"))
                .andExpect(jsonPath("$.name").value("Dr. Alice"));

        assertEquals(1L, doctorRepository.count());

        // 2. Second registration must fail (only 1 doctor allowed)
        RegisterRequest request2 = RegisterRequest.builder()
                .email("doctor2@clinic.com")
                .password("password123")
                .name("Dr. Bob")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Registration is disabled. Only one doctor account can exist in the system."));

        assertEquals(1L, doctorRepository.count());
    }

    @Test
    void testDoctorLoginFlow() throws Exception {
        // Register a doctor
        RegisterRequest request = RegisterRequest.builder()
                .email("doc@clinic.com")
                .password("password123")
                .name("Dr. Watson")
                .build();
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Attempt login
        LoginRequest loginRequest = LoginRequest.builder()
                .email("doc@clinic.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(cookie().exists(JwtUtils.COOKIE_NAME))
                .andExpect(cookie().httpOnly(JwtUtils.COOKIE_NAME, true))
                .andExpect(jsonPath("$.email").value("doc@clinic.com"));
    }

    @Test
    void testPublicBmiCalculationAndDeduplication() throws Exception {
        BmiCalculateRequest calcRequest1 = BmiCalculateRequest.builder()
                .name("John Doe")
                .phone("9876543210")
                .age(30)
                .gender("Male")
                .height(180.0)
                .weight(80.0)
                .unitSystem(UnitSystem.METRIC)
                .build();

        // 1. Initial calculation creates a patient and history record
        mockMvc.perform(post("/api/bmi/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(calcRequest1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bmiValue").value(24.69))
                .andExpect(jsonPath("$.classification").value("Normal weight"));

        assertEquals(1L, patientRepository.count());
        assertEquals(1L, bmiCalculationRepository.count());

        // 2. Secondary calculation with same phone updates name/age, appends history, deduplicates patient
        BmiCalculateRequest calcRequest2 = BmiCalculateRequest.builder()
                .name("John Updated")
                .phone("9876543210")
                .age(31)
                .gender("Male")
                .height(180.0)
                .weight(85.0)
                .unitSystem(UnitSystem.METRIC)
                .build();

        mockMvc.perform(post("/api/bmi/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(calcRequest2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bmiValue").value(26.23))
                .andExpect(jsonPath("$.classification").value("Overweight"));

        // Assertions verifying duplicate wasn't created but second history was linked
        assertEquals(1L, patientRepository.count());
        assertEquals(2L, bmiCalculationRepository.count());

        Patient patient = patientRepository.findByPhoneNumber("9876543210").orElseThrow();
        assertEquals("John Updated", patient.getName());
        assertEquals(31, patient.getAge());
    }

    @Test
    void testAuthorizationForProtectedEndpoints() throws Exception {
        // Access folder without cookie -> Rejects with Forbidden (or Unauth depending on Security Filter config)
        mockMvc.perform(get("/api/doctors/patients"))
                .andExpect(status().isForbidden());

        // Register doctor, extract JWT cookie, verify successful authorized access
        RegisterRequest request = RegisterRequest.builder()
                .email("doctor@sec.com")
                .password("password123")
                .name("Dr. Secure")
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        Cookie jwtCookie = result.getResponse().getCookie(JwtUtils.COOKIE_NAME);
        assertNotNull(jwtCookie);

        mockMvc.perform(get("/api/doctors/patients")
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void testGeneratePdfReportSuccessfully() throws Exception {
        // 1. Create a patient via calculate endpoint
        BmiCalculateRequest calcRequest = BmiCalculateRequest.builder()
                .name("John Doe")
                .phone("9876543210")
                .age(30)
                .gender("Male")
                .height(180.0)
                .weight(80.0)
                .unitSystem(UnitSystem.METRIC)
                .build();

        mockMvc.perform(post("/api/bmi/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(calcRequest)))
                .andExpect(status().isCreated());

        var patient = patientRepository.findByPhoneNumber("9876543210").orElseThrow();

        // 2. Register doctor, retrieve JWT cookie
        RegisterRequest registerReq = RegisterRequest.builder()
                .email("doctor@report.com")
                .password("password123")
                .name("Dr. Report")
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andReturn();

        Cookie jwtCookie = result.getResponse().getCookie(JwtUtils.COOKIE_NAME);
        assertNotNull(jwtCookie);

        // 3. Request report download (authorized)
        mockMvc.perform(get("/api/doctors/patients/" + patient.getId() + "/report")
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=\"report_John_Doe_")))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        // 4. Request report for non-existent patient UUID -> 404
        java.util.UUID randomId = java.util.UUID.randomUUID();
        mockMvc.perform(get("/api/doctors/patients/" + randomId + "/report")
                        .cookie(jwtCookie))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Patient with ID " + randomId + " not found."));
    }

    @Test
    void testPatientSoftDeleteAndRestore() throws Exception {
        // 1. Create a patient via calculate endpoint
        BmiCalculateRequest calcRequest = BmiCalculateRequest.builder()
                .name("Jane Doe")
                .phone("1112223333")
                .age(28)
                .gender("Female")
                .height(165.0)
                .weight(60.0)
                .unitSystem(UnitSystem.METRIC)
                .build();

        mockMvc.perform(post("/api/bmi/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(calcRequest)))
                .andExpect(status().isCreated());

        Patient patient = patientRepository.findByPhoneNumber("1112223333").orElseThrow();
        java.util.UUID patientId = patient.getId();

        // Register a doctor and get JWT cookie
        RegisterRequest registerReq = RegisterRequest.builder()
                .email("doctor@softdelete.com")
                .password("password123")
                .name("Dr. SoftDelete")
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andReturn();

        Cookie jwtCookie = result.getResponse().getCookie(JwtUtils.COOKIE_NAME);
        assertNotNull(jwtCookie);

        // Verify patient appears in search
        mockMvc.perform(get("/api/doctors/patients")
                        .cookie(jwtCookie)
                        .param("search", "Jane"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(patientId.toString()));

        // 2. Soft delete the patient
        mockMvc.perform(delete("/api/doctors/patients/" + patientId)
                        .cookie(jwtCookie))
                .andExpect(status().isNoContent());

        // Verify patient does NOT appear in search anymore
        mockMvc.perform(get("/api/doctors/patients")
                        .cookie(jwtCookie)
                        .param("search", "Jane"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        // Verify detail retrieval fails with 400 bad request (or appropriate error message/status)
        mockMvc.perform(get("/api/doctors/patients/" + patientId)
                        .cookie(jwtCookie))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Patient has been deleted."));

        // 3. Restore the patient
        mockMvc.perform(post("/api/doctors/patients/" + patientId + "/restore")
                        .cookie(jwtCookie))
                .andExpect(status().isNoContent());

        // Verify patient appears in search again
        mockMvc.perform(get("/api/doctors/patients")
                        .cookie(jwtCookie)
                        .param("search", "Jane"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(patientId.toString()));
    }

    @Test
    void testDuplicatePhoneNumberConcurrency() throws Exception {
        final int threadCount = 4;
        final java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(threadCount);
        final java.util.concurrent.CountDownLatch startLatch = new java.util.concurrent.CountDownLatch(1);
        final java.util.concurrent.CountDownLatch endLatch = new java.util.concurrent.CountDownLatch(threadCount);

        BmiCalculateRequest concurrentRequest = BmiCalculateRequest.builder()
                .name("Concurrent Patient")
                .phone("9999999999")
                .age(25)
                .gender("Female")
                .height(170.0)
                .weight(65.0)
                .unitSystem(UnitSystem.METRIC)
                .build();

        final String requestBody = objectMapper.writeValueAsString(concurrentRequest);
        final java.util.List<MvcResult> results = java.util.Collections.synchronizedList(new java.util.ArrayList<>());
        final java.util.List<Exception> exceptions = java.util.Collections.synchronizedList(new java.util.ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    MvcResult res = mockMvc.perform(post("/api/bmi/calculate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                            .andReturn();
                    results.add(res);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Start all threads at once
        endLatch.await();
        executor.shutdown();

        // Verify there are no exceptions in test threads
        assertEquals(0, exceptions.size(), "There should be no thread execution exceptions: " + exceptions);

        // Verify only 1 patient was created in the database
        long patientCount = patientRepository.count();
        assertEquals(1L, patientCount, "Only one patient should be created in the database under high concurrency.");

        // The calculations can be multiple (as they calculate multiple times, or fail depending on lock/concurrency configuration).
        // Let's assert that at least one of the requests succeeded.
        boolean atLeastOneSuccess = results.stream()
                .anyMatch(res -> res.getResponse().getStatus() == 201 || res.getResponse().getStatus() == 200);
        org.junit.jupiter.api.Assertions.assertTrue(atLeastOneSuccess, "At least one request must succeed.");
    }
}
