package com.dewmobile.kuaiya.game.chinesechess;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.util.Log;
/**
 * the ChessPlayer may be either human or computer or remote player
 * @author cussyou
 *
 */
public class DmChessPlayer {
	public final static String TAG = DmChessPlayer.class.getName();
	public final static int SIDE_RED = 0;
	public final static int SIDE_BLACK = 1;
	
	public DmChessPlayer(int color){
		side = color;

	}
	
	/**
	 * if the player is a remote player, then send request move message.
	 * if the player is a computer, then send a message to computer.
	 * @param rival
	 */
	public void requestNextMove(DmChessPlayer rival){
		
	}
	
	/**
	 * called when receive the next move from player
	 * @param move
	 */
	public void onPieceMoved(DmChessMove move){
		
	}
	/**
	 * 
	 * @param move
	 * @return return ture if this player lost
	 */
	public boolean onRivalMoved(DmChessPiece[][] board, DmChessMove move){
		if( move.piece.pieceColor != this.side){
			if(board[move.destX][move.destY]!= null && 
					board[move.destX][move.destY].pieceType == DmChessPiece.PIECE_JIANG){
				return true;
			}
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
