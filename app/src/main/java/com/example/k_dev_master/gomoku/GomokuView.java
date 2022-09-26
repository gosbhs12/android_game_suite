package com.example.k_dev_master.gomoku;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.k_dev_master.R;

public class GomokuView extends View {
    //Internal Constants
    public final int numCellTypes = 2;
    private final BitmapDrawable[] bitmapCells = new BitmapDrawable[2];
    public GomokuGame game;

    //Internal variables
    private final Paint paint = new Paint();

    public int userX;
    public int userY;

    private double lowerRange_x;
    private double higherRange_x;

    private double lowerRange_y;
    private double higherRange_y;

    //Layout variables
    public int cellSize;
    private double boardWidth;
    public int width;

    //Assets
    private Bitmap background = null;

    public GomokuView(Context context) {
        super(context);
        init(null);
        game = new GomokuGame(context, this);
        setOnTouchListener(new InputListener(this));
        game.newGame();
    }

    public GomokuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(null);
        game = new GomokuGame(context, this);
        setOnTouchListener(new InputListener(this));
        game.newGame();
    }

    public GomokuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        game = new GomokuGame(context, this);
        setOnTouchListener(new InputListener(this));
        game.newGame();
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        if (game.start) {
            drawStones(canvas);
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        cellSize = (int) (24 * this.getWidth() / 800);
        boardWidth = (this.getWidth()  * 42) / 800;
        width = this.getWidth();
        createBitmapCells();
    }

    private void setDraw(Canvas canvas, Drawable draw, int startingX, int startingY, int endingX, int endingY) {
        draw.setBounds(startingX, startingY, endingX, endingY);
        draw.draw(canvas);
    }

    private void drawBackground(Canvas canvas) {
        background = BitmapFactory.decodeResource(getResources(), R.drawable.go_board);
        background = background.createScaledBitmap(background,getWidth(),getWidth(),false);
        canvas.drawBitmap(background, 0, (int)((this.getHeight()*0.65) - 0.5 * this.getWidth()), paint);
    }

    private void drawStones(Canvas canvas) {
        for (int xx = 0; xx < game.numSquaresX; xx++) {
            for (int yy = 0; yy < game.numSquaresY; yy++) {

                Stone currCell = game.board.getStone(xx, yy);
                if (currCell != null) {

                    int sX = (int) (currCell.getCoordinateX() - 0.25 * boardWidth);
                    int eX = sX + cellSize;
                    int sY = (int) (currCell.getCoordinateY() - 0.25 * boardWidth);
                    int eY = sY + cellSize;

                    if (currCell.getColor() == Stone.Color.BLACK) {
                        setDraw(canvas, bitmapCells[0], sX, sY, eX, eY);
                    } else if (currCell.getColor() == Stone.Color.WHITE) {
                        setDraw(canvas, bitmapCells[1], sX, sY, eX, eY);
                    }
                }
            }
        }
    }

    public boolean InRange(double low, double high, int num){
        if (num >= low && num <= high) {
            return true;
        }
        return false;
    }

    private void createBitmapCells() {
        Resources resources = getResources();
        int[] cellRectangleIds = getCellRectangleIds();
        for (int xx = 0; xx < bitmapCells.length; xx++) {
            Bitmap cellBitmap = Bitmap.createBitmap(cellSize, cellSize, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(cellBitmap);
            setDraw(canvas, resources.getDrawable(cellRectangleIds[xx]), 0, 0, cellSize, cellSize);
            bitmapCells[xx] = new BitmapDrawable(resources, cellBitmap);
        }
    }

    private int[] getCellRectangleIds() {
        int[] cellRectangleIds = new int[numCellTypes];
        cellRectangleIds[0] = R.drawable.black_stone;
        cellRectangleIds[1] = R.drawable.white_stone;
        return cellRectangleIds;
    }
}