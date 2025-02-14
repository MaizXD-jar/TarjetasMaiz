package org.tarjetasmaiz.model;

import java.util.UUID;

public class YellowCardHolder {
    private UUID playerUUID;
    private String playerName;
    private long timestamp;

    public YellowCardHolder(UUID playerUUID, String playerName, long timestamp) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.timestamp = timestamp;
    }

    // Getters y setters
}
