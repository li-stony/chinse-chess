package com.dewmobile.kuaiya.game.chinesechess;

import android.util.Log;

public class DmChessRule {
	public final static String TAG = "ChessRule";
	/**
	 * every 4 elements is a group. 1st and 2 sd are move vector,
	 * and the 3rd and 4th are check Ma leg vector.
	 */
	public final static int[] maMoveVector = {
		2,  1,  1,  0,
		2, -1,  1,  0,
	   -2,  1, -1,  0,
	   -2, -1, -1,  0,
		1,  2,  0,  1,
		1, -2,  0, -1,
	   -1,  2,  0,  1,
	   -1, -2,  0, -1
	};
	
	public boolean checkChessMoveWithRule(DmChessPiece[][] board, DmChessMove move){
		Log.d(TAG,"checkChessMoveWithRule() color: "+move.piece.pieceColor
				+ " type: "+ move.piece.pieceType 
				+" to: "+move.destX + ","+move.destY);
		
		switch(move.piece.pieceType){
		case DmChessPiece.PIECE_JU:
			return checkJuMove(board, move);
		case DmChessPiece.PIECE_MA:
			return checkMaMove(board, move);
		case DmChessPiece.PIECE_XIANG:
			return checkXiangMove(board, move);
		case DmChessPiece.PIECE_SHI:
			return checkShiMove(board, move);
		case DmChessPiece.PIECE_JIANG:
			return checkJiangMove(board, move);
		case DmChessPiece.PIECE_PAO:
			return checkPaoMove(board, move);
		case DmChessPiece.PIECE_ZU:
			return checkZuMove(board, move);
		
		}
		return false;
	}
	// check if the dest positon in the chess board, and
	// if the dest position has a self piece.
	// and other common condition if necessary.
	private boolean checkMove(DmChessPiece[][] board,DmChessMove move){
		if(move.destX < 0 || move.destX > 8){
			return false;
		}
		if(move.destY < 0 || move.destY > 9){
			return false;
		}
		if(board[move.destX][move.destY]!= null){
			if(board[move.destX][move.destY].pieceColor == move.piece.pieceColor){
				return false;
			}
		}
		return true;
	}
	private boolean checkJuMove(DmChessPiece[][] board, DmChessMove move){
		boolean re = checkMove(board, move);
		if(re == false) return false;
		int tmpx = move.destX - move.piece.pieceX;
		int tmpy = move.destY - move.piece.pieceY;
		if(tmpx*tmpy != 0 ) return false;
		if(move.destX == move.piece.pieceX){
			int start = move.destY>move.piece.pieceY ? move.piece.pieceY : move.destY;
			int end = move.destY<move.piece.pieceY ? move.piece.pieceY : move.destY;
			for(int i = start+1; i<end; i++){
				if(board[move.destX][i] != null){
					return false;
				}
			}
		}else{
			int start = move.destX>move.piece.pieceX ? move.piece.pieceX : move.destX;
			int end = move.destX<move.piece.pieceX ? move.piece.pieceX : move.destX;
			for(int i = start+1; i<end; i++){
				if(board[i][move.destY] != null){
					return false;
				}
			}
		}
		// run here means , has no piece on the road
		
		return true;
	}
	private boolean checkMaMove(DmChessPiece[][] board, DmChessMove move){
		boolean re = checkMove(board, move);
		if(re == false){
			return false;
		}
		for(int i=0;i<maMoveVector.length/4;i++){
			int ind = i*4;
			int tmpx,tmpy;
			tmpx = move.piece.pieceX + maMoveVector[ind];
			tmpy = move.piece.pieceY + maMoveVector[ind+1];
			if( tmpx== move.destX && tmpy== move.destY){
				// check ma leg
				int legx,legy;
				legx = move.piece.pieceX + maMoveVector[ind+2];
				legy = move.piece.pieceY + maMoveVector[ind+3];
				if(board[legx][legy] != null){ // check next position
					continue;
				}else{
					return true;
				}
			}			
		}
		return false;
	}
	private boolean checkXiangMove(DmChessPiece[][] board, DmChessMove move){
		DmChessPiece p = move.piece;
		// check if the move is in the rival's land.
		if(move.piece.pieceColor==DmChessPlayer.SIDE_RED){
			if(move.destY > 4){
				return false;
			}
		}else{
			if(move.destY < 5){
				return false;
			}
		}
		if(Math.abs(move.destX-p.pieceX) == 2 &&
				Math.abs(move.destY-p.pieceY)==2){
			// check block
			int tmpx,tmpy;
			tmpx = (move.destX + p.pieceX) /2;
			tmpy = (move.destY + p.pieceY) /2;
			if( board[tmpx][tmpy] != null){ // block
				return false; 
			}
		}else{
			return false;
		}
		return true;
	}
	
