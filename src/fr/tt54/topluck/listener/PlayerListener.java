package fr.tt54.topluck.listener;

import fr.tt54.topluck.Main;
import fr.tt54.topluck.manager.TopLuckManager;
import fr.tt54.topluck.utils.MaterialType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!TopLuckManager.knowPlayer(event.getPlayer())) {
            TopLuckManager.resetPlayer(event.getPlayer());
        }
        TopLuckManager.blockBreakedFromLastConnexion.put(event.getPlayer().getName(), new HashMap<>());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !Main.getInstance().getConfig().getBoolean("creativebypass")) {
            if (TopLuckManager.containsBlock(new MaterialType(event.getBlock().getType().getId(), event.getBlock().getData()))) {
                TopLuckManager.mine(event.getPlayer(), event.getBlock().getType().getId(), event.getBlock().getData());
            } else if (event.getBlock().getType() == Material.STONE) {
                TopLuckManager.mineStone(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        TopLuckManager.saveTopLuck();
        TopLuckManager.blockBreakedFromLastConnexion.remove(event.getPlayer().getName());
    }

}
