package com.kodingkingdom.craftercoordinator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class CrafterRegion{
	String worldName;
	int MaxX;
	int MaxY;
	int MaxZ;
	int MinX;
	int MinY;
	int MinZ;
	

	public int getMaxX() {
		return MaxX;}
	public int getMaxY() {
		return MaxY;}
	public int getMaxZ() {
		return MaxZ;}
	public int getMinX() {
		return MinX;}
	public int getMinY() {
		return MinY;}
	public int getMinZ() {
		return MinZ;}	
	public World getWorld() {
		return Bukkit.getWorld(worldName);}
	
	public boolean isIn(Location loc){
		if (!loc.getWorld().equals(Bukkit.getWorld(worldName)))return false;
		return (getMinX()<=loc.getBlockX() && loc.getBlockX()<=getMaxX() &&
				getMinY()<=loc.getBlockY() && loc.getBlockY()<=getMaxY() &&
				getMinZ()<=loc.getBlockZ() && loc.getBlockZ()<=getMaxZ());}
	public boolean isIntersecting(CrafterRegion region){
		if (!Bukkit.getWorld(region.worldName).equals(Bukkit.getWorld(worldName)))return false;
		return ((region.getMinX()<=getMaxX()&&getMinX()<=region.getMaxX()) 
				&& (region.getMinY()<=getMaxY()&&getMinY()<=region.getMaxY())
				&& (region.getMinZ()<=getMaxZ()&&getMinZ()<=region.getMaxZ()));}
	public CrafterRegion(Location loc1, Location loc2){
		if (!loc1.getWorld().equals(loc2.getWorld())) throw new IllegalArgumentException();
		worldName=loc1.getWorld().getName();
		MaxX=(loc1.getBlockX()>loc2.getBlockX()?loc1.getBlockX():loc2.getBlockX());
		MinX=(loc1.getBlockX()<loc2.getBlockX()?loc1.getBlockX():loc2.getBlockX());
		MaxY=(loc1.getBlockY()>loc2.getBlockY()?loc1.getBlockY():loc2.getBlockY());
		MinY=(loc1.getBlockY()<loc2.getBlockY()?loc1.getBlockY():loc2.getBlockY());
		MaxZ=(loc1.getBlockZ()>loc2.getBlockZ()?loc1.getBlockZ():loc2.getBlockZ());
		MinZ=(loc1.getBlockZ()<loc2.getBlockZ()?loc1.getBlockZ():loc2.getBlockZ());}}

