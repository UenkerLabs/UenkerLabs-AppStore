package com.uenkerlabs.gamekit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

/** Shared non-network fullscreen host for each separate application. */
public abstract class GameHost extends Activity {
    protected FrameGame game;
    protected abstract FrameGame makeGame();
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);fullscreen();game=makeGame();setContentView(game);}
    private void fullscreen(){getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);}
    @Override protected void onPause(){super.onPause();if(game!=null)game.pauseLoop();}
    @Override protected void onResume(){super.onResume();fullscreen();if(game!=null)game.resumeLoop();}
    @Override public void onBackPressed(){if(game instanceof BackHandler)((BackHandler)game).back();else super.onBackPressed();}
    public interface BackHandler {void back();}
}
