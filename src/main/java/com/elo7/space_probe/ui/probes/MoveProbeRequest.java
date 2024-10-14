package com.elo7.space_probe.ui.probes;

public class MoveProbeRequest {
    private String commands;
    private String initialDirection;

    public String getCommands() {
        return commands;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }

    public String getInitialDirection() {
        return initialDirection;
    }

    public void setInitialDirection(String initialDirection) {
        this.initialDirection = initialDirection;
    }
}