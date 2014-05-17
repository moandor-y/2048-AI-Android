package gov.moandor.android2048ai.bean;

import android.view.View;

/**
 * Cell Bean
 * 
 * @author
 */
public class Cell {
    private View view;
    
    // cell's value(2,4,8,16...)
    private int value;
    
    // the current location
    private int currX, currY;
    
    // the status
    private int status;
    
    public View getView() {
        return view;
    }
    
    public void setView(View view) {
        this.view = view;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public int getCurrX() {
        return currX;
    }
    
    public void setCurrX(int currX) {
        this.currX = currX;
    }
    
    public int getCurrY() {
        return currY;
    }
    
    public void setCurrY(int currY) {
        this.currY = currY;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "x:" + currX + " y:" + currY + " v:" + value;
    }
    
}
