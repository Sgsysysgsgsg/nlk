package com.neverlandkingdom.nlk;

import com.viaversion.viaversion.api.Via;
import org.bukkit.entity.Player;

public final class ClientVersionService {
    private static final int SERVER_PROTOCOL_1_21_11 = 774;

    public ClientProfile profile(Player player) {
        int protocol = Via.getAPI().getPlayerVersion(player.getUniqueId());
        return new ClientProfile(
                player.getName(),
                protocol,
                protocol > SERVER_PROTOCOL_1_21_11
        );
    }
}
