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
		//
		for(DmChessCallback cb : callbacks){
			cb.onGameStart();
		}
		//
		DmChessState.getCurrentState().reset(); // init
		Message m = new Message();
		m.what = DmChessMessage.MSG_REQUEST_MOVE;
		this.sendMessage(m);
	}
	private void handleEnd() {
		for(DmChessCallback cb : callbacks){
			cb.onGameOver();
		}
		return;
	}
	private void handleQuestNextMove() {			
		DmChessState.getCurrentState().requestNextMove();		
	}

	private void handleOnMove(DmChessMove move) {		
		DmChessState.getCurrentState().onPieceMoved(move);		
		for(DmChessCallback cb : callbacks){
			cb.onPieceMoved(move);
		}
		//
		if(DmChessState.getCurrentState().isGameOver()){
			Message m = new Message();
			m.what = DmChessMessage.MSG_END;
			this.sendMessage(m);
			return;
		}
		// 		
		Message m = new Message();
		m.what = DmChessMessage.MSG_REQUEST_MOVE;
		this.sendMessage(m);		
		
	}
}