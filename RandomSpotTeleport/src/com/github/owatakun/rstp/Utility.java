package com.github.owatakun.rstp;

public class Utility{

	// 定形メッセージを呼び出すメソッド
	public static String msg(String msg) {
		if (msg.equalsIgnoreCase("header")) {
			msg = "&2[&rRandomSpotTP&2] ";
		} else if (msg.equalsIgnoreCase("error")){
			msg = "&cError: ";
		} else if (msg.equalsIgnoreCase("cmdErr")){
			msg = "&cコマンド書式が正しくありません。以下の書式を確認してください";
		} else if (msg.equalsIgnoreCase("errStop")) {
			msg = "&cエラーが発生したためコマンド実行を中止しました。\nサーバーコンソールを確認してください";
		} else if (msg.equalsIgnoreCase("senderErr")) {
			msg = "&cこのコマンドはゲーム内からのみ実行可能です";
		}
		// 最後に書式リセットをつけ、フォーマットコードを変換する
		msg = replaceSection(msg + "&r");
		return msg;
	}

	// フォーマットコードの変換(ampersand to replaceSection)
	public static String replaceSection(String temp) {
		temp = temp.replaceAll("&([0-9a-fk-r])", "\u00A7$1");
		return temp;
	}

	// 数値判定メソッド
	public static boolean tryParse(String str) {
		if (!str.isEmpty()){
		return str.matches("^-?[0-9]+$");
		}
		return false;
	}
}
