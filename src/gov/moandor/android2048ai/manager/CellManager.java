package gov.moandor.android2048ai.manager;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import gov.moandor.android2048ai.R;
import gov.moandor.android2048ai.bean.Cell;
import gov.moandor.android2048ai.bean.CellAnimation;
import gov.moandor.android2048ai.ui.Controller.Direction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * manage cell data and movements
 * 
 * @author
 * 
 */
public class CellManager implements AnimationListener {
    
    public static final int CELL_STATUS_CREATING = 1;
    public static final int CELL_STATUS_ALLIVE = 2;
    public static final int CELL_STATUS_RECYCLED = 3;
    
    public static final int CELL_STATUS_RECYCLING = 4;
    
    // the max count of objs in pool
    private static final int POOLSIZE = 4;
    
    private Context imcontext;
    
    private CellBoard board;
    
    private LinkedList<Cell> pools;
    
    private int cellMargin, cellSize;
    
    // count of allied cells
    private int alliedCount;
    
    // Cell data (cell[y][x])
    private Cell[][] data = new Cell[4][4];
    
    private List<CellAnimation> moveAnimList;
    
    private List<CellAnimation> placeAnimList;
    
    // current score
    private int score;
    
    public CellManager(Context imcontext, CellBoard board) {
        super();
        this.imcontext = imcontext;
        cellMargin = imcontext.getResources().getDimensionPixelOffset(R.dimen.cell_margin);
        cellSize = imcontext.getResources().getDimensionPixelOffset(R.dimen.cell_size);
        this.board = board;
        init();
    }
    
    private void init() {
        pools = new LinkedList<Cell>();
        moveAnimList = new ArrayList<CellAnimation>();
        placeAnimList = new ArrayList<CellAnimation>();
        alliedCount = 0;
    }
    
    public int getAlliedCount() {
        return alliedCount;
    }
    
    public Cell createNewCell(int value) {
        Cell cell = null;
        TextView view = null;
        
        if (!pools.isEmpty() && pools.peek().getStatus() == CELL_STATUS_RECYCLED) {
            cell = pools.poll();
            view = (TextView) cell.getView();
        }
        
        if (cell == null) {
            cell = new Cell();
            view = new TextView(imcontext);
            LayoutParams params = new LayoutParams(cellSize, cellSize);
            view.setLayoutParams(params);
            view.setTextAppearance(imcontext, R.style.cellTextStyle);
            cell.setView(view);
            view.setGravity(Gravity.CENTER);
        }
        
        cell.setValue(value);
        cell.setStatus(CELL_STATUS_CREATING);
        
        updateCell(cell, value);
        
        return cell;
    }
    
    private void updateCell(Cell cell, int value) {
        TextView view = (TextView) cell.getView();
        cell.setValue(value);
        switch (value) {
        
        case 2:
            view.setTextColor(imcontext.getResources().getColor(R.color.text_2));
            view.setBackgroundResource(R.color.bg_2);
            break;
        
        case 4:
            view.setTextColor(imcontext.getResources().getColor(R.color.text_4));
            view.setBackgroundResource(R.color.bg_4);
            break;
        
        case 8:
            view.setTextColor(imcontext.getResources().getColor(R.color.text_8));
            view.setBackgroundResource(R.color.bg_8);
            break;
        
        case 16:
            view.setTextColor(imcontext.getResources().getColor(R.color.text_16));
            view.setBackgroundResource(R.color.bg_16);
            break;
        
        case 32:
            view.setTextColor(imcontext.getResources().getColor(R.color.text_32));
            view.setBackgroundResource(R.color.bg_32);
            break;
        
        case 64:
            view.setTextColor(imcontext.getResources().getColor(R.color.text_64));
            view.setBackgroundResource(R.color.bg_64);
            break;
        
        case 128:
            view.setTextColor(imcontext.getResources().getColor(R.color.text_128));
            view.setBackgroundResource(R.color.bg_128);
            break;
        
        case 256:
            view.setTextColor(imcontext.getResources().getColor(R.color.text_256));
            view.setBackgroundResource(R.color.bg_256);
            break;
        
        case 512:
            view.setTextColor(imcontext.getResources().getColor(R.color.text_512));
            view.setBackgroundResource(R.color.bg_512);
            break;
        
        case 1024:
            view.setTextColor(imcontext.getResources().getColor(R.color.text_1024));
            view.setBackgroundResource(R.color.bg_1024);
            break;
        
        case 2048:
            view.setTextColor(imcontext.getResources().getColor(R.color.text_2048));
            view.setBackgroundResource(R.color.bg_2048);
            break;
        
        default:
            break;
        }
        view.setText(value + "");
    }
    
    public void destroyCell(Cell cell) {
        pools.add(cell);
        cell.setStatus(CELL_STATUS_RECYCLING);
        data[cell.getCurrY()][cell.getCurrX()] = null;
        alliedCount--;
    }
    
