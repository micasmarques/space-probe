package com.elo7.space_probe.app.probes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elo7.space_probe.domain.Planet;
import com.elo7.space_probe.domain.Probe;
import com.elo7.space_probe.exceptions.MovimentoForaDoLimiteException;

import org.junit.jupiter.api.Test;

public class MoveProbeServiceTest {

    @Test
    public void testMovimentarSondaComSucesso() {
        Planet planet = new Planet("Terra", 5, 5);
        Probe probe = new Probe("Sonda1", 1, 2, planet);

        MoveProbeService service = new MoveProbeService();
        Probe movedProbe = service.execute(probe, planet, "LMLMLMLMM", "N");

        assertEquals(1, movedProbe.getXPosition());
        assertEquals(3, movedProbe.getYPosition());
    }

    @Test
    public void testMovimentoForaDosLimites() {
        Planet planet = new Planet("Terra", 5, 5);
        Probe probe = new Probe("Sonda1", 5, 5, planet);

        MoveProbeService service = new MoveProbeService();

        Exception exception = assertThrows(MovimentoForaDoLimiteException.class, () -> {
            service.execute(probe, planet, "M", "N");
        });

        String expectedMessage = "Movimento fora dos limites do planeta!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}