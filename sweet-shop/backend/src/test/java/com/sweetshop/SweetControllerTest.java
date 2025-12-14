package com.sweetshop;

import com.sweetshop.model.Sweet;
import com.sweetshop.repository.SweetRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SweetControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private SweetRepository sweetRepository;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllSweets() throws Exception {
        Sweet s1 = new Sweet(1L, "Choco", "Candy", 1.0, 10);
        Mockito.when(sweetRepository.findAll()).thenReturn(Arrays.asList(s1));
        mockMvc.perform(get("/api/sweets")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Choco"));
    }

    @Test
    @WithMockUser(username = "user")
    public void testPurchaseSweet() throws Exception {
        Sweet s1 = new Sweet(1L, "Choco", "Candy", 1.0, 10);
        Mockito.when(sweetRepository.findById(1L)).thenReturn(java.util.Optional.of(s1));
        mockMvc.perform(post("/api/sweets/1/purchase")).andExpect(status().isOk());
    }
}