    /**
     * 
     * @param cell
     * @param x
     *            the x-index of cell
     * @param y
     *            the y-index of cell
     */
    public void placeCellOnLocation(Cell cell, int x, int y) {
        cell.setCurrX(x);
        cell.setCurrY(y);
        int marginX = (x + 1) * cellMargin + x * cellSize;
        int marginY = (y + 1) * cellMargin + y * cellSize;
        
        LayoutParams layoutParams = (LayoutParams) cell.getView().getLayoutParams();
        layoutParams.leftMargin = marginX;
        layoutParams.topMargin = marginY;
        
        if (cell.getView().getParent() == null) {
            board.addCell(cell);
        }
        cell.getView().setLayoutParams(layoutParams);
        CellAnimation anim = new CellAnimation(cell, this);
        anim.appear();
        placeAnimList.add(anim);
        
        data[y][x] = cell;
        alliedCount++;
        
    }
    
    public void moveCellTowards(Cell cell, Direction direction) {
        int currX = cell.getCurrX();
        int currY = cell.getCurrY();
        switch (direction) {
        case LEFT:
            for (int x = currX - 1; x >= 0; x--) {
                if (!isLocationEmpty(x, currY)) {
                    Cell target = getCell(x, currY);
                    if (target.getValue() == cell.getValue() && target.getStatus() == CellManager.CELL_STATUS_ALLIVE) {
                        cellMoveAndCombine(cell, target);
                        
                    } else {
                        cellMove(cell, x + 1, currY);
                    }
                    
                    break;
                } else if (x == 0) {
                    cellMove(cell, 0, currY);
                }
            }
            break;
        
        case UP:
            for (int y = currY - 1; y >= 0; y--) {
                if (!isLocationEmpty(currX, y)) {
                    Cell target = getCell(currX, y);
                    if (target.getValue() == cell.getValue() && target.getStatus() == CellManager.CELL_STATUS_ALLIVE) {
                        cellMoveAndCombine(cell, target);
                        
                    } else {
                        cellMove(cell, currX, y + 1);
                    }
                    
                    break;
                } else if (y == 0) {
                    cellMove(cell, currX, 0);
                }
            }
            break;
        case RIGHT:
            for (int x = currX + 1; x < 4; x++) {
                if (!isLocationEmpty(x, currY)) {
                    Cell target = getCell(x, currY);
                    if (target.getValue() == cell.getValue() && target.getStatus() == CellManager.CELL_STATUS_ALLIVE) {
                        cellMoveAndCombine(cell, target);
                        
                    } else {
                        cellMove(cell, x - 1, currY);
                    }
                    
                    break;
                } else if (x == 3) {
                    cellMove(cell, 3, currY);
                }
            }
            break;
        case DOWN:
            for (int y = currY + 1; y < 4; y++) {
                if (!isLocationEmpty(currX, y)) {
                    Cell target = getCell(currX, y);
                    if (target.getValue() == cell.getValue() && target.getStatus() == CellManager.CELL_STATUS_ALLIVE) {
                        cellMoveAndCombine(cell, target);
                        
                    } else {
                        cellMove(cell, currX, y - 1);
                    }
                    break;
                } else if (y == 3) {
                    cellMove(cell, currX, y);
                }
            }
            break;
        
        default:
            break;
        }
        
    }
    
    /**
     * move a cell to a new location
     * 
     * @param cell
     * @param x
     * @param y
     */
    public void cellMove(final Cell cell, int x, int y) {
        if (x == cell.getCurrX() && y == cell.getCurrY()) {
            return;
        }
        
        data[cell.getCurrY()][cell.getCurrX()] = null;
        
        cell.setCurrX(x);
        cell.setCurrY(y);
        int marginX = (x + 1) * cellMargin + x * cellSize;
        int marginY = (y + 1) * cellMargin + y * cellSize;
        
        CellAnimation anim = new CellAnimation(cell, this);
        anim.moveTo(marginX, marginY);
        moveAnimList.add(anim);
        anim.startAnim();
        
        LayoutParams params = (LayoutParams) cell.getView().getLayoutParams();
        params.leftMargin = marginX;
        params.topMargin = marginY;
        cell.getView().setLayoutParams(params);
        
        data[y][x] = cell;
        
    }
    
    /**
     * move a cell over another and combine
     * 
     * @param cell
     * @param target
     */
    public void cellMoveAndCombine(Cell cell, Cell target) {
        if (cell == target) {
            return;
        }
        CellAnimation anim = new CellAnimation(cell, this);
        anim.moveToAndCombine(target);
        moveAnimList.add(anim);
        anim.startAnim();
        
        LayoutParams params = (LayoutParams) cell.getView().getLayoutParams();
        LayoutParams targetParam = (LayoutParams) target.getView().getLayoutParams();
        params.leftMargin = targetParam.leftMargin;
        params.topMargin = targetParam.topMargin;
        cell.getView().setLayoutParams(params);
        
        destroyCell(cell);
        destroyCell(target);
        score += cell.getValue();
        
        int value = cell.getValue() + target.getValue();
        Cell newCell = createNewCell(value);
        placeCellOnLocation(newCell, target.getCurrX(), target.getCurrY());
    }
    
