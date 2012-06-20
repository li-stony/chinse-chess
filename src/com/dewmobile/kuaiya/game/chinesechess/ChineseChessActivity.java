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
        
        chessView = (ChessView)findViewById(R.id.chess_board);
       
    }    
   
   
}