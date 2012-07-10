package com.dewmobile.kuaiya.game.chinesechess;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.dewmobile.game.chinesechess.R;
import com.dewmobile.library.user.DmProfile;
import com.dewmobile.pluginsdk.DmPluginCallback;
import com.dewmobile.pluginsdk.DmPluginHelper;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChineseChessActivity extends Activity implements OnClickListener, DmChessCallback, DmPluginCallback {
	
	public final int MODE_SINGLE = 1;
	public final int MODE_BATTLE = 2;
	
	private static final String TAG = "ChineseChess";

	private Activity otherActivity;
	
	private DmChessStatusView text ;
	private DmChessMainView chessView;
	private Button freshUser;
	private LinearLayout userList ;
	//
	AudioManager am;
	SoundPool sp;
	int moveSound = 0;
	
	int battleMode = 1;
	int localColor = -1;
	DmChessPlayer red;
	DmChessPlayer black;
	String remoteImei;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);	
		Button btn = (Button)findViewById(R.id.single_mode);
		btn.setOnClickListener(this);
		btn = (Button)findViewById(R.id.battle_mode);
		btn.setOnClickListener(this);
		//
		DmPluginHelper.getInstance().bindService(this.getApplicationContext());
		DmPluginHelper.getInstance().registerCallback(this);
        //
        init(this);
        //
        DmChessHandler.getInstance().addChessCallback(this);
    }
    private void init(Context context){
		am=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		moveSound = sp.load(context, R.raw.go,1);
	}
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	DmPluginHelper.getInstance().unbindService(this.getApplicationContext());
    }
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.single_mode){
			battleMode = MODE_SINGLE;
			Message msg = new Message();
			msg.what = DmChessMessage.MSG_START;
			DmChessHandler.getInstance().sendMessage(msg);
		}
		if(v.getId() == R.id.battle_mode){
			battleMode = MODE_BATTLE;
			this.setContentView(R.layout.chess_userlist);
			freshUser = (Button)findViewById(R.id.user_list_fresh);
			userList = (LinearLayout)findViewById(R.id.user_list_container);			
			freshUser.setOnClickListener(this);
			
		}
		if(v.getId() == R.id.user_list_fresh){
			List<DmProfile> users = null;
			try {
				users = DmPluginHelper.getInstance().getRemoteUsers();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			userList.removeAllViews();
			for(DmProfile u: users){
				final DmProfile toUser = u;
				TextView t = new TextView(this);
				t.setText(u.getDisplayName());
				t.setOnClickListener(new View.OnClickListener() {					
					@Override
					public void onClick(View v) {
						localColor = DmChessPlayer.SIDE_RED;
						Message msg = new Message();
						msg.what = DmChessMessage.MSG_START;
						DmChessHandler.getInstance().sendMessage(msg);	
						//
						JSONObject json = new JSONObject();
						try {
							json.put("status", "start");
							remoteImei = toUser.getImei();
							DmPluginHelper.getInstance().sendMessage(json.toString(), toUser.getImei());
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (RemoteException e){
							e.printStackTrace();
						}
					}
				});
				userList.addView(t);
			}
		}     
   
	}
	@Override
	public void onGameStart() {
		//
		DmChessState.createFirstState();		
		
		this.setContentView(R.layout.chess_main);
		//
		chessView = (DmChessMainView) findViewById(R.id.chess_board);
		text = (DmChessStatusView) findViewById(R.id.chess_state);
		text.setText(R.string.red_move);		
		//
		if(battleMode == MODE_SINGLE){
			red = new DmLocalChessPlayer(DmChessPlayer.SIDE_RED, chessView);
			black = new DmLocalChessPlayer(DmChessPlayer.SIDE_BLACK, chessView);
		}else {
			if(localColor == DmChessPlayer.SIDE_BLACK) {
				red = new DmRemoteChessPlayer(DmChessPlayer.SIDE_RED, remoteImei);
				black = new DmLocalChessPlayer(DmChessPlayer.SIDE_BLACK, chessView);
			}else{
				red = new DmLocalChessPlayer(DmChessPlayer.SIDE_RED, chessView);
				black = new DmRemoteChessPlayer(DmChessPlayer.SIDE_BLACK, remoteImei);
			}
		}	
		if(battleMode == MODE_SINGLE){
			chessView.setRotate(false);
		}else if(black instanceof DmLocalChessPlayer ){
			chessView.setRotate(true);
		}
	}	
	@Override
	public void onGameOver() {
		int who = DmChessState.getCurrentState().getWhoWin();
		if (who == DmChessPlayer.SIDE_RED) {
			text.setText(R.string.red_win);
		} else {
			text.setText(R.string.black_win);
		}
	}
	@Override
	public void onRequestRedMove() {
		text.setText(R.string.red_move);		
		red.requestNextMove();		
	}
	@Override
	public void onRedMove(DmChessMove move) {
		DmChessState state = DmChessState.getCurrentState();		
		
		//
		red.onPieceMoved(move);
		boolean re = black.onRivalMoved(state.getChessBoard(), move);
		// change to next state
		state.onPieceMoved(move);
		// play sound
		sp.play(moveSound, 1.0f, 1.0f, 1, 0, 1);
		if(re){ // the end;
			state.setGameOver(true);
			state.setWhoWin(DmChessPlayer.SIDE_RED);
			Message msg = new Message();
			msg.what = DmChessMessage.MSG_END;
			DmChessHandler.getInstance().sendMessage(msg);
			return;
		}
		//
		Message msg = new Message();
		msg.what = DmChessMessage.MSG_REQUEST_BLACK_MOVE;
		DmChessHandler.getInstance().sendMessage(msg);
		
	}	
	@Override
	public void onRequestBlackMove() {
		text.setText(R.string.black_move);
		black.requestNextMove();
	}
	@Override
	public void onBlackMove(DmChessMove move) {
		DmChessState state = DmChessState.getCurrentState();		
		//
		black.onPieceMoved(move);
		boolean re = red.onRivalMoved(state.getChessBoard(), move);
		// change to next state
		state.onPieceMoved(move);	
		// play sound
		sp.play(moveSound, 1.0f, 1.0f, 1, 0, 1);
		
		if(re){ // the end;
			state.setGameOver(true);
			state.setWhoWin(DmChessPlayer.SIDE_BLACK);
		}
		Message msg = new Message();
		msg.what = DmChessMessage.MSG_REQUEST_RED_MOVE;
		DmChessHandler.getInstance().sendMessage(msg);
	}
	@Override
	public void onMessaged(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			String status = obj.getString("status");
			if(status.equals("start")){
				//
				battleMode = MODE_BATTLE;
				localColor = DmChessPlayer.SIDE_BLACK;
				remoteImei = obj.getString("sourceImei");
				// send msg_start
				Message msg = new Message();
				msg.what = DmChessMessage.MSG_START;
				DmChessHandler.getInstance().sendMessage(msg);	
			}else if(status.equals("move")){
				int color = obj.getInt("color");
				
				int piece_type = obj.getInt("piece_type");
				int x = obj.getInt("from_x");
				int y = obj.getInt("from_y");
				DmChessPiece piece = new DmChessPiece(color, piece_type, x, y);
				int toX = Integer.valueOf(obj.getString("to_x"));
				int toY = Integer.valueOf(obj.getString("to_y"));
				int move_type = obj.getInt("move_type");
				DmChessMove move = new DmChessMove(move_type, piece, toX, toY);
				Message msg = new Message();
				if(color == DmChessPlayer.SIDE_BLACK){
					msg.what = DmChessMessage.MSG_ON_BLACK_MOVED;
				}else{
					msg.what = DmChessMessage.MSG_ON_RED_MOVED;
				}
				msg.obj = move;
				DmChessHandler.getInstance().sendMessage(msg);
				
			}else if(status.equals("end")){
				DmChessState state = DmChessState.getCurrentState();
				state.setGameOver(true);
				int whoLost = obj.getInt("lost");
				int whoWin = (whoLost==DmChessPlayer.SIDE_BLACK)?DmChessPlayer.SIDE_RED:DmChessPlayer.SIDE_BLACK;
				state.setWhoWin(whoWin); // local win
				Message msg = new Message();
				msg.what = DmChessMessage.MSG_END;
				DmChessHandler.getInstance().sendMessage(msg);
			}else {
				Log.d(TAG,json);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}