package sn.sunugarage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposeLaDocumentationOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs/accueil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("SunuGarage API"))
                .andExpect(jsonPath("$.paths['/api/accueil/clients']").exists());
    }
}
