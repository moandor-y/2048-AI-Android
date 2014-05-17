package gov.moandor.android2048ai.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gov.moandor.android2048ai.R;
import gov.moandor.android2048ai.bean.Cell;
import gov.moandor.android2048ai.ui.Controller.ControllerCallBack;
import gov.moandor.android2048ai.ui.Controller.Direction;

/**
 * cell board and game manager
 * 
 * @author
 * 
 */
public class CellBoard implements ControllerCallBack {
    
    private CellManager manager;
    
    private Context context;
    
    // the PROBABILITY of 2 and 4 when creating random cell
    // the bigger, the more 2 appears
    private static final int PROBABILITY_2 = 8;
    // the bigger, the more 4 appears
    private static final int PROBABILITY_4 = 1;
    
    // whether to respond controller
    private boolean isActive = true;
    
    private RelativeLayout cellContainer = null;
    private TextView text_value = null;
    private TextView text_best_score = null;
    private TextView text_curr_score = null;
    
    public CellBoard(Context context) {
        this.context = context;
        init();
        
    }
    
    public View createBoardView() {
        View view = LayoutInflater.from(context).inflate(R.layout.lay_board, null);
        cellContainer = (RelativeLayout) view.findViewById(R.id.cell_board);
        text_value = (TextView) view.findViewById(R.id.max_value);
        text_best_score = (TextView) view.findViewById(R.id.maxScore);
        text_curr_score = (TextView) view.findViewById(R.id.currScore);
        
        return view;
    }
    
    private void init() {
        manager = new CellManager(context, this);
    }
    
    /**
     * start game
     */
    public void startGame() {
        reset();
        placeRamdomCell();
        placeRamdomCell();
        manager.showAnimations();
    }
    
    /**
     * place a cell in ramdom position
     */
    public void placeRamdomCell() {
        
        int temp = (int) (Math.random() * (PROBABILITY_2 + PROBABILITY_4));
        int value = 2;
        if (temp < PROBABILITY_4) {
            value = 4;
        }
        
        int availableCount = 16 - manager.getAlliedCount();
        Cell cell = manager.createNewCell(value);
        int index = (int) (Math.random() * availableCount);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (manager.isLocationEmpty(x, y)) {
                    if (index == 0) {
                        manager.placeCellOnLocation(cell, x, y);
                        return;
                    }
                    index--;
                }
                
            }
        }
    }
    
    // reset to default state
    public void reset() {
        manager.clearData();
        
        cellContainer.removeAllViews();
        updateScores();
        
    }
    
    @Override
    public void onDirectionChosed(Direction direction) {
        if (!isActive) {
            return;
        }
        isActive = false;
        if (direction == Direction.LEFT || direction == Direction.UP) {
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    if (!manager.isLocationEmpty(x, y)) {
                        Cell cell = manager.getCell(x, y);
                        manager.moveCellTowards(cell, direction);
                    }
                }
            }
        } else {
            for (int x = 3; x >= 0; x--) {
                for (int y = 3; y >= 0; y--) {
                    if (!manager.isLocationEmpty(x, y)) {
                        Cell cell = manager.getCell(x, y);
                        manager.moveCellTowards(cell, direction);
                    }
                }
            }
        }
        
        int result = manager.check();
        switch (result) {
        case -1:
            Intent failIntent = new Intent(Constant.ACTION_FILTER_2048GAME);
            failIntent.putExtra(Constant.ACTION_FILTER_2048GAME, Constant.RESULT_FAIL);
            context.sendBroadcast(failIntent);
            break;
        
        case 1:
            Intent winIntent = new Intent(Constant.ACTION_FILTER_2048GAME);
            winIntent.putExtra(Constant.ACTION_FILTER_2048GAME, Constant.RESULT_WINNING);
            context.sendBroadcast(winIntent);
            break;
        case 0:
            placeRamdomCell();
            break;
        default:
            break;
        }
        
        manager.showAnimations();
    }
    
    public void onCellMovementEnd() {
        isActive = true;
        int curr = manager.getScore();
        int highScore = getBestScore();
        if (curr > highScore) {
            saveBestScore(curr);
        }
        
        updateScores();
    }
    
    private void updateScores() {
        int highScore = getBestScore();
        text_best_score.setText(String.valueOf(highScore));
        text_curr_score.setText(String.valueOf(manager.getScore()));
        text_value.setText(String.valueOf(manager.getMaxValue()));
    }
    
    public void addCell(Cell cell) {
        cellContainer.addView(cell.getView());
    }
    
    private void saveBestScore(int score) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_BEST_SCORE, Context.MODE_PRIVATE);
        sp.edit().putInt(Constant.SP_BEST_SCORE, score).commit();
    }
    
    private int getBestScore() {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_BEST_SCORE, Context.MODE_PRIVATE);
        int result = sp.getInt(Constant.SP_BEST_SCORE, 0);
        return result;
    }
    
    public CellManager getManager() {
        return manager;
    }
}
