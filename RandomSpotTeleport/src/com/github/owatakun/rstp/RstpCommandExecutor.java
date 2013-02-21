package com.github.owatakun.rstp;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class RstpCommandExecutor implements CommandExecutor{

	private RstpConfig config;
//	private Plugin plugin;

	public RstpCommandExecutor(RstpConfig config, Plugin plugin){
		this.config = config;
//		this.plugin = plugin;
	}

	/**
	 * コマンド実行
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		// reload
		if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			config.reload();
			sender.sendMessage(Utility.msg("header") + Utility.replaceSection("&2") + "設定を再読み込みしました");
			return true;
		}
		// エラーチェック
		if (!config.isSuccesfullyLoaded()) {
			sender.sendMessage(Utility.msg("error") + Utility.replaceSection("&c") + "Configのフォーマットが不適切なため、リストの取得に失敗しています\nConfigを修正後、/rstp reloadで設定を再読み込みしてください");
			return true;
		}
		// save
		if (args.length == 1 && args[0].equalsIgnoreCase("save")) {
			config.save();
			sender.sendMessage(Utility.msg("header") + Utility.replaceSection("&2") + "設定を保存しました");
			return true;
		}
		// list
		if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
			return execList(sender, cmd, commandLabel, args);
		}
		// addコマンド
		if (args.length >= 2 && args[0].equalsIgnoreCase("add")) {
			return execAdd(sender, cmd, commandLabel, args);
		}
		// removeコマンド
		if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
			return execRemove(sender, cmd, commandLabel, args);
		}
		return false; //コマンド形式が変だったらfalseを返す
	}

	/**
	 * Listコマンド実行
	 */
	private boolean execList(CommandSender sender, Command cmd, String commandLabel, String[] args){
		List<Point> list = config.getPoints();
		// 引数から表示するページ数決定
		int page;
		if (args.length == 2 && args[1].matches("^[0-9]*")){
			// 引数が2つで2つめが数字ならそのまま代入
			page = Integer.parseInt(args[1]);
		} else if (args.length == 1){
			// 2つめが省略されていたら1ページ
			page = 1;
		} else {
			// 数字じゃない、引数が多いなどはすべて処理を抜ける
			sender.sendMessage(Utility.msg("error") + Utility.msg("cmdErr"));
			sender.sendMessage("/rstp list [page] - 設定されたポイントを表示する");
			return true;
		}
		// 表示するアイテムの最大数
		int max = page * 10;
		// Listよりも最大数が多くなったらListのsizeでやめる
		if (list.size() < max) {
			max = list.size();
		}
		sender.sendMessage(Utility.replaceSection("&3") + "設定ポイントリスト " + (page * 10 -9) + "～" + max + "件目 / " + list.size() + "件中");
		for (int i = page * 10 - 10; i < max; i++) {
			String name = list.get(i).getName();
			int x = list.get(i).getX();
			int y = list.get(i).getY();
			int z = list.get(i).getZ();
			sender.sendMessage(name + " - " + x + "," + y + "," + z);
		}
		return true;
	}

	/**
	 * Addコマンド実行
	 */
	private boolean execAdd(CommandSender sender, Command cmd, String commandLabel, String[] args){
		// hereの場合
		if (args.length == 3 && args[2].equalsIgnoreCase("here")) {
			// senderがプレイヤーでなければ処理を抜ける
			if (!(sender instanceof Player)) {
				sender.sendMessage(Utility.msg("error") + Utility.msg("senderErr"));
				return true;
			}
			// 変数の準備
			Player player = (Player) sender;
			String name = args[1];
			Location location = player.getLocation();
			int x, y, z;
			x = location.getBlockX();
			y = location.getBlockY();
			z = location.getBlockZ();
			Point point = new Point(name, x, y, z);
			config.addPoint(point);
			sender.sendMessage(Utility.replaceSection("&2") + "次のポイントを追加しました: " + Utility.replaceSection("&r") + name + " - " + x + "," + y + "," + z);
			return true;
		}
		// 座標直接指定の場合
		if (args.length == 5) {
			// String型のPoint形式に整形
			String tempPoint = args[1] + "," + args[2] + "," + args[3] + "," + args[4];
			Point point = Point.deserialize(tempPoint);
			if(point == null) {
				sender.sendMessage(Utility.msg("error") + Utility.replaceSection("&c") + "ポイント追加に失敗しました。\n/rstp add <Name> <x> <y> <z> で再度追加してください");
				return true;
			}
			config.addPoint(point);
			sender.sendMessage(Utility.replaceSection("&2") + "次のポイントを追加しました: " + Utility.replaceSection("&r") + point.getName() + " - " + point.getX() + "," + point.getY() + "," + point.getZ());
		}
		return true;
	}
	/**
	 * Removeコマンド実行
	 */
	private boolean execRemove(CommandSender sender, Command cmd, String commandLabel, String[] args){
		// 削除と、削除された場合そのポイントを表示
		Point removedPoint = config.removePoint(args[1]);
		if (removedPoint != null) {
			sender.sendMessage(Utility.replaceSection("&2") + "次のポイントを削除しました: " + Utility.replaceSection("&r") + removedPoint.getName() + " - " + removedPoint.getX() + "," + removedPoint.getY() + "," + removedPoint.getZ());
			return true;
		} else {
			sender.sendMessage(Utility.msg("error") + Utility.replaceSection("&c") + "ポイント名" + args[1] + "は存在しません");
			return true;
		}
	}
}
