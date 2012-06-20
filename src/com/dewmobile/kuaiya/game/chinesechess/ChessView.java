package com.dewmobile.kuaiya.game.chinesechess;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.dewmobile.game.chinesechess.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ChessView extends SurfaceView implements SurfaceHolder.Callback{
	// the grid is chess board grid
	private final int gridWidth = 34;
	private final int gridHeight = 35;
	//
	private final int margin = 10;
	private final int lineWidth = 3;
	private final float scaleFactor = 1.0f; // maybe others, if need change scale.
	
	//
	private ChessPiece selectedPiece;
	
	private SurfaceHolder holder;
	DrawLoop loop ;
	private Map<Integer, Bitmap> pieceBitmapMap = new HashMap<Integer,Bitmap>();
	// bitmap
	private Bitmap board;
	private Bitmap pieceBackground;
	public ChessView(Context context) {
		super(context);
		init(context);		
	}
	public ChessView(Context context, AttributeSet attrs){
		super(context,attrs);
		init(context);
	}
	public ChessView(Context context, AttributeSet attrs, int defStyle){
		super(context,attrs,defStyle);
		init(context);
	}
	private void init(Context context){
		holder = this.getHolder();
		holder.addCallback(this);
		loop = new DrawLoop();
		//
		board = BitmapFactory.decodeResource(getResources(), R.drawable.qipan);
		pieceBackground = BitmapFactory.decodeResource(getResources(), R.drawable.qizi);
		//
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if(action == MotionEvent.ACTION_DOWN){
			// x,y
			float x, y;
			x = event.getX();
			y = event.getY();
			SurfaceCoordinate tmp = new SurfaceCoordinate();
			tmp.x = x;
			tmp.y = y;
			ChessCoordinate coord = surfaceCoordinatetoChess(tmp);
			if(coord== null){
				return true;
			}
			// check if here is ChessPiece
			ChessPiece p = ChessState.getCurrentState().getChessPiece(coord.x, coord.y);
			if(p == null && selectedPiece != null){
				// move select to destination
				ChessMove move = new ChessMove(ChessMove.MOVE_MOVE,selectedPiece, coord.x, coord.y);
				ChessState.getCurrentState().onPieceMoved(move);
				selectedPiece = null ;
			}
			if(p !=null ){
				// select this
				selectedPiece = p;
			}
			
		}
		return true;
	}
	private void doDraw(Canvas canvas){
		// background
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(board, margin, margin, null);
		// chess piece
		ChessState state = ChessState.getCurrentState();
		state.lock();
		ChessPiece[][] board = state.getChessBoard();
		for(int i=0; i<board.length;i++){
			for(int j=0;j<board[i].length;j++){
				ChessPiece p = board[i][j];
				if(p!=null){
					drawPiece(canvas,p);
				}
			}
		}
		state.unlock();
		// add a 
		if(selectedPiece!=null){
			drawSelectedPiece(canvas, selectedPiece);
		}
		// state
	}
	// change chess coordinate to surface coordinate
	private SurfaceCoordinate chessCoordinateToSurface(ChessCoordinate chess){
		SurfaceCoordinate re = new SurfaceCoordinate();
		re.x = margin + gridWidth*chess.x + gridWidth/2;
		re.y = margin + gridHeight*(9-chess.y)+ gridHeight/2;
		return re;
	}
	//
	private ChessCoordinate surfaceCoordinatetoChess(SurfaceCoordinate coord ){
		ChessCoordinate re = new ChessCoordinate();
		float x,y;
		x = (coord.x - margin - gridWidth/2)/gridWidth;
		y = 9- (coord.y - margin - gridWidth/2)/gridWidth;
		int ix,iy;
		ix = (int)Math.rint(x);
		iy = (int)Math.rint(y);
		if(x<0 || x>8){
			return null;
		}
		if(y<0 || y>9){
			return null;
		}
		re.x = ix;
		re.y = iy;
		return re;
		
	}
	// 
	private ChessPiece getChessPieceBySurfaceCoordinate(){
		return null;
	}
	private void drawPiece(Canvas canvas, ChessPiece piece){
		// the center x,y of piece in the surface.
		float centerX, centerY;
		int w = pieceBackground.getWidth();
		// TODO:
		// need to change get the symmetry coordinate if the side is black.
		// piece = symmetry piece
		// 
		ChessCoordinate tmp = new ChessCoordinate();
		tmp.x = piece.pieceX;
		tmp.y = piece.pieceY;
		SurfaceCoordinate coord = chessCoordinateToSurface(tmp);
		centerX = coord.x;
		centerY = coord.y;
		// get the top and left 
		float top,left;
		left = centerX - w/2;
		top = centerY - w/2;
		canvas.drawBitmap(pieceBackground, left, top,null);
		// 
		Bitmap pieceBitmap = getPieceBitmap(piece.pieceColor, piece.pieceType);
		canvas.drawBitmap(pieceBitmap, 
				centerX-pieceBitmap.getWidth()/2,
				centerY-pieceBitmap.getHeight()/2 ,
				null);
	}
	private void drawSelectedPiece(Canvas canvas, ChessPiece piece){
		float centerX, centerY;
		int w = pieceBackground.getWidth();
		ChessCoordinate tmp = new ChessCoordinate();
		tmp.x = piece.pieceX;
		tmp.y = piece.pieceY;
		SurfaceCoordinate coord = chessCoordinateToSurface(tmp);
		centerX = coord.x;
		centerY = coord.y;
		// get the top and left 
		float top,left;
		left = centerX - w/2;
		top = centerY - w/2;
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		canvas.drawLine(left, top, left+w, top, paint);
		canvas.drawLine(left, top, left, top+w, paint);
		canvas.drawLine(left+w, top,  left+w, top+w, paint);
		canvas.drawLine(left, top+w, left+w ,top+w,  paint);
	}
	private Bitmap getPieceBitmap(int side, int pieceType){
		int key = side*100 + pieceType ;
		Bitmap value = pieceBitmapMap.get(key);
		if(value == null){
			int id = R.drawable.hongju;
			// push it into map
			if(side == ChessPlayer.SIDE_RED){				
				if(pieceType == ChessPiece.PIECE_JU){
					id = R.drawable.hongju;
				}else if(pieceType == ChessPiece.PIECE_MA){
					id = R.drawable.hongma;
				}else if(pieceType == ChessPiece.PIECE_XIANG){
					id = R.drawable.hongxiang;
				}else if(pieceType == ChessPiece.PIECE_SHI){
					id = R.drawable.hongshi;
				}else if(pieceType == ChessPiece.PIECE_JIANG){
					id = R.drawable.hongjiang;
				}else if(pieceType == ChessPiece.PIECE_PAO){
					id = R.drawable.hongpao;
				}else if(pieceType == ChessPiece.PIECE_ZU){
					id = R.drawable.hongzu;
				}				
			}else{
				if(pieceType == ChessPiece.PIECE_JU){
					id = R.drawable.heiju;
				}else if(pieceType == ChessPiece.PIECE_MA){
					id = R.drawable.heima;
				}else if(pieceType == ChessPiece.PIECE_XIANG){
					id = R.drawable.heixiang;
				}else if(pieceType == ChessPiece.PIECE_SHI){
					id = R.drawable.heishi;
				}else if(pieceType == ChessPiece.PIECE_JIANG){
					id = R.drawable.heishuai;
				}else if(pieceType == ChessPiece.PIECE_PAO){
					id = R.drawable.heipao;
				}else if(pieceType == ChessPiece.PIECE_ZU){
					id = R.drawable.heibing;
				}
			}
			value = BitmapFactory.decodeResource(getResources(), id);
			pieceBitmapMap.put(key,value);
			
		}
		return value;
	}
	class DrawLoop extends Thread{
		public void run(){
			while(true){
				Canvas canvas = holder.lockCanvas();
				// draw
				doDraw(canvas);
				holder.unlockCanvasAndPost(canvas);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
	class ChessCoordinate {
		public int x;
		public int y;
	}
	class SurfaceCoordinate{
		public float x;
		public float y;
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		loop.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		loop.interrupt();
		
	}
	
}
