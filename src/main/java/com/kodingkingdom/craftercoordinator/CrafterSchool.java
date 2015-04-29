package com.kodingkingdom.craftercoordinator;

import java.util.HashSet;
import java.util.UUID;

public class CrafterSchool {
	String schoolName;
	HashSet<UUID> players;
	
	CrafterSchool(String SchoolName){schoolName=SchoolName;players=new HashSet<UUID>();}
	
	public String getName(){return schoolName;}
	public HashSet<UUID> getPlayers(){return players;}}
