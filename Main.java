import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.grinderwolf.swm.api.exceptions.WorldAlreadyExistsException;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.grinderwolf.swm.plugin.config.ConfigManager;
import com.grinderwolf.swm.plugin.config.WorldData;
import com.grinderwolf.swm.plugin.config.WorldsConfig;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        getCommand("benchmark").setExecutor(this);
        System.out.println("XD XD XD XD XD XD XD XD XD XD XD XD XD XD XD XD XD XD XD");
    }

    @Override
    public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
        if (args.length == 0){
            s.sendMessage("§cPlease use: /benchmark <prefix> <amount>");
            return true;
        }
        SWMPlugin swmPlugin = SWMPlugin.getInstance();
        try {
            WorldData wdata = new WorldData();
            wdata.setSpawn("0, 40, 0");
            wdata.setLoadOnStartup(false);
            World a = Bukkit.getWorld("pattern");
            if (a == null) {
                swmPlugin.generateWorld(swmPlugin.createEmptyWorld(SWMPlugin.getInstance().getLoader("file"), "pattern", false, wdata.toPropertyMap()));
                WorldsConfig config = ConfigManager.getWorldConfig();
                config.getWorlds().put("pattern", wdata);
                config.save();
            }
        } catch (WorldAlreadyExistsException | IOException e) {
            e.printStackTrace();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                File file = new File(getDataFolder(),"polvo.schematic");
                World a = Bukkit.getWorld("pattern");
                try {
                    Schematic schem = FaweAPI.load(file);
                    schem.paste(FaweAPI.getWorld("pattern"), BukkitUtil.toVector(a.getSpawnLocation())).flushQueue();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Integer amount = Integer.valueOf(args[1]);
                            ComplexThread complexThread = new ComplexThread("pattern");
                            complexThread.setDaemon(true);
                            for (int i = 0; i < amount; i++) {
                                String name = args[0] + i;
                                complexThread.getQueue().enqueue(name);
                            }
                            complexThread.start();
                        }
                    }.runTaskLaterAsynchronously(Main.getPlugin(Main.class),20L*3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(Main.getPlugin(Main.class),20L*3);
        return false;
    }

}
