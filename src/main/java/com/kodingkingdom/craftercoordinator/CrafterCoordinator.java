
package com.kodingkingdom.craftercoordinator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;


public class CrafterCoordinator implements Listener{	
	HashMap<UUID,CrafterPlayer> players;
	HashMap<UUID,HashMap<String,CrafterRegion>> playerRegions;
	
	HashMap<String,CrafterSchool> schools;
	HashMap<String,HashMap<String,CrafterRegion>> schoolRegions;
	
	boolean isCoordinating;
	
	HashSet<CrafterRegion> plotLimit;
	int heightMaxLimit;
	int heightMinLimit;
	long loadAmountLimit;
	long loadTimeLimit;
	
	CrafterCoordinatorPlugin plugin;
	Queue<CrafterTask> taskQueue;	
	ReentrantLock simplelock = new ReentrantLock();
		
	CrafterCoordinator(CrafterCoordinatorPlugin Plugin){
		plugin=Plugin;taskQueue=new LinkedList<CrafterTask>();players= new HashMap<UUID,CrafterPlayer>();schools= new HashMap<String,CrafterSchool>();playerRegions=new HashMap<UUID,HashMap<String,CrafterRegion>>();schoolRegions=new HashMap<String,HashMap<String,CrafterRegion>>();
		loadAmountLimit=Long.MAX_VALUE;loadTimeLimit=1;heightMaxLimit=Integer.MAX_VALUE;heightMinLimit=Integer.MIN_VALUE;plotLimit = new HashSet<CrafterRegion>();
		isCoordinating=false;}	
	
	@EventHandler
	public void checkPlayerHeightLimit(BlockPlaceEvent e){
		if (checkPlayerLimit(e.getPlayer().getUniqueId()))return;
		boolean limit = !(getHeightMinLimit()<=e.getBlock().getLocation().getBlockY()&&e.getBlock().getLocation().getBlockY()<=heightMaxLimit);
		if (limit){e.setCancelled(true);
			e.getPlayer().sendMessage("You cannot build here!");}}
	@EventHandler
	public void checkPlayerHeightLimit(BlockBreakEvent e){
		if (checkPlayerLimit(e.getPlayer().getUniqueId()))return;
		boolean limit = !(getHeightMinLimit()<=e.getBlock().getLocation().getBlockY()&&e.getBlock().getLocation().getBlockY()<=heightMaxLimit);
		if (limit){e.setCancelled(true);
			e.getPlayer().sendMessage("You cannot build here!");}}
	//@EventHandler
	public void checkPlayerRegionLimit(BlockBreakEvent e){
		if (checkPlayerLimit(e.getPlayer().getUniqueId()))return;
		boolean limit = true;
		for (CrafterRegion region : getPlayerRegion(e.getPlayer().getUniqueId()).values()){
			if (region.isIn(e.getBlock().getLocation())){limit=false;return;}}
		if (limit) for (CrafterSchool school : getSchools().values()){
					if (school.getPlayers().contains(e.getPlayer().getUniqueId()))
						for (CrafterRegion region : getSchoolRegion(school.getName()).values()){
							if (region.isIn(e.getBlock().getLocation())){limit=false;return;}}}
		if (limit){e.setCancelled(true);
			e.getPlayer().sendMessage("You cannot build here!");}}
	//@EventHandler
	public void checkPlayerRegionLimit(BlockPlaceEvent e){
		if (checkPlayerLimit(e.getPlayer().getUniqueId()))return;
		boolean limit = true;
		for (CrafterRegion region : getPlayerRegion(e.getPlayer().getUniqueId()).values()){
			if (region.isIn(e.getBlock().getLocation())){limit=false;return;}}
		if (limit) for (CrafterSchool school : getSchools().values()){
			if (school.getPlayers().contains(e.getPlayer().getUniqueId()))
				for (CrafterRegion region : getSchoolRegion(school.getName()).values()){
					if (region.isIn(e.getBlock().getLocation())){limit=false;return;}}}
		if (limit){e.setCancelled(true);
			e.getPlayer().sendMessage("You cannot build here!");}}	
	
