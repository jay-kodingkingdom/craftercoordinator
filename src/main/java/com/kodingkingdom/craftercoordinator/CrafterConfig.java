package com.kodingkingdom.craftercoordinator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

public enum CrafterConfig {

	PLAYERPLOT("craftercoordinator.playerplot",ConfigType.SETTING),
	HEIGHTMAX("craftercoordinator.heightmax",ConfigType.SETTING),
	HEIGHTMIN("craftercoordinator.heightmin",ConfigType.SETTING),
	LOADAMOUNT("craftercoordinator.loadamount",ConfigType.SETTING),
	LOADTIME("craftercoordinator.loadtime",ConfigType.SETTING)
	;
	
	public final String config;
	public final ConfigType configType;
	
	private CrafterConfig(String Config,ConfigType ConfigType){
		config=Config;configType=ConfigType;}
		
	public static void loadConfig(){
		CrafterCoordinatorPlugin plugin = CrafterCoordinatorPlugin.getPlugin();
		CrafterCoordinator coordinator = plugin.getCoordinator();
		FileConfiguration config = plugin.getConfig();
		
		try{
			coordinator.setHeightMaxLimit(config.getInt(HEIGHTMAX.config));
			coordinator.setHeightMinLimit(config.getInt(HEIGHTMIN.config));
			coordinator.setLoadAmountLimit(config.getInt(LOADAMOUNT.config));
			coordinator.setLoadTimeLimit(config.getInt(LOADTIME.config));

			UUID playerId;
			
			for(String regionString : config.getStringList(PLAYERPLOT.config)){
				String[] args=regionString.split("!");
				if (args.length!=9)throw new IllegalStateException();
				try {
					playerId=UUID.fromString(args[0]);}
				catch(IllegalArgumentException e){
					playerId=Bukkit.getPlayer(args[0]).getUniqueId();}			

				Location loc1 = new Location(plugin.getServer().createWorld(new WorldCreator(args[2])),
						Integer.parseInt(args[3]),
						Integer.parseInt(args[4]),
						Integer.parseInt(args[5]));
				Location loc2 = new Location(plugin.getServer().createWorld(new WorldCreator(args[2])),
						Integer.parseInt(args[6]),
						Integer.parseInt(args[7]),
						Integer.parseInt(args[8]));
				CrafterRegion region = new CrafterRegion(loc1,loc2);
				
				coordinator.addPlayerRegion(playerId,args[1], region);}		
			
			plugin.getLogger().info("Config successfully loaded");}
		
		catch(Exception e){
			plugin.getLogger().severe("Could not load config!");
			plugin.getLogger().severe("ERROR MESSAGE: "+e.getMessage());
			e.printStackTrace();
			config.set("craftercoordinator.ERROR", true);}}
			
	
	public static void saveConfig(){
		CrafterCoordinatorPlugin plugin = CrafterCoordinatorPlugin.getPlugin();
		CrafterCoordinator coordinator = plugin.getCoordinator();
		FileConfiguration config = plugin.getConfig();

		if (config.isSet("craftercoordinator.ERROR")){
			plugin.getLogger().info("Config state invalid, will not save");
			return;}
		try{
			for(String key : config.getKeys(false)){
				 config.set(key,null);}
			
			ArrayList<String> playerplots=new ArrayList<String>();
			for (Map.Entry<UUID,HashMap<String,CrafterRegion>> playerEntry : coordinator.playerRegions.entrySet()){
				for (Map.Entry<String,CrafterRegion> regionEntry : playerEntry.getValue().entrySet()){
					CrafterRegion region=regionEntry.getValue();
					playerplots.add(playerEntry.getKey().toString()+"!"+regionEntry.getKey()+"!"+region.worldName+"!"+region.MinX+"!"+region.MinY+"!"+region.MinZ+"!"+region.MaxX+"!"+region.MaxY+"!"+region.MaxZ);}}
			config.set(PLAYERPLOT.config,playerplots);

			config.set(HEIGHTMAX.config,coordinator.heightMaxLimit);
			config.set(HEIGHTMIN.config,coordinator.heightMinLimit);
			config.set(LOADAMOUNT.config,coordinator.loadAmountLimit);
			config.set(LOADTIME.config,coordinator.loadTimeLimit);

			plugin.saveConfig();
			plugin.getLogger().info("Config successfully saved");}
		catch(Exception e){
			plugin.getLogger().severe("Could not save config!");
			plugin.getLogger().severe("ERROR MESSAGE: "+e.getMessage());
			e.printStackTrace();}}

	public enum ConfigType{
		SETTING;}}
