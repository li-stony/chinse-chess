package com.dewmobile.kuaiya.game.chinesechess;

/**
 * this class is for chess piece.
 * @author cussyou
 *
 */
public class DmChessPiece {

	public final static int PIECE_JU = 1;
	public final static int PIECE_MA = 2;
	public final static int PIECE_XIANG = 3;
	public final static int PIECE_SHI = 4;
	public final static int PIECE_JIANG = 5;
	public final static int PIECE_PAO = 6;
	public final static int PIECE_ZU = 7;
	/**
	 * the x,y are coordinates. 
	 * x is from the left to right of red player, starts from 0 to 8.
	 * y is from bottom to top of red player, starts from 0 to 9 .
	 * @param color
	 * @param type
	 * @param x
	 * @param y
	 */
	public DmChessPiece(int color, int type, int x, int y){
		pieceColor = color;
		pieceType = type;
		pieceX = x;
		pieceY = y;
	}
	public int pieceColor(){
		return pieceColor;
	}
	public int pieceType(){
		return pieceType;
	}
	public void moveTo(int x, int y){
		pieceX = x;
		pieceY = y;
	}
	public boolean equals(DmChessPiece obj){
		if( this == obj) return true;
		if(pieceColor != obj.pieceColor) return false;
		if(pieceType != obj.pieceType) return false;
		if(pieceX != obj.pieceX) return false;
		if(pieceY != obj.pieceY ) return false;
		return true;
	}
	public int pieceColor = DmChessPlayer.SIDE_RED;
	public int pieceType = PIECE_JU;
	public int pieceX = 0;
	public int pieceY = 0;
}
