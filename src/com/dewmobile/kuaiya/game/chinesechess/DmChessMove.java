package com.dewmobile.kuaiya.game.chinesechess;

public class DmChessMove {
	
	public final static int MOVE_MOVE = 1;
	public final static int MOVE_THROW_PIECE = 2; // lost the game
	
	public DmChessMove(int type, DmChessPiece p, int x, int y){

		moveType = type;
		piece = p;
		destX = x;
		destY = y;
	}
	public int getMoveType(){
		return moveType;
	}
	public DmChessPiece getChessPiece(){
		return piece;
	}
	public int getDestX(){
		return destX;
	}
	public int getDestY(){
		return destY;
	}
	private int moveType = MOVE_MOVE;	
	public DmChessPiece piece;
	public int destX;
	public int destY;
}
