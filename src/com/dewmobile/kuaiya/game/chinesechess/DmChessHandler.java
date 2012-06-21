package com.dewmobile.kuaiya.game.chinesechess;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.dewmobile.game.chinesechess.R;

public class DmChessHandler  extends Handler {
	private static DmChessHandler inst;
	private DmChessCallback callback;
	public static DmChessHandler getInstance(){
		if(inst == null){
			inst = new DmChessHandler();
		}
		return inst;
	}
	public void addChessCallback(DmChessCallback cb){
		callback = cb;
	}
	private DmChessHandler(){
		
	}
	@Override
	public void handleMessage(Message msg) {

		switch (msg.what) {
		case ChessMessage.MSG_START:
			handleStart();
			break;
		case ChessMessage.MSG_END:
			break;
		case ChessMessage.MSG_REQUEST_MOVE:
			handleQuestNextMove();
			break;
		case ChessMessage.MSG_ON_MOVED:
			handleOnMove((ChessMove)msg.obj);
		default:
			break;
		}
		// draw
	}

	private void handleStart() {
		ChessState.getCurrentState(); // init
		Message m = new Message();
		m.what = ChessMessage.MSG_REQUEST_MOVE;
		this.sendMessage(m);
	}

	private void handleQuestNextMove() {			
		ChessState.getCurrentState().requestNextMove();		
	}

	private void handleOnMove(ChessMove move) {
		ChessState.getCurrentState().onPieceMoved(move);
		if(callback != null){
			callback.onPieceMoved(move);
		}
		// 		
		Message m = new Message();
		m.what = ChessMessage.MSG_REQUEST_MOVE;
		this.sendMessage(m);		
		
	}
}