package gov.moandor.android2048ai;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

import gov.moandor.android2048ai.R;
import gov.moandor.android2048ai.bean.Cell;
import gov.moandor.android2048ai.manager.CellBoard;
import gov.moandor.android2048ai.manager.Constant;
import gov.moandor.android2048ai.ui.Controller;

public class MainActivity extends Activity {
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    private static final String JS_INTERFACE = "Android";
    
    private CellBoard mCellBoard;
    
    private Button btn_reset;
    
    private boolean mAutoRunning;
    private Controller mController;
    private WebView mJsExecutor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mJsExecutor = new WebView(this);
        mJsExecutor.getSettings().setJavaScriptEnabled(true);
        mJsExecutor.addJavascriptInterface(this, JS_INTERFACE);
        mController = new Controller(this);
        mCellBoard = new CellBoard(this);
        View boardView = mCellBoard.createBoardView();
        
        ViewGroup lay_content = (ViewGroup) findViewById(R.id.lay_content);
        lay_content.addView(boardView);
        lay_content.addView(mController);
        mController.setCallback(mCellBoard);
        
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                showResetDialog();
            }
        });
        final Button autoButton = (Button) findViewById(R.id.btn_auto);
        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAutoRunning) {
                    autoButton.setText(R.string.stop_auto);
                    startAuto();
                } else {
                    autoButton.setText(R.string.start_auto);
                    stopAuto();
                }
            }
        });
        
        registeReceiver();
        
        mCellBoard.startGame();
    }
    
    private void registeReceiver() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra(Constant.ACTION_FILTER_2048GAME);
                if (msg.equals(Constant.RESULT_WINNING)) {
                    onSucess();
                } else if (msg.equals(Constant.RESULT_FAIL)) {
                    onFail();
                }
                if (mAutoRunning) {
                    stopAuto();
                }
            }
        };
        
        IntentFilter filter = new IntentFilter(Constant.ACTION_FILTER_2048GAME);
        registerReceiver(receiver, filter);
    }
    
    private void onSucess() {
        showWinOrFailDialog(R.string.win_message);
    }
    
    private void onFail() {
        showWinOrFailDialog(R.string.fail_message);
    }
    
    /**
     * exit dialog
     */
    protected void showExitDialog() {
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        builder.setMessage(getString(R.string.exit_message));
        builder.setPositiveButton(getString(R.string.confirm), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    
    /**
     * reset game dialog
     */
    protected void showResetDialog() {
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        builder.setMessage(getString(R.string.reset_message));
        builder.setPositiveButton(getString(R.string.confirm), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mCellBoard.reset();
                mCellBoard.startGame();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    
    /**
     * when win or lose
     */
    protected void showWinOrFailDialog(int msgId) {
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        builder.setMessage(getString(msgId));
        builder.setPositiveButton(getString(R.string.confirm), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    
    @Override
    public void onBackPressed() {
        showExitDialog();
    }
    
    private void startAuto() {
        mAutoRunning = true;
        mController.setIsActive(false);
        mJsExecutor.loadUrl("file:///android_asset/index.html");
    }
    
    private void stopAuto() {
        mAutoRunning = false;
        mController.setIsActive(true);
        mJsExecutor.loadUrl("about:blank");
    }
    
    @android.webkit.JavascriptInterface
    public int cellValue(int x, int y) {
        Cell[][] data = mCellBoard.getManager().getData();
        Cell cell = data[y][x];
        if (cell != null) {
            return cell.getValue();
        } else {
            return 0;
        }
    }
    
    @android.webkit.JavascriptInterface
    public void onAiResult(String directionStr) {
        final int direction = Integer.parseInt(directionStr);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (direction) {
                case UP:
                    mCellBoard.onDirectionChosed(Controller.Direction.UP);
                    break;
                case DOWN:
                    mCellBoard.onDirectionChosed(Controller.Direction.DOWN);
                    break;
                case LEFT:
                    mCellBoard.onDirectionChosed(Controller.Direction.LEFT);
                    break;
                case RIGHT:
                    mCellBoard.onDirectionChosed(Controller.Direction.RIGHT);
                    break;
                }
            }
        });
    }
    
    @android.webkit.JavascriptInterface
    public void debug(String msg) {
        Log.d("ai", msg);
    }
}