	private boolean checkShiMove(DmChessPiece[][] board, DmChessMove move){
		if(move.piece.pieceColor == DmChessPlayer.SIDE_RED){
			if( move.piece.pieceX == 4 &&
					move.piece.pieceY == 1){
				// can move to abs(x-x1) == 1 , and abs(y-y1) == 1;
				int tmpx = Math.abs(move.destX - move.piece.pieceX);
				int tmpy = Math.abs(move.destY - move.piece.pieceY);
				if(tmpx ==1 && tmpy == 1){
					return true;
				}else{
					return false;
				}
			}else{
				// next only (4,1);
				if(move.destX == 4 && move.destY == 1){
					return true;
				}else{
					return false;
				}
			}
		}else{ // black
			if( move.piece.pieceX == 4 &&
					move.piece.pieceY == 8){
				// can move to abs(x-x1) == 1 , and abs(y-y1) == 1;
				int tmpx = Math.abs(move.destX - move.piece.pieceX);
				int tmpy = Math.abs(move.destY - move.piece.pieceY);
				if(tmpx ==1 && tmpy == 1){
					return true;
				}else{
					return false;
				}
			}else{
				// next only (4,1);
				if(move.destX == 4 && move.destY == 8){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	private boolean checkJiangMove(DmChessPiece[][] board, DmChessMove move){
		if(move.piece.pieceColor == DmChessPlayer.SIDE_RED){
			if(move.destX < 3 || move.destX > 5) return false;
			if(move.destY < 0 || move.destY > 2) return false;
			
		}else{
			if(move.destX < 3 || move.destX > 5) return false;
			if(move.destY < 7 || move.destY > 9) return false;
		}
		int tmpx = Math.abs(move.destX - move.piece.pieceX);
		int tmpy = Math.abs(move.destY - move.piece.pieceY);
		if(tmpx+tmpy == 1){
			return true;
		}
		return false;
	}
	private boolean checkPaoMove(DmChessPiece[][] board, DmChessMove move){
		boolean re = checkMove(board, move);
		if(re == false) return false;
		int tmpx = move.destX - move.piece.pieceX;
		int tmpy = move.destY - move.piece.pieceY;
		if(tmpx*tmpy != 0 ) return false;
		
		int cnt = 0;
		if(move.destX == move.piece.pieceX){
			int start = move.destY>move.piece.pieceY ? move.piece.pieceY : move.destY;
			int end = move.destY<move.piece.pieceY ? move.piece.pieceY : move.destY;

			for(int i = start+1; i<end; i++){
				if(board[move.destX][i] != null){
					cnt ++;
				}
			}
		}else{
			int start = move.destX>move.piece.pieceX ? move.piece.pieceX : move.destX;
			int end = move.destX<move.piece.pieceX ? move.piece.pieceX : move.destX;

			for(int i = start+1; i<end; i++){
				if(board[i][move.destY] != null){
					cnt++;
				}
			}			
		}
		// the counter of pieces on the road has calculated
		if(cnt > 1){ // too many piece on the road
			return false;
		}else if(cnt == 1){ // kill it if dest is rival's or can't move
			if(board[move.destX][move.destY] != null ){
				return true;
			}else{
				return false;
			}
		}else { // 
			if(board[move.destX][move.destY] == null){
				return true; // the dest is empty;
			}else{ // the dest has another piece 
				return false;
			}
		}
	}
	private boolean checkZuMove(DmChessPiece[][] board, DmChessMove move){
		boolean re = checkMove(board, move);
		if(re==false){
			return false;
		}
		// check if moved one grid
		int tmpx,tmpy;
		tmpx = Math.abs(move.destX - move.piece.pieceX);
		tmpy = Math.abs(move.destY - move.piece.pieceY);
		if(tmpx*tmpy !=0 || tmpx+tmpy != 1){
			return false;
		}
		// check the moved direction
		if(move.piece.pieceColor == DmChessPlayer.SIDE_RED){
			if(move.piece.pieceY <= 4){ // in self land, must forward
				if(move.destY - move.piece.pieceY != 1){ // can't move left or right, must forward.
					return false; 
				}else {
					return true;
				}
			}else{ // in the rival's land, in the same words, over the river
				if(move.destY < move.piece.pieceY){ // can't backward
					return false;
				}else{
					return true;
				}
			}
		}else{
			if(move.piece.pieceY >= 5){ // must forward
				if(move.destY -move.piece.pieceY != -1){
					return false;
				}else{
					return true;
				}
			}else{
				if(move.destY > move.piece.pieceY){
					return false;
				}else{
					return true;
				}
			}
		}
	}
	
}
