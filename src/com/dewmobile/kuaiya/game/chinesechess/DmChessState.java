package com.dewmobile.kuaiya.game.chinesechess;

import java.util.Vector;
import java.util.concurrent.Semaphore;

/**
 * the current state of the chess game.
 * and this class may be saved in a vector when some one want to un-move.
 * @author cussyou
 *
 */
public class DmChessState {
	public final static int CHESS_STATE_END = 0;
	public final static int CHESS_STATE_PLAYING = 1 ;
	public static DmChessState getCurrentState(){
		if(stateInst == null){
			stateInst = new DmChessState();
		}
		return stateInst;
	}
	public int whoMoveNext(){
		return moveTurn;
	}
	public DmChessPlayer getPlayer(int side){
		if(side == DmChessPlayer.SIDE_RED){
			return red;
		}else{
			return black;
		}
	}
	/**
	 * called when any side moved.
	 * may be called after player move some piece on UI, or computer returns a move;
	 * @param move
	 */
	public void onPieceMoved(DmChessMove move){
		if(move.getMoveType() == DmChessMove.MOVE_THROW_PIECE){
			// some one lost
			isChessEnd = true;
			return;
		}
		//
		boolean lost = false;
		if(moveTurn == DmChessPlayer.SIDE_RED){
			lost = black.onRivalMoved(board,move);
			if(lost) {
				isChessEnd = true;
				whoWin = DmChessPlayer.SIDE_RED;
			}
		}else{
			lost = red.onRivalMoved(board,move);
			if(lost) {
				isChessEnd = true;
				whoWin = DmChessPlayer.SIDE_BLACK;
			}
		}
		
		// change side to request next move 
		if(moveTurn == DmChessPlayer.SIDE_RED){
			moveTurn = DmChessPlayer.SIDE_BLACK;
		}else{
			moveTurn = DmChessPlayer.SIDE_RED;
		}
		// change board;
		lock();
		DmChessPiece p = new DmChessPiece(move.piece.pieceColor, move.piece.pieceType, move.destX, move.destY);
		board[p.pieceX][p.pieceY]= p;
		board[move.piece.pieceX][move.piece.pieceY] = null;
		unlock();
	}
	public void requestNextMove(){
		if(moveTurn == DmChessPlayer.SIDE_RED){
			red.requestNextMove(black);
		}else{
			black.requestNextMove(red);
		}
	}
	public DmChessPiece getChessPiece(int x,int y){
		return board[x][y];
	}
	// return is the chess board. if board[i][j] == null, then this is an empty grid.
	public DmChessPiece[][] getChessBoard(){
		return board;
	}
	public void lock(){
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void unlock(){
		mutex.release();
	}
	/**
	 * get who wins. 
	 * @return
	 */
	public int getWhoWin(){
		return whoWin;
	}
	public boolean isGameOver(){
		return isChessEnd;
	}
	private void addChessPiece(DmChessPiece p){
		board[p.pieceX][p.pieceY] = p;
	}
	
	
	//
	private int moveTurn = DmChessPlayer.SIDE_RED;
	private int whoWin = -1;
	private boolean isChessEnd = false;
	private DmChessPlayer red;
	private DmChessPlayer black;	
	private Semaphore mutex ;
	// this is a index, that, give the x,y, can get the ChessPiese in red or black;
	private DmChessPiece[][] board = new DmChessPiece[9][10];
	
	private static DmChessState stateInst;
	
	private DmChessState(){
		red = new DmChessPlayer(DmChessPlayer.SIDE_RED);
		black = new DmChessPlayer(DmChessPlayer.SIDE_BLACK);
		mutex = new Semaphore(1);
		initBoard();
	}
	private void initBoard(){
		DmChessPiece p;
		// ju
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_JU, 0, 0);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_JU, 8, 0);
		addChessPiece(p);
		// ma
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_MA, 1, 0);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_MA, 7, 0);
		addChessPiece(p);
		// pao
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_PAO, 1, 2);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_PAO, 7, 2);
		addChessPiece(p);
		// xiang
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_XIANG, 2, 0);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_XIANG, 6, 0);
		addChessPiece(p);
		// shi
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_SHI, 3, 0);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_SHI, 5, 0);
		addChessPiece(p);
		// jiang 
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_JIANG, 4, 0);
		addChessPiece(p);
		// zu
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_ZU, 0, 3);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_ZU, 2, 3);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_ZU, 4, 3);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_ZU, 6, 3);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_RED, DmChessPiece.PIECE_ZU, 8, 3);
		addChessPiece(p);
		// black side
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_JU, 0, 9);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_JU, 8, 9);
		addChessPiece(p);
		// ma
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_MA, 1, 9);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_MA, 7, 9);
		addChessPiece(p);
		// pao
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_PAO, 1, 7);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_PAO, 7, 7);
		addChessPiece(p);
		// xiang
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_XIANG, 2, 9);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_XIANG, 6, 9);
		addChessPiece(p);
		// shi
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_SHI, 3, 9);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_SHI, 5, 9);
		addChessPiece(p);
		// jiang 
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_JIANG, 4, 9);
		addChessPiece(p);
		// zu
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_ZU, 0, 6);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_ZU, 2, 6);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_ZU, 4, 6);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_ZU, 6, 6);
		addChessPiece(p);
		p = new DmChessPiece(DmChessPlayer.SIDE_BLACK, DmChessPiece.PIECE_ZU, 8, 6);
		addChessPiece(p);
	}
}