    public void clearData() {
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (data[y][x] != null) {
                    destroyCell(data[y][x]);
                }
            }
        }
        score = 0;
        setCellStatus();
        flush();
    }
    
    public boolean isLocationEmpty(int x, int y) {
        return data[y][x] == null;
    }
    
    public Cell getCell(int x, int y) {
        return data[y][x];
    }
    
    public void showAnimations() {
        if (!moveAnimList.isEmpty()) {
            startMoveAnimation();
            
        } else if (!placeAnimList.isEmpty()) {
            startPLaceAnimation();
        } else {
            setCellStatus();
            flush();
            board.onCellMovementEnd();
        }
        
    }
    
    /**
     * check if user win or lose
     * 
     * @return 1--win 0--normal -1--fail
     */
    public int check() {
        if (getMaxValue() >= 2048) {
            return 1;
        }
        
        // check if not moveable nor combinable
        if (alliedCount == 16) {
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    if (data[y][x] != null) {
                        int value = data[y][x].getValue();
                        if (x != 3) {
                            // compare with right one
                            int targetX = x + 1;
                            
                            while (targetX < 4) {
                                if (data[y][targetX] == null) {
                                    targetX++;
                                    
                                } else {
                                    if (value == data[y][targetX].getValue()) {
                                        return 0;
                                        
                                    } else {
                                        break;
                                    }
                                }
                            }
                            
                        }
                        
                        if (y != 3) {
                            int targetY = y + 1;
                            // compare with down one
                            while (targetY < 4) {
                                if (data[targetY][x] == null) {
                                    targetY++;
                                    
                                } else {
                                    if (value == data[targetY][x].getValue()) {
                                        return 0;
                                        
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                        
                    }
                }
            }
            
            return -1;
        }
        
        return 0;
    }
    
    /**
     * set the status of cells at beginning of every turn
     */
    public void setCellStatus() {
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (data[y][x] != null) {
                    
                    switch (data[y][x].getStatus()) {
                    case CELL_STATUS_CREATING:
                        data[y][x].setStatus(CELL_STATUS_ALLIVE);
                        break;
                    
                    case CELL_STATUS_RECYCLING:
                        destroyCell(data[y][x]);
                        
                        break;
                    default:
                        break;
                    }
                }
            }
        }
        
        ListIterator<Cell> it = pools.listIterator();
        while (it.hasNext()) {
            Cell cell = it.next();
            if (cell.getStatus() == CELL_STATUS_RECYCLING) {
                if (cell.getView().getParent() != null) {
                    ((ViewGroup) cell.getView().getParent()).removeView(cell.getView());
                }
                
                cell.setStatus(CELL_STATUS_RECYCLED);
                
            } else {
                break;
            }
            
            if (pools.size() > POOLSIZE) {
                it.remove();
            }
        }
    }
    
    private void startMoveAnimation() {
        for (int i = 0; i < moveAnimList.size(); i++) {
            CellAnimation anim = moveAnimList.get(i);
            if (anim.getState() == CellAnimation.ANIM_STATE_CREATED) {
                anim.startAnim();
            }
        }
    }
    
    private void startPLaceAnimation() {
        for (int i = 0; i < placeAnimList.size(); i++) {
            CellAnimation anim = placeAnimList.get(i);
            if (anim.getState() == CellAnimation.ANIM_STATE_CREATED) {
                anim.startAnim();
            }
        }
    }
    
    private void flush() {
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (data[y][x] != null) {
                    if (data[y][x].getStatus() == CELL_STATUS_ALLIVE) {
                        data[y][x].getView().requestLayout();
                    }
                }
            }
        }
    }
    
    @Override
    public void onAnimationStart(Animation animation) {
        
    }
    
    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation instanceof CellAnimation) {
            CellAnimation anim = (CellAnimation) animation;
            anim.setState(CellAnimation.ANIM_STATE_ENDED);
            moveAnimList.remove(anim);
            placeAnimList.remove(anim);
        }
        
        if (moveAnimList.size() == 0) {
            startPLaceAnimation();
        }
        
        if (placeAnimList.size() == 0) {
            setCellStatus();
            flush();
            board.onCellMovementEnd();
        }
        
    }
    
    @Override
    public void onAnimationRepeat(Animation animation) {}
    
    /**
     * get max value of the game
     * 
     * @return
     */
    public int getMaxValue() {
        int max = 0;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (data[y][x] != null) {
                    if (data[y][x].getValue() > max) {
                        max = data[y][x].getValue();
                    }
                }
            }
        }
        return max;
    }
    
    /**
     * 
     * @return
     */
    public int getScore() {
        return score;
    }
    
    public Cell[][] getData() {
        return data;
    }
}
