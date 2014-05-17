package gov.moandor.android2048ai.bean;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout.LayoutParams;

import gov.moandor.android2048ai.manager.Constant;

public class CellAnimation extends AnimationSet {
    
    public static final int ANIM_STATE_CREATED = 0;
    
    public static final int ANIM_STATE_STARTED = 1;
    
    public static final int ANIM_STATE_ENDED = 2;
    
    private Cell cell;
    
    private int state;
    
    private String des;
    
    public CellAnimation(Cell cell, AnimationListener listener) {
        super(false);
        this.cell = cell;
        setRepeatCount(0);
        setAnimationListener(listener);
        state = ANIM_STATE_CREATED;
        des = cell.toString();
    }
    
    public int getState() {
        return state;
    }
    
    public void setState(int state) {
        this.state = state;
    }
    
    public void moveTo(int x, int y) {
        LayoutParams params = (LayoutParams) cell.getView().getLayoutParams();
        int oriX = params.leftMargin;
        int oriY = params.topMargin;
        TranslateAnimation translate = new TranslateAnimation(oriX - x, 0, oriY - y, 0);
        translate.setDuration(Constant.ANI_MOVE_DURATION);
        addAnimation(translate);
        des += " move to:" + x + "," + y;
    }
    
    public void moveToAndCombine(Cell target) {
        LayoutParams params = (LayoutParams) cell.getView().getLayoutParams();
        int oriX = params.leftMargin;
        int oriY = params.topMargin;
        
        params = (LayoutParams) target.getView().getLayoutParams();
        int targetX = params.leftMargin;
        int targetY = params.topMargin;
        TranslateAnimation translate = new TranslateAnimation(oriX - targetX, 0, oriY - targetY, 0);
        translate.setDuration(Constant.ANI_MOVE_DURATION);
        addAnimation(translate);
        
        des += " move to and combine:" + target.toString();
        
    }
    
    public void appear() {
        cell.getView().setVisibility(View.INVISIBLE);
        ScaleAnimation scale =
                new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(Constant.ANI_ELASTICITY_DURATION);
        addAnimation(scale);
        des += " appear";
    }
    
    public void startAnim() {
        cell.getView().setVisibility(View.VISIBLE);
        state = ANIM_STATE_STARTED;
        cell.getView().startAnimation(this);
    }
    
    @Override
    public String toString() {
        return des;
    }
}
