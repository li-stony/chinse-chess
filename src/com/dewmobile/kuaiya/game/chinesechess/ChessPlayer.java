package com.dewmobile.kuaiya.game.chinesechess;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.util.Log;
/**
 * the ChessPlayer may be either human or computer
 * @author cussyou
 *
 */
public class ChessPlayer {
	public final static String TAG = ChessPlayer.class.getName();
	public final static int SIDE_RED = 0;
	public final static int SIDE_BLACK = 1;
	
	public ChessPlayer(int color){
		side = color;

	}
	
	/**
	 * maybe asynchronized.p
	 * @param rival
	 */
	public void requestNextMove(ChessPlayer rival){
		
	}
	
	/**
	 * called when receive the next move from player
	 * @param move
	 */
	public void onPieceMoved(ChessMove move){
		
	}
	/**
	 * 
	 * @param move
	 * @return return ture if this player lost
	 */
	public boolean onRivalMoved(ChessMove move){
		if( move.piece.pieceColor != this.side){
			
		}else{
			// this MUST NOT happen
			Log.d(TAG," onRivalMoved() error");
		}
		return false;
	}
	/**
	 * 
	 * @return if this player was lost.
	 */
	public boolean isLost(){
		return isLost;
	}
		
	private int side = SIDE_RED;
	private boolean isLost = false;

}
