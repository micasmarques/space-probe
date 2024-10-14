package com.elo7.space_probe.app.probes;

import java.util.HashMap;
import java.util.Map;

import com.elo7.space_probe.domain.Planet;
import com.elo7.space_probe.domain.Probe;
import com.elo7.space_probe.exceptions.MovimentoForaDoLimiteException;

import org.springframework.stereotype.Service;

@Service
public class MoveProbeService {

    private final Map<Integer, String> probeDirections = new HashMap<>();

    public Probe execute(Probe probe, Planet planet, String commands, String initialDirection) {
        probeDirections.put(probe.getId(), initialDirection);

        for (char command : commands.toCharArray()) {
            switch (command) {
                case 'L' -> turnLeft(probe);
                case 'R' -> turnRight(probe);
                case 'M' -> moveForward(probe, planet);
                default -> throw new IllegalArgumentException("Comando inválido: " + command);
            }
        }
        return probe;
    }

    private void turnLeft(Probe probe) {
        String currentDirection = probeDirections.get(probe.getId());
        switch (currentDirection) {
            case "N" -> probeDirections.put(probe.getId(), "W");
            case "W" -> probeDirections.put(probe.getId(), "S");
            case "S" -> probeDirections.put(probe.getId(), "E");
            case "E" -> probeDirections.put(probe.getId(), "N");
        }
    }

    private void turnRight(Probe probe) {
        String currentDirection = probeDirections.get(probe.getId());
        switch (currentDirection) {
            case "N" -> probeDirections.put(probe.getId(), "E");
            case "E" -> probeDirections.put(probe.getId(), "S");
            case "S" -> probeDirections.put(probe.getId(), "W");
            case "W" -> probeDirections.put(probe.getId(), "N");
        }
    }

    private void moveForward(Probe probe, Planet planet) {
        String currentDirection = probeDirections.get(probe.getId());
        int x = probe.getXPosition();
        int y = probe.getYPosition();

        switch (currentDirection) {
            case "N" -> y++;
            case "E" -> x++;
            case "S" -> y--;
            case "W" -> x--;
        }

        if (x < 0 || x >= planet.getWidth() || y < 0 || y >= planet.getHeight()) {
            throw new MovimentoForaDoLimiteException("Movimento fora dos limites do planeta!");
        }

        probe.updatePosition(x, y);
    }
}
