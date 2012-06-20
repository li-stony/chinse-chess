package com.dewmobile.kuaiya.game.chinesechess;

import com.dewmobile.game.chinesechess.R;
import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

public class ChineseChessActivity extends Activity {
	private TextView text ;
	private ChessView chessView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);            
        this.setContentView(R.layout.chinese_chess);
        
        text = (TextView)findViewById(R.id.chess_state);
        chessView = (ChessView)findViewById(R.id.chess_board);
        handler = new ChessHandler();
        Message msg = new Message();
        msg.what = ChessMessage.MSG_START;
        handler.sendMessage(msg);
    }
    
   
    private Handler handler ;
    
    class ChessHandler extends Handler {
    	
   	 @Override
        public void handleMessage(Message msg) {
   		 
   		 switch (msg.what){
   		 case ChessMessage.MSG_START:
   			 ChessState.getCurrentState(); // init
   			 Message m= new Message();
   			 m.what = ChessMessage.MSG_REQUEST_RED_MOVE;
   			 handler.sendMessage(m);
   			 break;
   		 case ChessMessage.MSG_END:
   			 break;
   		 case ChessMessage.MSG_REQUEST_RED_MOVE:
   			 ChessState.getCurrentState().requestNextMove();
   			 text.setText("红方走");
   			 break;
   		 case ChessMessage.MSG_REQUEST_BLACK_MOVE:
   			 ChessState.getCurrentState().requestNextMove();
   			 text.setText("黑方走");
   			 break;
   		 case ChessMessage.MSG_ON_RED_MOVE:
   			 break;
   		case ChessMessage.MSG_ON_BLACK_MOVE:
  			 break;
   		 default:
   			 break;
   		 }
   		 // draw
   	 }
   }
}