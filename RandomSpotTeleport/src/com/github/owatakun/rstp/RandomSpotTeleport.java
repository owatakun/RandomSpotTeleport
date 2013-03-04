package com.github.owatakun.rstp;



import org.bukkit.plugin.java.JavaPlugin;




public class RandomSpotTeleport extends JavaPlugin{

	private RstpConfig config;

	/**
	 * 有効
	 */
	public void onEnable(){
		// Config
		config = new RstpConfig(this);
		config.listLoad();
		// Command
		getCommand("rstp").setExecutor(new RstpCommandExecutor(config,this));
		// 起動メッセージ
		getLogger().info("RandomSpotTeleport v" + getDescription().getVersion() + " has been enabled!");
	}

	/**
	 * 無効
	 */
	public void onDisable(){
		// リストを保存
		config.saveAll();
		// 終了メッセージ
		getLogger().info("RandomSpotTeleport v" + getDescription().getVersion() + " has been disabled!");
	}
}
