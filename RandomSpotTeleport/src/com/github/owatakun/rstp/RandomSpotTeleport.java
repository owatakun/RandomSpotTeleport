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
		config.reload();
		// エラーチェック
		if (!config.isSuccesfullyLoaded()) {
			getLogger().severe("Error:\nConfigのフォーマットが不適切なため、リストの取得に失敗しました\nConfigを修正後、/rstp reloadで設定を再読み込みしてください");
		}
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
		if (config.isSuccesfullyLoaded()) {
			config.save();
		}
		// 終了メッセージ
		getLogger().info("RandomSpotTeleport v" + getDescription().getVersion() + " has been disabled!");
	}
}
