package com.dewmobile.kuaiya.game.chinesechess;

import com.dewmobile.game.chinesechess.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class DmChessStatusView extends TextView implements DmChessCallback {

	public DmChessStatusView(Context context) {
		super(context);
		init(context);
	}
	public DmChessStatusView(Context context, AttributeSet attr){
		super(context,attr);
		init(context);
	}
	public DmChessStatusView(Context context, AttributeSet attr, int style){
		super(context,attr,style);
		init(context);
	}
	private void init(Context context){
		
	}
	@Override
	public void onPieceMoved(DmChessMove move) {
		int who = DmChessState.getCurrentState().whoMoveNext();		
		if (who == DmChessPlayer.SIDE_RED) {
			this.setText("�췽��");
		} else {
			this.setText("�ڷ���");
		}
	}
	@Override
	public void onGameOver(){
		int who = DmChessState.getCurrentState().getWhoWin();
		if (who == DmChessPlayer.SIDE_RED) {
			this.setText("�췽ʤ");
		} else {
			this.setText("�ڷ�ʤ");
		}
	}
	@Override
	public void onGameStart(){
		this.setText("�췽����");
	}
	
}
