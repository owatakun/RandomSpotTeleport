package com.github.owatakun.rstp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class RstpConfig {

	private FileConfiguration config;
	private Plugin plugin;
	private boolean pointsLoaded;
	private LinkedList<Point> points;
	private LinkedHashMap<String, RstpConfig> lists;
	private File listFile;

	/**
	 * 初期化
	 */
	public RstpConfig(Plugin plugin){
		this.plugin = plugin;
		this.lists = new LinkedHashMap<String, RstpConfig>();
		plugin.saveDefaultConfig(); //configファイルが存在しない場合にjar内の同名ファイルを移植
	}
	public RstpConfig(Plugin plugin, String listName) throws FileNotFoundException {
		this.plugin = plugin;
		this.pointsLoaded = true;
		this.listFile = new File(plugin.getDataFolder() + File.separator + "List_" + listName + ".yml");
		if (!listFile.exists()) {
			this.pointsLoaded = false;
			throw new FileNotFoundException("ファイル \"" + listName + ".yml\" が存在しませんでした");
		}
		this.config = YamlConfiguration.loadConfiguration(listFile);
		this.points = new LinkedList<Point>();
	}

	/**
	 * リストロード
	 */
	public void listLoad() {
		File[] dirFiles;
		// プラグインデータフォルダのファイル一覧を取得
		dirFiles = plugin.getDataFolder().listFiles();
		lists.clear();
		if (dirFiles != null) {
			for (File tempFile: dirFiles) {
				// ファイル名がリストファイル形式であれば読み込み開始
				if (tempFile.getName().matches("^List_.*\\.yml$")) {
					String listName = tempFile.getName().replaceAll("^List_(.*)\\.yml$", "$1");
					try {
						// 初期化
						RstpConfig listConfig = new RstpConfig(plugin, listName);
						// LinkedHashMapに追加
						lists.put(listName, listConfig);
						// リスト読み出し
						lists.get(listName).loadPoints();
						// エラーチェック
						if (!isSuccesfullyLoaded(listName)) {
							plugin.getLogger().severe("Error:\n\"" + listName + ".yml\"のフォーマットが不適切なため、このリストの取得をスキップしました\n修正後、/rstp reloadで設定を再読み込みしてください");
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * リスト存在チェック
	 */
	public boolean listExists(String listName) {
		return lists.containsKey(listName);
	}

	/**
	 * リスト一覧を取得
	 */
	public String[] getLists() {
		if (lists.size() != 0) {
			String[] list = new String[lists.size()];
			int i = 0;
			for (Entry<String, RstpConfig> tempList: lists.entrySet()) {
				String listName = tempList.getKey();
				list[i] = listName;
				i++;
			}
			return list;
		} else {
			return null;
		}
	}

	/**
	 * リストの取得状況を取得
	 */
	public boolean isSuccesfullyLoaded(String listName) {
		return lists.get(listName).pointsLoaded;
	}

	/**
	 * ポイントのリストを取得
	 */
	public List<Point> getPoints(String listName) {
		//外から変更できないようにしてから渡す
		return Collections.unmodifiableList(lists.get(listName).points);
	}

	/**
	 * ファイル作成
	 */
	protected boolean createList(String listName) {
		if (!lists.containsKey(listName)) {
			File createFile = new File(plugin.getDataFolder() + File.separator + "List_" + listName + ".yml");
			try {
				createFile.createNewFile();
				listLoad();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * ファイル削除
	 */
	protected boolean deleteList(String listName) {
		RstpConfig list = lists.get(listName);
		if (list.listFile.exists()) {
			list.listFile.delete();
			listLoad();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ポイント追加
	 */
	public void addPoint(String listName, Point p) {
		points = lists.get(listName).points;
		// ポイント名が既存で存在する場合置換する
		for (int i = 0; i < points.size(); i++){
			Point tempPoint = points.get(i);
			if (tempPoint.getName().equalsIgnoreCase(p.getName())) {
			points.set(i, p);
			save(listName);
			return;
			}
		}
		// 重複していなかった場合は最後に追加
		points.add(p);
		save(listName);
	}

	/**
	 * 指定した名前のポイントを削除
	 */
	public Point removePoint(String listName, String name){
		points = lists.get(listName).points;
		Iterator<Point> ite = points.iterator();
		while(ite.hasNext()){
			Point p = ite.next();
			if(p.getName().equalsIgnoreCase(name)){
				ite.remove();
				save(listName);
				return p;
			}
		}
		return null;
	}

	/**
	 * セーブ
	 */
	public void save(String listName) {
		RstpConfig list = lists.get(listName);
		ArrayList<String> saveList = new ArrayList<String>();
		for (Point point: list.points) {
			saveList.add(point.serialize());
		}
		list.config.set("TPList", saveList);
		try {
			list.config.save(list.listFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void saveAll() {
		String[] getLists = getLists();
		if (getLists != null) {
			for (String listName: getLists()) {
				if (lists.get(listName).pointsLoaded) {
					save(listName);
				} else {
					plugin.getLogger().severe("Error: \"" + listName + "\" のセーブに失敗しました");
				}
			}
		}
	}

	/**
	 * コンフィグすべて読み込み
	 */
	public void reload(){
		// リロード
		plugin.reloadConfig();
		config = plugin.getConfig();
		// TPList読み出し
		listLoad();
	}

	/**
	 * 位置リストロード
	 */
	private void loadPoints(){
		List<String> tempList = config.getStringList("TPList");
		// リストの準備
		points.clear();
		// ループ
		for(String temp: tempList) {
			// Point
			Point pt = Point.deserialize(temp);
			// リストに加える
			if(pt != null){
				points.add(pt);
			} else {
				// 制作失敗していたらロード完了フラグをfalseにして処理を中止する
				pointsLoaded = false;
				return;
			}
		}
	}
}
