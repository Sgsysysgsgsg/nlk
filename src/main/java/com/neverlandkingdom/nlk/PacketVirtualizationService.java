package com.neverlandkingdom.nlk;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public final class PacketVirtualizationService extends PacketListenerAbstract {
    private final NLKPlugin plugin;
    private final Map<BlockKey, String> virtualBlocks = new HashMap<>();

    public PacketVirtualizationService(NLKPlugin plugin) {
        this.plugin = plugin;
    }

    public void register(BlockKey key, String virtualBlockId) {
        if (plugin.getVirtualBlockRegistry().find(virtualBlockId) == null) {
            throw new IllegalArgumentException("Unknown virtual block: " + virtualBlockId);
        }
        virtualBlocks.put(key, virtualBlockId);
    }

    public void unregister(BlockKey key) {
        virtualBlocks.remove(key);
    }

    public int size() {
        return virtualBlocks.size();
    }

    public void registerIfAvailable() {
        Plugin packetEvents = plugin.getServer().getPluginManager().getPlugin("packetevents");
        if (packetEvents == null) {
            plugin.getLogger().info("PacketEvents not found; packet virtualization is disabled.");
            return;
        }

        PacketEvents.getAPI().getEventManager().registerListener(this);
        plugin.getLogger().info("Packet virtualization listener registered.");
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!plugin.getConfig().getBoolean("virtualization.enabled", false)) return;

        Player player = event.getPlayer();
        if (player == null) return;

        ClientProfile profile;
        try {
            profile = plugin.getClientVersionService().profile(player);
        } catch (RuntimeException ignored) {
            return;
        }

        if (!profile.newerThanServer()) return;

        if (event.getPacketType() == com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(event);
            Location location = new Location(
                    player.getWorld(),
                    packet.getBlockPosition().x(),
                    packet.getBlockPosition().y(),
                    packet.getBlockPosition().z()
            );

            String id = virtualBlocks.get(BlockKey.from(location));
            if (id != null) {
                packet.setBlockState(carrierState(id));
            }
            return;
        }

        if (event.getPacketType() == com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            WrapperPlayServerMultiBlockChange packet = new WrapperPlayServerMultiBlockChange(event);
            for (WrapperPlayServerMultiBlockChange.EncodedBlock block : packet.getBlocks()) {
                Location location = new Location(player.getWorld(), block.getX(), block.getY(), block.getZ());
                String id = virtualBlocks.get(BlockKey.from(location));
                if (id != null) {
                    block.setBlockState(carrierState(id));
                }
            }
        }
    }

    private WrappedBlockState carrierState(String id) {
        VirtualBlock block = plugin.getVirtualBlockRegistry().find(id);
        if (block == null) {
            throw new IllegalArgumentException("Unknown virtual block: " + id);
        }
        return WrappedBlockState.getByString("minecraft:" + block.carrier().name().toLowerCase());
    }

    public record BlockKey(String world, int x, int y, int z) {
        public static BlockKey from(Location location) {
            return new BlockKey(
                    location.getWorld().getName(),
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ()
            );
        }
    }
}
