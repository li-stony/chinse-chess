package com.dewmobile.kuaiya.game.chinesechess;

import com.dewmobile.game.chinesechess.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.widget.TextView;

public class DmChessStatusView extends TextView implements DmChessCallback {

	AudioManager am;
	SoundPool sp;
	int moveSound = 0;
	
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
		am=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		moveSound = sp.load(context, R.raw.go,1);
	}
	@Override
	public void onPieceMoved(DmChessMove move) {
		int who = DmChessState.getCurrentState().whoMoveNext();		
		if (who == DmChessPlayer.SIDE_RED) {
			this.setText("�췽��");
		} else {
			this.setText("�ڷ���");
		}
		// play sound
		sp.play(moveSound, 1.0f, 1.0f, 1, 0, 1);
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
