package me.pagani;

import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.grinderwolf.swm.plugin.config.ConfigManager;
import com.grinderwolf.swm.plugin.config.WorldData;
import com.grinderwolf.swm.plugin.config.WorldsConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;

import java.io.IOException;

public class ComplexThread extends Thread {

    private ObjectArrayFIFOQueue<String> queue = new ObjectArrayFIFOQueue<>();
    private SWMPlugin swmPlugin;
    private SlimeLoader swmLoader;
    private boolean isRunning;
    private String worldPattern;
    public ComplexThread(String worldPattern){
        this.worldPattern = worldPattern;
        this.swmPlugin = SWMPlugin.getInstance();
        this.swmLoader = swmPlugin.getLoader("file");
    }

    @Override
    public void run() {
        this.isRunning = true;
        while (queue.size() > 0){
            String x = queue.dequeue();
            WorldsConfig config = ConfigManager.getWorldConfig();
            WorldData worldData = config.getWorlds().get(worldPattern);
            if (worldData == null){
                System.out.println("Data for Island "+ x + " not configured yet, can't create world.");
                return;
            }
            try {
                SlimeWorld swmWorld = swmPlugin.loadWorld(swmLoader,worldPattern,true,worldData.toPropertyMap()).clone(x,swmLoader);
                swmPlugin.generateWorld(swmWorld);
                /**
                 * WORLD CREATED HERE ;)
                 */
            } catch (WorldAlreadyExistsException e) {
                /**
                 * WORLD ALREADY EXISTSs
                 */
                try {
                    worldData = config.getWorlds().get(x);
                    if (worldData == null){
                        System.out.println("ERROR MAXIMO, ACONTECEU O QUE NAO TEM COMO ACONTECER");
                        return;
                    }
                    SlimeWorld swmWorld = swmPlugin.loadWorld(swmLoader,"",false,worldData.toPropertyMap());
                    swmPlugin.generateWorld(swmWorld);
                } catch (UnknownWorldException | IOException | NewerFormatException unknownWorldException) {
                    unknownWorldException.printStackTrace();
                } catch (CorruptedWorldException corruptedWorldException) {
                    /**
                     * CREATE NEW WORLD HERE WHY IS IT CORRUPTED?
                     */
                } catch (WorldInUseException worldInUseException) {
                    /**
                     * WORLD IN USE, ERROR :)
                     */
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CorruptedWorldException e) {
                e.printStackTrace();
            } catch (NewerFormatException e) {
                e.printStackTrace();
            } catch (WorldInUseException e) {
                e.printStackTrace();
            } catch (UnknownWorldException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Queue foi rodada.");
        this.isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public ObjectArrayFIFOQueue<String> getQueue() {
        return queue;
    }
}
