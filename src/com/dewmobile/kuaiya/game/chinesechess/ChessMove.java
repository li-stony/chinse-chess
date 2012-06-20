package com.dewmobile.kuaiya.game.chinesechess;

public class ChessMove {
	
	public final static int MOVE_MOVE = 1;
	public final static int MOVE_THROW_PIECE = 2; // lost the game
	
	public ChessMove(int type, ChessPiece p, int x, int y){

		moveType = type;
		piece = p;
		destX = x;
		destY = y;
	}
	public int getMoveType(){
		return moveType;
	}
	public ChessPiece getChessPiece(){
		return piece;
	}
	public int getDestX(){
		return destX;
	}
	public int getDestY(){
		return destY;
	}
	private int moveType = MOVE_MOVE;	
	public ChessPiece piece;
	public int destX;
	public int destY;
}
