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
			this.setText("红方走");
		} else {
			this.setText("黑方走");
		}
	}

}
