package com.elo7.space_probe.app.probes;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class ProbeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCriarPlaneta() throws Exception {
        mockMvc.perform(post("/v1/planets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Terra\",\"width\":5,\"height\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Terra"));
    }

    @Test
    public void testCriarSonda() throws Exception {
        mockMvc.perform(post("/v1/probes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sonda1\",\"x\":1,\"y\":2,\"planet_id\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sonda1"));
    }

    @Test
    public void testMovimentarSonda() throws Exception {
        mockMvc.perform(post("/v1/planets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Terra\",\"width\":5,\"height\":5}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/probes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sonda1\",\"x\":1,\"y\":2,\"planet_id\":1}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/probes/1/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"commands\":\"LMLMLMLMM\",\"initialDirection\":\"N\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(1))
                .andExpect(jsonPath("$.y").value(3));
    }

    @Test
    @Transactional
    public void testMovimentoForaDosLimites() throws Exception {
        mockMvc.perform(post("/v1/planets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Terra\",\"width\":5,\"height\":5}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/probes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sonda1\",\"x\":5,\"y\":5,\"planet_id\":1}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/probes/1/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"commands\":\"M\",\"initialDirection\":\"N\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Movimento fora dos limites do planeta!"));
    }

}