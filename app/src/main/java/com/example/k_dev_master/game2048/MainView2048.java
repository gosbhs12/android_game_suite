package com.example.k_dev_master.game2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;


import androidx.annotation.Nullable;

import com.example.k_dev_master.R;

public class MainView2048 extends View {

    //Internal Constants
    public final int numCellTypes = 12; // 2048 = 2^11
    private final BitmapDrawable[] bitmapCells = new BitmapDrawable[12];
    public MainGame2048 game;
    //Internal variables
    private final Paint paint = new Paint();
    public int startingX;
    public int startingY;
    public int endingX;
    public int endingY;
    private int cellSize;
    private int gridWidth;
    private Bitmap background = null;

    public MainView2048(Context context) {
        super(context);
        init(null);
        game = new MainGame2048(context, this);
        setOnTouchListener(new InputListener(this));
        game.newGame();
    }
    public MainView2048(Context context, AttributeSet attrs) {
        super(context, attrs);
        game = new MainGame2048(context, this);
        setOnTouchListener(new InputListener(this));
        game.newGame();
        init(attrs);
    }

    public MainView2048(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        game = new MainGame2048(context, this);
        setOnTouchListener(new InputListener(this));
        game.newGame();
        init(attrs);
    }


    private void init(@Nullable AttributeSet set) {
    }

    private static int log2(int n) {
        if (n <= 0) throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros(n);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //Reset the transparency of the screen
        super.onDraw(canvas);
        canvas.drawBitmap(background, 0, 0, paint);
        drawScoreText(canvas);
        drawCells(canvas);
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        cellSize = 210;
        gridWidth = 10;
        int screenMiddleX = w / 2;
        int screenMiddleY = h / 2;
        int boardMiddleY = screenMiddleY + cellSize;
        startingX = screenMiddleX - (cellSize + gridWidth) * 2 - gridWidth / 2;
        endingX = screenMiddleX + (cellSize + gridWidth) * 2 + gridWidth / 2;
        startingY = boardMiddleY - (cellSize + gridWidth) * 2 - gridWidth / 2;
        endingY =  boardMiddleY + (cellSize + gridWidth) * 2 + gridWidth / 2;
        createBitmapCells();
        background = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(background);
        drawBackgroundGrid(canvas);
    }
    private void setDraw(Canvas canvas, Drawable draw, int startingX, int startingY, int endingX, int endingY) {
        draw.setBounds(startingX, startingY, endingX, endingY);
        draw.draw(canvas);
    }

    private void drawScoreText(Canvas canvas) {
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.RIGHT);

        paint.setColor(getResources().getColor(R.color.text_white));
        canvas.drawText(String.valueOf(game.score), startingX + 220 + (cellSize + gridWidth) * 3 + gridWidth * 6,200, paint);
    }


    //Renders the set of 16 background squares.
    private void drawBackgroundGrid(Canvas canvas) {
        Resources resources = getResources();
        Drawable backgroundCell = resources.getDrawable(R.drawable.tilebackground);
        // Outputting the game grid
        for (int xx = 0; xx < game.numSquaresX; xx++) {
            for (int yy = 0; yy < game.numSquaresY; yy++) {
                int sX = startingX + gridWidth + (cellSize + gridWidth) * xx;
                int eX = sX + cellSize;
                int sY = startingY + gridWidth + (cellSize + gridWidth) * yy;
                int eY = sY + cellSize;

                setDraw(canvas, backgroundCell, sX, sY, eX, eY);
            }
        }
    }

    private void drawCells(Canvas canvas) {
        for (int xx = 0; xx < game.numSquaresX; xx++) {
            for (int yy = 0; yy < game.numSquaresY; yy++) {

                int sX = startingX + gridWidth + (cellSize + gridWidth) * xx; // starting x for each cell
                int eX = sX + cellSize;
                int sY = startingY + gridWidth + (cellSize + gridWidth) * yy;
                int eY = sY + cellSize;

                Cell currCell = game.grid.getCellContent(xx, yy);
                if (currCell != null) {
                    int value = currCell.getValue();
                    int index = log2(value);
                    bitmapCells[index].setBounds(sX, sY, eX, eY);
                    bitmapCells[index].draw(canvas);
                }
            }
        }
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
        cellRectangleIds[0] = R.drawable.tilebackground;
        cellRectangleIds[1] = R.drawable.cell_tile_2;
        cellRectangleIds[2] = R.drawable.cell_tile_4;
        cellRectangleIds[3] = R.drawable.cell_tile_8;
        cellRectangleIds[4] = R.drawable.cell_tile_16;
        cellRectangleIds[5] = R.drawable.cell_tile_32;
        cellRectangleIds[6] = R.drawable.cell_tile_64;
        cellRectangleIds[7] = R.drawable.cell_tile_128;
        cellRectangleIds[8] = R.drawable.cell_tile_256;
        cellRectangleIds[9] = R.drawable.cell_tile_512;
        cellRectangleIds[10] = R.drawable.cell_tile_1024;
        cellRectangleIds[11] = R.drawable.cell_tile_2048;
        return cellRectangleIds;
    }

}