package com.kodingkingdom.craftercoordinator;

import org.bukkit.scheduler.BukkitRunnable;

public class CrafterTask {
	Runnable task;
	int load;
	public CrafterTask(BukkitRunnable Task, int Load){task=Task;load=Load;}}
