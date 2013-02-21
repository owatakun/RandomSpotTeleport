package com.github.owatakun.rstp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

//#addPoint,removePointしてもファイルに反映されなくなった。　反映したいときにconfig.save

/**
 * RSTPのコンフィグ処理を一任
 * 元々Utilにあった処理を移植
 */
public class RstpConfig {

	private FileConfiguration config;
	private Plugin plugin;
	private boolean pointsLoaded;
	private LinkedList<Point> points;   //LinkedList:削除が早いがインデックスアクセスができない

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
		boolean added = false;
		// ポイント名が既存で存在するか確認
		for (Point tempPoint: points) {
			if (tempPoint.getName().equalsIgnoreCase(p.getName())) {
				tempPoint = p;
				added = true;
			}
		}
		if (!added) {
			points.add(p);
		}
	}

	/**
	 * 指定した名前のポイントを削除
	 */
	public void removePoint(String name){
		//forループとやってることはだいたい同じ
		//ite.removeを利用するためにこの形式に。
		//java イテレータで検索
		Iterator<Point> ite = points.iterator();
		while(ite.hasNext()){
			Point p = ite.next();
			if(p.getName().equals(name)){
				ite.remove();
				break;
			}
		}
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
		// リストの準備)
		points.clear();
//		// エラーリストの準備
//		ArrayList<String> errList = new ArrayList<String>();
		//るーぷ
		for(String temp: tempList) {
			//Point
			Point pt = Point.deserialize(temp);
			//制作失敗ならエラーへ
			if(pt == null){
				pointsLoaded = false;
			}else{
				points.add(pt);
			}
		}
/*		// えらー
		if(errList.size() == 0){
			pointsLoadError = null;
		}else{
			pointsLoadError = new ConfigFormatError(errList);
		}
*/
	}
}
