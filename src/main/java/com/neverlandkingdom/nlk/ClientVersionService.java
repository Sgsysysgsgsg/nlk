package com.neverlandkingdom.nlk;

import com.viaversion.viaversion.api.Via;
import org.bukkit.entity.Player;

public final class ClientVersionService {
    /**
     * Java protocol used by the 1.21.11 backend.
     * Kept in config so NLK can be updated without changing code.
     */
    private final int serverProtocol;

    public ClientVersionService(int serverProtocol) {
        this.serverProtocol = serverProtocol;
    }

    public ClientProfile profile(Player player) {
        int protocol = Via.getAPI().getPlayerVersion(player.getUniqueId());
        return new ClientProfile(
                player.getName(),
                protocol,
                protocol > serverProtocol
        );
    }

    public int serverProtocol() {
        return serverProtocol;
    }
}
