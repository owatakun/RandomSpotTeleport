package com.github.owatakun.rstp;

public class Point {
	private String name;
	private int x, y, z;

	public Point(String name, int x, int y, int z){
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String getName(){ return name;}
	public int getX(){ return x;}
	public int getY(){ return y;}
	public int getZ(){ return z;}

	public String serialize(){
		return name + "," + x + "," + y + "," + z;
	}

	public static Point deserialize(String str){
		//変数準備
		String name = "";
		int x = 0, y = 0, z = 0;
		boolean error = false;
		//分割
		String[] data = str.split(",");
		//フォーマット適合確認
		if(data.length == 4){
			//変数へのデータの取り込み
			if (!data[0].isEmpty()) {
				name = data[0];
			} else {
				error = true;
			}
			if (Utility.tryParse(data[1])) {
				x = Integer.parseInt(data[1]);
			} else {
				error = true;
			}
			if (Utility.tryParse(data[2])) {
				y = Integer.parseInt(data[2]);
			} else {
				error = true;
			}
			if (Utility.tryParse(data[3])) {
				z = Integer.parseInt(data[3]);
			} else {
				error = true;
			}
		} else {
			error = true;
		}
		//うまく行ったらインスタンス化して返す
		if(error){
			return null;
		}else{
			return new Point(name, x, y, z);
		}
	}
}
