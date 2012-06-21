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
		case DmChessMessage.MSG_START:
			handleStart();
			break;
		case DmChessMessage.MSG_END:
			break;
		case DmChessMessage.MSG_REQUEST_MOVE:
			handleQuestNextMove();
			break;
		case DmChessMessage.MSG_ON_MOVED:
			handleOnMove((DmChessMove)msg.obj);
		default:
			break;
		}
		// draw
	}

	private void handleStart() {
		DmChessState.getCurrentState(); // init
		Message m = new Message();
		m.what = DmChessMessage.MSG_REQUEST_MOVE;
		this.sendMessage(m);
	}

	private void handleQuestNextMove() {			
		DmChessState.getCurrentState().requestNextMove();		
	}

	private void handleOnMove(DmChessMove move) {
		DmChessState.getCurrentState().onPieceMoved(move);
		if(callback != null){
			callback.onPieceMoved(move);
		}
		// 		
		Message m = new Message();
		m.what = DmChessMessage.MSG_REQUEST_MOVE;
		this.sendMessage(m);		
		
	}
}