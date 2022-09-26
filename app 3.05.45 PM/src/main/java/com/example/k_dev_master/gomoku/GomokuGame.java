package com.example.k_dev_master.gomoku;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.example.k_dev_master.MainActivity;
import com.example.k_dev_master.R;
import com.example.k_dev_master.game2048.LogicGame2048;
import com.example.k_dev_master.memorygame.MemoryGame;


public class GomokuGame extends AppCompatActivity {
    private Context mContext = null;
    private GomokuView mView = null;
    public Board board = null;
    public final int numSquaresX = 19;
    public final int numSquaresY = 19;
    public int turn = 0;
    //public boolean winning = false;
    public boolean start = false;

    public int stone_x;
    public int stone_y;

    public int board_x;
    public int board_y;

    public GomokuGame() {
    }

    public GomokuGame(Context context, GomokuView view) {
        mContext = context;
        mView = view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        //mView = new GomokuView(this);
        setContentView(R.layout.activity_main_gomoku);

        // popup menu button implementation
        ImageButton openMenu = findViewById(R.id.ListViewBtnGomoku);
        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(GomokuGame.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.pop:
                                startActivity(new Intent(getApplicationContext(), GomokuGame.class));
                                break;
                            case R.id.pop_inst:
                                LayoutInflater inflater = (LayoutInflater)
                                        getSystemService(LAYOUT_INFLATER_SERVICE);
                                View popupView = inflater.inflate(R.layout.popup_instruction_gomoku, null);
                                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                boolean focusable = true; // lets taps outside the popup also dismiss it
                                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                                // show the popup window
                                // which view you pass in doesn't matter, it is only used for the window tolken
                                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                                // dismiss the popup window when touched
                                popupView.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        popupWindow.dismiss();
                                        return true;
                                    }
                                });
                                break;
                            case R.id.pop_exit:
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                                break;
                            default:
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void gameWon() {
        String winColor = turn % 2 == 0 ? "Black" : "White";
        new AlertDialog.Builder(mView.getContext())
                .setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mView.game.newGame();
                    }
                })
                .setNegativeButton(R.string.continue_game, null)
                .setTitle(R.string.reset_dialog_title)
                .setMessage(winColor + " has won!")
                .show();
    }


    public void newGame() {
        if (board == null) {
            board = new Board();
        } else {
            board.clearBoard();
        }
        mView.invalidate();
    }

    /**
     * Adds stone when user locates a stone on the board.
     */
    public void addStone() {
        if (board.isStonesAvailable()) {
            Stone.Color color = turn % 2 == 0 ? Stone.Color.BLACK : Stone.Color.WHITE;
            System.out.println("sX: " + stone_x + "\nsY: " + stone_y + "\nbX: " + board_x + "\nbY: " + board_y);
            Stone cell = new Stone(stone_x, stone_y, color);

            //만나는 점 가운데로 정렬 필요
            cell.setCoordinateX(board_x);
            cell.setCoordinateY(board_y);

            if (board.isStoneAvailable(cell)) {
                spawnCell(cell);
            }

            if (checkWin(cell)) {
                //TODO: What to do when in
                gameWon();
            }
        }
    }

    /**
     * Generate new cells
     * @param cell containing location and value
     */
    private void spawnCell(Stone cell) {
        board.insertStone(cell);
    }

    private boolean checkWin(Stone stone) {
        return checkWinLU(stone) || checkWinU(stone) || checkWinRU(stone) || checkWinR(stone)
                || checkWinRD(stone) || checkWinD(stone) || checkWinLD(stone) || checkWinL(stone);
    }

    public Context getmContext() {
        return mContext;
    }

    private boolean checkWinLU(Stone stone) {
        int total;
        //왼위확인
        try {
            total = 0;
            for (int i = 0; i < 5; i++) {
                if (board.getStone(stone.getX() - i, stone.getY() - i).getColor() == stone.getColor()) {
                    total++;
                }
            }
            return total == 5;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean checkWinU(Stone stone) {
        int total;
        //위확인
        try {
            total = 0;
            for (int i = 0; i < 5; i++) {
                if (board.getStone(stone.getX(), stone.getY() - i).getColor() == stone.getColor()) {
                    total++;
                }
            }
            return total == 5;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean checkWinRU(Stone stone) {
        int total;
        //오위확인
        try {
            total = 0;
            for (int i = 0; i < 5; i++) {
                if (board.getStone(stone.getX() + i, stone.getY() - i).getColor() == stone.getColor()) {
                    total++;
                }
            }
            return total == 5;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean checkWinR(Stone stone) {
        int total;
        //오확인
        try {
            total = 0;
            for (int i = 0; i < 5; i++) {
                if (board.getStone(stone.getX() + i, stone.getY()).getColor() == stone.getColor()) {
                    total++;
                }
            }
            return total == 5;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean checkWinRD(Stone stone) {
        int total;
        //오밑확인
        try {
            total = 0;
            for (int i = 0; i < 5; i++) {
                if (board.getStone(stone.getX() + i, stone.getY() + i).getColor() == stone.getColor()) {
                    total++;
                }
            }
            return total == 5;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean checkWinD(Stone stone) {
        int total;
        //밑확인
        try {
            total = 0;
            for (int i = 0; i < 5; i++) {
                if (board.getStone(stone.getX(), stone.getY() + i).getColor() == stone.getColor()) {
                    total++;
                }
            }
            return total == 5;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean checkWinLD(Stone stone) {
        int total;
        //왼밑확인
        try {
            total = 0;
            for (int i = 0; i < 5; i++) {
                if (board.getStone(stone.getX() - i, stone.getY() + i).getColor() == stone.getColor()) {
                    total++;
                }
            }
            return total == 5;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean checkWinL(Stone stone) {
        int total;
        //왼확인
        try {
            total = 0;
            for (int i = 0; i < 5; i++) {
                if (board.getStone(stone.getX() - i, stone.getY()).getColor() == stone.getColor()) {
                    total++;
                }
            }
            return total == 5;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }
}