	private void flushTasks(){
		simplelock.lock();try{
			long beginTime = System.currentTimeMillis();
			CrafterTask nextTask;
			for (long loadLeft=loadAmountLimit;
					!taskQueue.isEmpty()&&loadLeft>=0;
					loadLeft-=nextTask.load){
				nextTask=taskQueue.remove();
				try{
					nextTask.task.run();}catch(Exception e){
					plugin.getLogger().log(Level.SEVERE, "Exception occured in scheduled task!");
					e.printStackTrace();}}
			long endTime = System.currentTimeMillis();
			long tickOffset = (endTime-beginTime)/50;
			if (isCoordinating) plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){public void run(){flushTasks();}}, loadTimeLimit+tickOffset);}finally{simplelock.unlock();}}

	public void start(){
		CrafterConfig.loadConfig();
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		isCoordinating=true;
		flushTasks();
		plugin.getLogger().info("Crafter coordinator started");}
	public void stop(){
		isCoordinating=false;
		CrafterConfig.saveConfig();
		plugin.getLogger().info("Crafter coordinator stopped");}

	public void addSchool(String schoolName){schools.put(schoolName, new CrafterSchool(schoolName));}
	public void addPlayer(UUID playerId){players.put(playerId, new CrafterPlayer(playerId));}
	public void removeSchool(String schoolName){schools.remove(schoolName);schoolRegions.remove(schoolName);}
	public void removePlayer(UUID playerId){players.remove(playerId);playerRegions.remove(playerId);}
	public void setLoadAmountLimit(long LoadAmountLimit){if (LoadAmountLimit<0)throw new IllegalArgumentException(); loadAmountLimit=LoadAmountLimit;}
	public void setLoadTimeLimit(long LoadTimeLimit){if (LoadTimeLimit<=0)throw new IllegalArgumentException(); loadTimeLimit=LoadTimeLimit;}
	public void setHeightMaxLimit(int HeightMaxLimit){if (HeightMaxLimit<0||HeightMaxLimit>255)throw new IllegalArgumentException(); heightMaxLimit=HeightMaxLimit;}
	public void setHeightMinLimit(int HeightMinLimit){if (HeightMinLimit<0||HeightMinLimit>255)throw new IllegalArgumentException(); heightMinLimit=HeightMinLimit;}
	public void addPlotLimit(CrafterRegion newPlotLimit){getPlotLimit().add(newPlotLimit);}
	public void removePlotLimit(CrafterRegion oldPlotLimit){getPlotLimit().remove(oldPlotLimit);}
	public void addPlayerRegion(UUID playerId, String regionName, CrafterRegion region){getPlayerRegion(playerId).put(regionName, region);}
	public void removePlayerRegion(UUID playerId, String regionName){getPlayerRegion(playerId).remove(regionName);}
	public void addSchoolRegion(String schoolName, String regionName, CrafterRegion region){getSchoolRegion(schoolName).put(regionName, region);}
	public void removeSchoolRegion(String schoolName, String regionName){getSchoolRegion(schoolName).remove(regionName);}
	
	public long getLoadAmountLimit(){return loadAmountLimit;}
	public long getLoadTimeLimit(){return loadTimeLimit;}
	public int getHeightMaxLimit(){return heightMaxLimit;}
	public int getHeightMinLimit(){return heightMinLimit;}
	public HashSet<CrafterRegion> getPlotLimit(){return plotLimit;}
	public HashMap<UUID,CrafterPlayer> getPlayers(){return players;}
	public HashMap<String,CrafterSchool> getSchools(){return schools;}

	public CrafterPlayer getPlayer(UUID playerId){
		if (!players.containsKey(playerId)) players.put(playerId, new CrafterPlayer(playerId));
		return players.get(playerId);}
	public HashMap<String,CrafterRegion> getPlayerRegion(UUID playerId){
		if (!players.containsKey(playerId)) players.put(playerId, new CrafterPlayer(playerId));
		if (!playerRegions.containsKey(playerId)) playerRegions.put(playerId, new HashMap<String,CrafterRegion>());
		return playerRegions.get(playerId);}
	public CrafterSchool getSchool(String schoolName){
		if (!schools.containsKey(schoolName)) schools.put(schoolName, new CrafterSchool(schoolName));
		return schools.get(schoolName);}
	public HashMap<String,CrafterRegion> getSchoolRegion(String schoolName){
		if (!schools.containsKey(schoolName)) schools.put(schoolName, new CrafterSchool(schoolName));
		if (!schoolRegions.containsKey(schoolName)) schoolRegions.put(schoolName, new HashMap<String,CrafterRegion>());
		return schoolRegions.get(schoolName);}

	public boolean checkPlayerLimit(UUID playerId){
		players.putIfAbsent(playerId, new CrafterPlayer(playerId));
		return Bukkit.getOfflinePlayer(playerId).getPlayer().isOp();}
	public boolean checkPlotLimit(CrafterRegion region){
		for (CrafterRegion limitRegion : plotLimit){
			if (region.isIntersecting(limitRegion)) return false;}
		return true;}
		
	public void scheduleTask(BukkitRunnable Task){
		scheduleTask(Task,1);}
	public void scheduleTask(BukkitRunnable Task, int Load){
		simplelock.lock();try{taskQueue.offer(new CrafterTask(Task, Load));}finally{simplelock.unlock();}}}
