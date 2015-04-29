package com.kodingkingdom.craftercoordinator;
import java.util.logging.Level;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class CrafterCoordinatorPlugin extends JavaPlugin implements Listener{
	CrafterCoordinator coordinator=new CrafterCoordinator(this);
	
	public CrafterCoordinator getCoordinator(){
		return coordinator;}
	
	
	@Override
    public void onEnable(){
		coordinator.start();} 
    @Override
    public void onDisable(){
    	coordinator.stop();}
    
    static CrafterCoordinatorPlugin instance=null;
    public CrafterCoordinatorPlugin(){instance=this;}
    public static CrafterCoordinatorPlugin getPlugin(){
    	return instance;}
    public static void debug(String msg){
    	instance.getLogger().log(Level.INFO//Level.FINE
    			, msg);}}