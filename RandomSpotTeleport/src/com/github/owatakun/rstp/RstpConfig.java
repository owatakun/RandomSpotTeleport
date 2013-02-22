package com.github.owatakun.rstp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class RstpConfig {

	private FileConfiguration config;
	private Plugin plugin;
	private boolean pointsLoaded;
	private LinkedList<Point> points;

	/**
	 * 初期化
	 */
	public RstpConfig(Plugin plugin){
		this.plugin = plugin;
		this.pointsLoaded = true;
		this.config = plugin.getConfig();
		this.points = new LinkedList<Point>();
		plugin.saveDefaultConfig(); //configファイルが存在しない場合にjar内の同名ファイルを移植
	}

	/**
	 * リストの取得状況を取得
	 */
	public boolean isSuccesfullyLoaded(){
		return pointsLoaded;
	}

	/**
	 * ポイントのリストを取得
	 */
	public List<Point> getPoints(){
		//外から変更できないようにしてから渡す
		return Collections.unmodifiableList(points);
	}

	/**
	 * ポイント追加
	 */
	public void addPoint(Point p){
		// ポイント名が既存で存在する場合置換する
		for (int i = 0; i < points.size(); i++){
			Point tempPoint = points.get(i);
			if (tempPoint.getName().equalsIgnoreCase(p.getName())) {
			points.set(i, p);
			return;
			}
		}
		// 重複していなかった場合は最後に追加
		points.add(p);
	}

	/**
	 * 指定した名前のポイントを削除
	 */
	public Point removePoint(String name){
		Iterator<Point> ite = points.iterator();
		while(ite.hasNext()){
			Point p = ite.next();
			if(p.getName().equalsIgnoreCase(name)){
				ite.remove();
				return p;
			}
		}
		return null;
	}

	/**
	 * セーブ
	 */
	public void save(){
		ArrayList<String> saveList = new ArrayList<String>();
		for (Point point: points) {
			saveList.add(point.serialize());
		}
		config.set("TPList", saveList);
		plugin.saveConfig();
	}

	/**
	 * コンフィグすべて読み込み
	 */
	public void reload(){
		// リロード
		plugin.reloadConfig();
		config = plugin.getConfig();

		// TPList読み出し
		loadPoints();
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
			// 制作失敗したらロード完了フラグをfalseにして処理を中止する
			if(pt == null){
				pointsLoaded = false;
				return;
			}else{
				points.add(pt);
			}
		}
	}
}
