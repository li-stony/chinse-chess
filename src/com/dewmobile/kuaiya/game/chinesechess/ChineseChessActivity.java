package com.dewmobile.kuaiya.game.chinesechess;

import com.dewmobile.game.chinesechess.R;
import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

public class ChineseChessActivity extends Activity {
	private static final String TAG = "ChineseChess";

	private Activity otherActivity;
	
	private DmChessStatusView text ;
	private DmChessMainView chessView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	boolean b = false;
    	if (savedInstanceState != null) {
			b = savedInstanceState.getBoolean("KEY_START_FROM_OTHER_ACTIVITY", false);
			if (b) {
				this.otherActivity.setContentView(R.layout.chinese_chess);
				chessView = (DmChessMainView)this.otherActivity.findViewById(R.id.chess_board);
				text = (DmChessStatusView) this.otherActivity.findViewById(R.id.chess_state);
			}
		}
		if (!b) {
			super.onCreate(savedInstanceState);            
	        this.setContentView(R.layout.chinese_chess);
	        chessView = (DmChessMainView)findViewById(R.id.chess_board);
	        text = (DmChessStatusView)findViewById(R.id.chess_state);
		}           

        DmChessHandler.getInstance().addChessCallback(text);
        Message msg = new Message();
        msg.what = DmChessMessage.MSG_START ;
        DmChessHandler.getInstance().sendMessage(msg);
        
    }     
    public void setActivity(Activity paramActivity) {
		Log.d(TAG, "setActivity..." + paramActivity);
		this.otherActivity = paramActivity;
	}
}