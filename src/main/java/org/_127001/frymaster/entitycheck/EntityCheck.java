package org._127001.frymaster.entitycheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityCheck extends JavaPlugin implements Listener {

    public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lagcheck")) {
            Map<String, EntityCounter<EntityType,Integer>> allEntities = new HashMap<String,EntityCounter<EntityType,Integer>>();
            for (World w : getServer().getWorlds()) {
                List<Entity> entities = w.getEntities();
                for (Entity e : entities) {
                    Chunk c = e.getLocation().getChunk();
                    int x = c.getX();
                    int y = c.getZ();
                    String key = EntityCounter.key(w,x,y);
                    EntityCounter<EntityType,Integer> ec = allEntities.get(key);
                    if (ec == null) ec=new EntityCounter<EntityType,Integer>(w,x,y);
                    EntityType et = e.getType();
                    Integer count = (Integer) ec.get(et);
                    if (count == null) count = 0;
                    count++;
                    ec.put(et,count);
                }
            }
            List<EntityCounter<EntityType,Integer>> ecl = new ArrayList<EntityCounter<EntityType,Integer>>(allEntities.values());
            Collections.sort(ecl, new EntityCounter<EntityType,Integer>(null,null,null));
            getLogger().info(ecl.get(0).toString());

            return true;
        }
        return false;
    }
}

class EntityCounter<K,V> extends HashMap implements Comparator {

    private World world;

    public World getWorld() {
        return world;
    }

    private Integer x;

    public Integer getX() {
        return x;
    }
    private Integer y;

    public Integer getY() {
        return y;
    }

    private int total = 0;

    public int getTotal() {
        return total;
    }

    public EntityCounter(World world, Integer x, Integer y) {
        super();
        this.world = world;
        this.x = x;
        this.y = y;
    }

    @Override
    public Object put(Object key, Object value) {
        total++;    // This is the only thing we're adding this to, it'll go horribly wrong if other methods are used
        return super.put(key, value);
    }

    public int hashCode() {
        return EntityCounter.hashCode(this);
    }
    
    public static int hashCode(EntityCounter ec) {
        int hash = 7;
        hash = 61 * hash + (ec.world != null ? ec.world.hashCode() : 0);
        hash = 61 * hash + (ec.x != null ? ec.x.hashCode() : 0);
        hash = 61 * hash + (ec.y != null ? ec.y.hashCode() : 0);
        return hash;
    }
    
    static String key(World w, Integer x, Integer y) {
        return (y.toString() + w.getName() + x.toString());
    }
    
    static String key(EntityCounter ec) {
        return key(ec.world,ec.x,ec.y);
        
    }
    
    String key() {
        return EntityCounter.key(this);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntityCounter<?, ?> other = (EntityCounter<?, ?>) obj;
        if (this.world != other.world && (this.world == null || !this.world.equals(other.world))) {
            return false;
        }
        if (this.x != other.x && (this.x == null || !this.x.equals(other.x))) {
            return false;
        }
        if (this.y != other.y && (this.y == null || !this.y.equals(other.y))) {
            return false;
        }
        
        return true;
    }

    public int compare(Object o1, Object o2) {
        int a,b;
        a = ((EntityCounter) o1).getTotal();
        b = ((EntityCounter) o2).getTotal();
        return a-b;
        
        
    }
}
