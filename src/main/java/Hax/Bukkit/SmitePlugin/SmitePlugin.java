package Hax.Bukkit.SmitePlugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class SmitePlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		this.getCommand("smite").setExecutor(new SmiteCommandExecutor(this));
	}
 
	@Override
	public void onDisable() {
		
	}
}
