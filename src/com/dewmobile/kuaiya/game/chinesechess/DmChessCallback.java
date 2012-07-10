package com.dewmobile.kuaiya.game.chinesechess;

public interface DmChessCallback {
	public void onGameStart();
	public void onRedMove(DmChessMove move);
	public void onRequestRedMove();
	public void onBlackMove(DmChessMove move);
	public void onRequestBlackMove();
	public void onGameOver();
}
