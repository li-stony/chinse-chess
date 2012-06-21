package com.dewmobile.kuaiya.game.chinesechess;

public interface DmChessCallback {
	public void onPieceMoved(DmChessMove move);
	public void onGameOver();
}
