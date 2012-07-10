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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class DmChessMainView extends SurfaceView implements
		SurfaceHolder.Callback {
	// the grid is chess board grid
	private final float gridWidth = 35.0f;
	private final float gridHeight = 35.0f;
	private final float boardMarginWidth = 10.0f; // the margin in the background bitmap
	private final float boardMarginHeight = 17.5f;
	//
	private final int stateLineHeight = 20;
	private final int margin = 10; // the margin that from the surface view boarder to background bitmap boarder
	private final int lineWidth = 3;
	private float scaleFactor = 1.0f; // maybe others, if need change
											// scale.
	//
	private DmChessPiece selectedPiece;

	private SurfaceHolder holder;
	DrawLoop loop;
	//
	DmChessRule rule = new DmChessRule();
	//
	private Map<Integer, Bitmap> pieceBitmapMap = new HashMap<Integer, Bitmap>();
	// bitmap
	private Bitmap chessBoard;
	private Bitmap chessBoardScaled;
	private Bitmap pieceBackground;
	private Bitmap pieceBackgroundScaled;
	private boolean needScale = true;
	private boolean needRotate = false; // if need rotate the chess board
	//
	boolean isRunning = true;
	//
	boolean requestMoveEnabled = false;

	public DmChessMainView(Context context) {
		super(context);
		init(context);
	}

	public DmChessMainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DmChessMainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		holder = this.getHolder();
		holder.addCallback(this);
		loop = new DrawLoop();
		// don't scaled now, because, now, the scaled factor not same.
		// and more, later, i will calculate the factor myself.
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inScaled = false;
		chessBoard = BitmapFactory.decodeResource(getResources(),
				R.drawable.qipan, opt);
		pieceBackground = BitmapFactory.decodeResource(getResources(),
				R.drawable.qizi, opt);
	}
	public void enableQuestMove(boolean r){
		requestMoveEnabled = r;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!requestMoveEnabled){
			return true;
		}
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			// x,y
			float x, y;
			x = event.getX();
			y = event.getY();
			SurfaceCoordinate tmp = new SurfaceCoordinate();
			tmp.x = x;
			tmp.y = y;
			// ChessCoordinate coord = surfaceCoordinatetoChess(tmp);
			// the flow is better
			ChessCoordinate coord = getNeareastChessCoordinate(x, y);
			if (coord == null) {
				return true;
			}

			DmChessState state = DmChessState.getCurrentState();
			// check if game over
			if (state.isGameOver()) {
				return true;
			}
			// check if here is ChessPiece
			DmChessPiece p = state.getChessPiece(coord.x, coord.y);

			if (p == null && selectedPiece == null) {
				return true;
			}else if (p == null && selectedPiece != null) {
				DmChessMove move = new DmChessMove(DmChessMove.MOVE_MOVE,
						selectedPiece, coord.x, coord.y);
				// check first
				boolean re = rule.checkChessMoveWithRule(state.getChessBoard(),
						move);
				if (re == false) { // can't move
					return true;
				}
				// move select to destination				
				Message msg = new Message();
				if(selectedPiece.pieceColor == DmChessPlayer.SIDE_BLACK){
					msg.what = DmChessMessage.MSG_ON_BLACK_MOVED;
				}else{
					msg.what = DmChessMessage.MSG_ON_RED_MOVED;
				}
				
				msg.obj = move;
				DmChessHandler.getInstance().sendMessage(msg);
				selectedPiece = null;
				return true;

			}else if (p != null && selectedPiece == null) {
				if (p.pieceColor == DmChessState.getCurrentState()
						.whoMoveNext()) {
					selectedPiece = p;
					return true;
				} else {
					return true; // can't move rival's piece;
				}
			}else if (p != null && selectedPiece != null) {
				if (p.pieceColor == DmChessState.getCurrentState()
						.whoMoveNext()) {
					selectedPiece = p;
					return true;
				} else {
					// kill rival's piece
					DmChessMove move = new DmChessMove(DmChessMove.MOVE_MOVE,
							selectedPiece, coord.x, coord.y);
					// check move first
					boolean re = rule.checkChessMoveWithRule(
							state.getChessBoard(), move);
					if (re == false) { // can't move
						return true;
					}
					// move select to destination					
					Message msg = new Message();
					if(selectedPiece.pieceColor == DmChessPlayer.SIDE_BLACK){
						msg.what = DmChessMessage.MSG_ON_BLACK_MOVED;
					}else{
						msg.what = DmChessMessage.MSG_ON_RED_MOVED;
					}
					msg.obj = move;
					DmChessHandler.getInstance().sendMessage(msg);
					selectedPiece = null;
					return true;
				}
			}

		}
		return true;
	}
	public void setRotate(boolean r){
		needRotate = r;
	}
	private void doDraw(Canvas canvas) {
		if(needScale){
		// calculate the scale factor		
				int realWidth,realHeight;
				realWidth = canvas.getWidth();
				realHeight = canvas.getHeight();
				if(realWidth / chessBoard.getWidth() > realHeight / chessBoard.getHeight()){
					scaleFactor = (float)(realHeight-2*margin) / chessBoard.getHeight();
				}else{
					scaleFactor = (float)(realWidth-2*margin) / chessBoard.getWidth();
				}
				//
				Matrix m = new Matrix();
				m.postScale(scaleFactor, scaleFactor);
				chessBoardScaled = Bitmap.createBitmap(chessBoard,
						0,0,
						chessBoard.getWidth(), chessBoard.getHeight(),
						m,
						true);
				pieceBackgroundScaled = Bitmap.createBitmap(pieceBackground,
						0,0,
						pieceBackground.getWidth(), pieceBackground.getHeight(),
						m,
						true);
				needScale = false; //
		}
		// background
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(chessBoardScaled, margin, margin, null);
		// chess piece
		DmChessState state = DmChessState.getCurrentState();
		state.lock();
		DmChessPiece[][] board = state.getChessBoard();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				DmChessPiece p = board[i][j];
				if (p != null) {
					drawPiece(canvas, p);
				}
			}
		}
		state.unlock();
		// add a
		if (selectedPiece != null) {
			drawSelectedPiece(canvas, selectedPiece);
		}
		// state
	}

	// change chess coordinate to surface coordinate
	private SurfaceCoordinate chessCoordinateToSurface(ChessCoordinate chess) {
		SurfaceCoordinate re = new SurfaceCoordinate();
		int x,y;
		if(!needRotate){
			x = chess.x;
			y = chess.y;
		}else{
			x = 8 - chess.x;
			y = 9 - chess.y;
		}
		// the margin in the chess board background, must be scaled too.
		re.x = margin + scaleFactor*boardMarginWidth + scaleFactor*gridWidth * x ;
		re.y = margin + scaleFactor*boardMarginHeight+ scaleFactor*gridHeight * (9 - y) ;
		return re;
	}

	//
	private ChessCoordinate surfaceCoordinatetoChess(SurfaceCoordinate coord) {
		ChessCoordinate re = new ChessCoordinate();
		float x, y;
		x = (coord.x - margin - gridWidth / 2) / gridWidth;
		y = 9 - (coord.y - margin - gridWidth / 2) / gridWidth;
		int ix, iy;
		ix = (int) Math.rint(x);
		iy = (int) Math.rint(y);
		if (x < 0 || x > 8) {
			return null;
		}
		if (y < 0 || y > 9) {
			return null;
		}
		re.x = ix;
		re.y = iy;
		return re;

	}

	// the follow method can be optimized. because every surface coordinate of
	// chess coordinate are fixed.
	private ChessCoordinate getNeareastChessCoordinate(float x, float y) {
		ChessCoordinate re = null;
		float distance = 100000.0f;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 10; j++) {
				ChessCoordinate coord = new ChessCoordinate();
				coord.x = i;
				coord.y = j;
				SurfaceCoordinate tmp = chessCoordinateToSurface(coord);
				float d = (float) Math.sqrt((tmp.x - x) * (tmp.x - x)
						+ (tmp.y - y) * (tmp.y - y));
				if (d < distance) {
					distance = d;
					re = coord;
				}
			}
		}
		if (distance > gridWidth)
			return null;
		return re;	
	}

	private void drawPiece(Canvas canvas, DmChessPiece piece) {
		// the center x,y of piece in the surface.
		float centerX, centerY;
		int w = pieceBackgroundScaled.getWidth();
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
		float top, left;
		left = centerX - w / 2;
		top = centerY - w / 2;
		canvas.drawBitmap(pieceBackgroundScaled, left, top, null);
		//
		Bitmap pieceBitmap = getPieceBitmap(piece.pieceColor, piece.pieceType);
		canvas.drawBitmap(pieceBitmap, centerX - pieceBitmap.getWidth() / 2,
				centerY - pieceBitmap.getHeight() / 2, null);
	}

	private void drawSelectedPiece(Canvas canvas, DmChessPiece piece) {
		float centerX, centerY;
		int w = pieceBackgroundScaled.getWidth();
		ChessCoordinate tmp = new ChessCoordinate();
		tmp.x = piece.pieceX;
		tmp.y = piece.pieceY;
		SurfaceCoordinate coord = chessCoordinateToSurface(tmp);
		centerX = coord.x;
		centerY = coord.y;
		// get the top and left
		float top, left;
		left = centerX - w / 2;
		top = centerY - w / 2;
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		canvas.drawLine(left, top, left + w, top, paint);
		canvas.drawLine(left, top, left, top + w, paint);
		canvas.drawLine(left + w, top, left + w, top + w, paint);
		canvas.drawLine(left, top + w, left + w, top + w, paint);
	}

	private Bitmap getPieceBitmap(int side, int pieceType) {
		int key = side * 100 + pieceType;
		Bitmap value = pieceBitmapMap.get(key);
		if (value == null) {
			int id = R.drawable.hongju;
			// push it into map
			if (side == DmChessPlayer.SIDE_RED) {
				if (pieceType == DmChessPiece.PIECE_JU) {
					id = R.drawable.hongju;
				} else if (pieceType == DmChessPiece.PIECE_MA) {
					id = R.drawable.hongma;
				} else if (pieceType == DmChessPiece.PIECE_XIANG) {
					id = R.drawable.hongxiang;
				} else if (pieceType == DmChessPiece.PIECE_SHI) {
					id = R.drawable.hongshi;
				} else if (pieceType == DmChessPiece.PIECE_JIANG) {
					id = R.drawable.hongjiang;
				} else if (pieceType == DmChessPiece.PIECE_PAO) {
					id = R.drawable.hongpao;
				} else if (pieceType == DmChessPiece.PIECE_ZU) {
					id = R.drawable.hongzu;
				}
			} else {
				if (pieceType == DmChessPiece.PIECE_JU) {
					id = R.drawable.heiju;
				} else if (pieceType == DmChessPiece.PIECE_MA) {
					id = R.drawable.heima;
				} else if (pieceType == DmChessPiece.PIECE_XIANG) {
					id = R.drawable.heixiang;
				} else if (pieceType == DmChessPiece.PIECE_SHI) {
					id = R.drawable.heishi;
				} else if (pieceType == DmChessPiece.PIECE_JIANG) {
					id = R.drawable.heishuai;
				} else if (pieceType == DmChessPiece.PIECE_PAO) {
					id = R.drawable.heipao;
				} else if (pieceType == DmChessPiece.PIECE_ZU) {
					id = R.drawable.heibing;
				}
			}
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inScaled = false;
			value = BitmapFactory.decodeResource(getResources(), id, opt);
			Matrix m = new Matrix();
			m.postScale(scaleFactor, scaleFactor);
			Bitmap valueScaled = Bitmap.createBitmap(value,
					0,0,
					value.getWidth(), value.getHeight(),
					m,
					true);
			pieceBitmapMap.put(key, valueScaled);
			value = valueScaled;
		}
		return value;
	}
	
	class DrawLoop extends Thread {
		public void run() {
			while (isRunning) {
				Canvas canvas = holder.lockCanvas();
				if (canvas == null) {
					continue;
				}
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

	class SurfaceCoordinate {
		public float x;
		public float y;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//
		isRunning = true;
		loop.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
	}

}