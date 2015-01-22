package Hax.Bukkit.SmitePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SmiteCommandExecutor implements CommandExecutor {

	private final SmitePlugin plugin;
	 
	public SmiteCommandExecutor(SmitePlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player pSender = (Player) sender;
			if (args.length > 2) {
				try {
					if (args[0].startsWith("~")) { 
						args[0] = Integer.toString(pSender.getLocation().getBlockX() + 
								Integer.parseInt(args[0].substring(1).length() > 0 ? args[0].substring(1) : "0"));
					}
					if (args[1].startsWith("~")) { 
						args[1] = Integer.toString(pSender.getLocation().getBlockY() + 
								Integer.parseInt(args[1].substring(1).length() > 0 ? args[1].substring(1) : "0"));
					}
					if (args[2].startsWith("~")) { 
						args[2] = Integer.toString(pSender.getLocation().getBlockZ() + 
								Integer.parseInt(args[2].substring(1).length() > 0 ? args[2].substring(1) : "0"));
					}
					smiteLocation(pSender, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				} catch (NumberFormatException e) {
					pSender.sendMessage("Invalid location.");
				}
			} else if (args.length > 0) {
				List<LivingEntity> targets = new ArrayList<LivingEntity>();
				if (args[0].equals("@e")) {
					targets = getNearbyEntities(pSender);
				} else if (args[0].equals("@p")) {
					targets.add(getNearestPlayer(pSender));
				} else if (args[0].equals("@a")) {
					targets.addAll(pSender.getWorld().getPlayers());
				} else if (args[0].equals("@r")) {
					targets.add(getRandomPlayer(pSender));
				} else {
					List<Player> players = pSender.getWorld().getPlayers();
					
					for (Player p : players) {
						if (p.getName().equals(args[0])) {
							targets.add(p);
						}
					}
				}
				
				if (targets.isEmpty()) {
					targets.add((Player) sender);
				}
				
				for (LivingEntity target : targets) {
					smiteEntity(pSender, target);
				}
			} 
				
			
			return true;
		}
		return false; 
	}
	
	private List<LivingEntity> getNearbyEntities(Player sender) {
		ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
		int numEntities = 0;
		for (Entity entity : sender.getWorld().getEntities()) {
			if (sender != entity && entity instanceof LivingEntity 
					&& entity.getLocation().distance(sender.getLocation()) < 50) {
				targets.add((LivingEntity) entity);
				numEntities++;
			} 
			
			if  (numEntities >= 10) {
				break;
			}
		}
		return targets;
	}
	
	private Player getNearestPlayer(Player sender) {
		List<Player> players = sender.getWorld().getPlayers();
		
		Player closestPlayer = null;
		double closestDistance = Double.MAX_VALUE;
		
		for (Player p : players) {
			if (p != sender) {
				double newDistance = p.getLocation().distance(sender.getLocation());
				if (newDistance < closestDistance) {
					closestDistance = newDistance;
					closestPlayer = p;
				}
			}
		}
		
		return closestPlayer;
	}
	
	private Player getRandomPlayer(Player sender) {
		int numPlayers = sender.getWorld().getPlayers().size();
		int randomPlayerIndex = (int) (Math.random() * numPlayers);
		
		return sender.getWorld().getPlayers().get(randomPlayerIndex);
	}
	
	private void smiteLocation(Player sender, int x, int y, int z) {
		Location target = new Location(sender.getWorld(), x, y, z);
		Material type = target.getWorld().getBlockAt(target).getType();
		throwBlocks(type, target);
		sender.getWorld().strikeLightning(target);
		sender.getWorld().createExplosion(target, 10f, true);
		sender.sendMessage("You smote " + target.getBlockX() + " " + target.getBlockY() + " " + target.getBlockZ());
	}
	
	private void smiteEntity(Player sender, Entity target) {
		Material type = null;
		type = target.getWorld().getBlockAt(target.getLocation().getBlockX(),
				target.getLocation().getBlockY() - 1,
				target.getLocation().getBlockZ()).getType();
		throwBlocks(type, target.getLocation());
		target.getWorld().strikeLightning(target.getLocation());
		target.getWorld().createExplosion(target.getLocation(), 10f, true);
		target.setVelocity(new Vector(0, 10, 0));
		((LivingEntity)target).damage(Double.MAX_VALUE);
		sender.sendMessage("You smote " + target.getName());
	}
	
	@SuppressWarnings("deprecation")
	private void throwBlocks(Material type, Location target) {
		for (int i = 0; i < 100; i++) {
			if (type == null || type.equals(Material.AIR)) {
				type = Material.DIRT;
			}
			FallingBlock debris = target.getWorld().spawnFallingBlock(target, type, (byte) 10);
			double x = Math.random() * 2 - 1.25;//r.nextInt(21) / 10.0 - 1;
			double y = Math.random() * 2 - 1;
			double z = Math.random() * 2 - 1.25;
			debris.setVelocity(new Vector(x, y, z));
		}
	}
}
