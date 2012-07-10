package com.dewmobile.kuaiya.game.chinesechess;

import java.util.LinkedList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.dewmobile.game.chinesechess.R;

public class DmChessHandler  extends Handler {
	private static DmChessHandler inst;
	
	private List< DmChessCallback > callbacks = new LinkedList<DmChessCallback>();
	
	public static DmChessHandler getInstance(){
		if(inst == null){
			inst = new DmChessHandler();
		}
		return inst;
	}
	public void addChessCallback(DmChessCallback cb){
		callbacks.add(cb);
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
			handleEnd();
			break;
		case DmChessMessage.MSG_REQUEST_RED_MOVE:
			handleRequestRedMove();
			break;
		case DmChessMessage.MSG_ON_RED_MOVED:
			handleOnRedMove((DmChessMove)msg.obj);
			break;
		case DmChessMessage.MSG_REQUEST_BLACK_MOVE:
			handleRequestBlackMove();
			break;
		case DmChessMessage.MSG_ON_BLACK_MOVED:
			handleOnBlackMove((DmChessMove)msg.obj);
			break;
		default:
			break;
		}
		// draw
	}

	private void handleStart() {
		//
		for(DmChessCallback cb : callbacks){
			cb.onGameStart();
		}
		Message m = new Message();
		m.what = DmChessMessage.MSG_REQUEST_RED_MOVE;
		this.sendMessage(m);
	}
	private void handleEnd() {
		for(DmChessCallback cb : callbacks){
			cb.onGameOver();
		}
		return;
	}
	private void handleRequestRedMove() {	
		for(DmChessCallback cb : callbacks){
			cb.onRequestRedMove();
		}
		
	}

	private void handleOnRedMove(DmChessMove move) {			
		for(DmChessCallback cb : callbacks){
			cb.onRedMove(move);
		}
		//
		if(DmChessState.getCurrentState().isGameOver()){
			Message m = new Message();
			m.what = DmChessMessage.MSG_END;
			this.sendMessage(m);
			return;
		}
		
	}
	private void handleRequestBlackMove() {	
		for(DmChessCallback cb : callbacks){
			cb.onRequestBlackMove();
		}		
	}

	private void handleOnBlackMove(DmChessMove move) {			
		for(DmChessCallback cb : callbacks){
			cb.onBlackMove(move);
		}
		//
		if(DmChessState.getCurrentState().isGameOver()){
			Message m = new Message();
			m.what = DmChessMessage.MSG_END;
			this.sendMessage(m);
			return;
		}
	}
}