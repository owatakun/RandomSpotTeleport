package com.github.owatakun.rstp;



import org.bukkit.plugin.java.JavaPlugin;




public class RandomSpotTeleport extends JavaPlugin{

	private RstpConfig config;

	/**
	 * 有効
	 */
	public void onEnable(){
		//こんふぃぐ
		config = new RstpConfig(this);
		config.reload();
		//こまんど
		getCommand("rstp").setExecutor(new RstpCommandExecutor(config,this));
		//・。・
		getLogger().info("RandomSpotTeleport v" + getDescription().getVersion() + " has been enabled!");
	}
	
	/**
	 * 無効
	 */
	public void onDisable(){
		getLogger().info("RandomSpotTeleport v" + getDescription().getVersion() + " has been disabled!");
	}
}
