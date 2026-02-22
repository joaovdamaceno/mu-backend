package br.unioeste.mu.mu_backend.registration;

import br.unioeste.mu.mu_backend.shared.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegistrationController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationRepository registrationRepository;

    @Test
    void shouldCreateRegistrationAndNormalizeFields() throws Exception {
        Registration saved = new Registration();
        saved.setName("Maria");
        saved.setEmail("maria@example.com");
        saved.setCampus("Campus Cascavel");
        saved.setCourse("Computação");
        saved.setSemester("5º");
        saved.setHowDidYouHear("Instagram");

        when(registrationRepository.save(any(Registration.class))).thenReturn(saved);

        String payload = """
                {
                  "name": "  Maria ",
                  "email": " maria@example.com ",
                  "campus": " Campus Cascavel ",
                  "course": " Computação ",
                  "semester": " 5º ",
                  "howDidYouHear": " Instagram ",
                  "whatsapp": "   ",
                  "institution": "   "
                }
                """;

        mockMvc.perform(post("/api/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Maria"))
                .andExpect(jsonPath("$.email").value("maria@example.com"));

        verify(registrationRepository).save(any(Registration.class));
    }

    @Test
    void shouldReturnValidationDetailsForInvalidRegistrationPayload() throws Exception {
        String payload = """
                {
                  "name": "",
                  "email": "invalido",
                  "campus": "",
                  "course": "",
                  "semester": "",
                  "howDidYouHear": ""
                }
                """;

        mockMvc.perform(post("/api/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[?(@.field=='name')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='email')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='campus')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='course')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='semester')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='howDidYouHear')]").isNotEmpty());

        verifyNoInteractions(registrationRepository);
    }
}
