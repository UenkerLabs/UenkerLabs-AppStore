package com.uenkerlabs.gamekit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import java.util.*;

/** Original, offline native-Android 60Hz game foundation. Logical resolution 1280x720. */
public abstract class FrameGame extends View {
    public static final float W=1280f, H=720f;
    protected final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    protected final SharedPreferences save;
    protected final Random rng = new Random(20261111);
    protected final HashMap<Integer, Finger> fingers = new HashMap<>();
    protected final Activity host;
    protected boolean paused=false;
    private long lastFrame=0;
    private float scale=1, offsetX=0, offsetY=0;
    private float elapsed=0;
    public static final class Finger {
        public float x,y,startX,startY;
        public long started;
        Finger(float x,float y){this.x=x;this.y=y;startX=x;startY=y;started=SystemClock.uptimeMillis();}
    }
    protected FrameGame(Activity a,String storage){
        super(a);host=a;save=a.getSharedPreferences("uenkerlabs_"+storage, Context.MODE_PRIVATE);
        setLayerType(View.LAYER_TYPE_HARDWARE,null);
        setFocusable(true);
    }
    protected abstract void tick(float dt);
    protected abstract void scene(Canvas c);
    protected void down(float x,float y,int pointer){}
    protected void up(float x,float y,int pointer){}
    protected void move(float x,float y,int pointer){}
    public final void resumeLoop(){paused=false;lastFrame=0;postInvalidateOnAnimation();}
    public final void pauseLoop(){paused=true;lastFrame=0;}
    @Override protected final void onDraw(Canvas real){
        super.onDraw(real);
        float sx=getWidth()/W, sy=getHeight()/H;
        scale=Math.min(sx,sy); if(scale<=0)scale=1;
        offsetX=(getWidth()-W*scale)/2f;offsetY=(getHeight()-H*scale)/2f;
        real.drawColor(Color.BLACK);
        real.save();real.translate(offsetX,offsetY);real.scale(scale,scale);
        long now=SystemClock.elapsedRealtimeNanos();
        float dt=lastFrame==0?.016f:(now-lastFrame)/1e9f;
        lastFrame=now; dt=Math.max(.004f,Math.min(.035f,dt));elapsed+=dt;
        if(!paused)tick(dt);
        scene(real);
        real.restore();
        if(!paused)postInvalidateOnAnimation();
    }
    @Override public final boolean onTouchEvent(MotionEvent ev){
        int action=ev.getActionMasked(),idx=ev.getActionIndex(),id=ev.getPointerId(idx);
        if(action==MotionEvent.ACTION_DOWN||action==MotionEvent.ACTION_POINTER_DOWN){
            float x=(ev.getX(idx)-offsetX)/scale,y=(ev.getY(idx)-offsetY)/scale;
            fingers.put(id,new Finger(x,y));down(x,y,id);
        }else if(action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_POINTER_UP){
            Finger f=fingers.get(id);float x=(ev.getX(idx)-offsetX)/scale,y=(ev.getY(idx)-offsetY)/scale;
            up(x,y,id);fingers.remove(id);
        }else if(action==MotionEvent.ACTION_CANCEL){fingers.clear();}
        else if(action==MotionEvent.ACTION_MOVE){
            for(int i=0;i<ev.getPointerCount();i++){
                int pointer=ev.getPointerId(i);Finger f=fingers.get(pointer);if(f==null)continue;
                float x=(ev.getX(i)-offsetX)/scale,y=(ev.getY(i)-offsetY)/scale;
                f.x=x;f.y=y;move(x,y,pointer);
            }
        }
        return true;
    }
    protected float time(){return elapsed;}
    protected boolean held(float x,float y,float radius){
        float r=radius*radius;for(Finger f:fingers.values())if((f.x-x)*(f.x-x)+(f.y-y)*(f.y-y)<=r)return true;return false;
    }
    protected boolean in(float tx,float ty,float x,float y,float w,float h){return tx>=x&&tx<=x+w&&ty>=y&&ty<=y+h;}
    protected void color(int col){p.reset();p.setAntiAlias(true);p.setFilterBitmap(true);p.setColor(col);p.setStyle(Paint.Style.FILL);}
    protected static int rgb(int r,int g,int b){return Color.rgb(r,g,b);}
    protected static int argb(int a,int r,int g,int b){return Color.argb(a,r,g,b);}
    protected void rect(Canvas c,float x,float y,float w,float h,int col){color(col);c.drawRect(x,y,x+w,y+h,p);}
    protected void rounded(Canvas c,float x,float y,float w,float h,float r,int col){color(col);c.drawRoundRect(x,y,x+w,y+h,r,r,p);}
    protected void circle(Canvas c,float x,float y,float r,int col){color(col);c.drawCircle(x,y,r,p);}
    protected void stroke(Canvas c,float x,float y,float w,float h,float r,int col,float weight){
        color(col);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(weight);c.drawRoundRect(x,y,x+w,y+h,r,r,p);p.setStyle(Paint.Style.FILL);
    }
    protected void line(Canvas c,float x,float y,float x2,float y2,int col,float weight){
        color(col);p.setStrokeWidth(weight);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,x2,y2,p);
    }
    protected void text(Canvas c,String value,float x,float baseline,int size,int col,boolean bold){
        color(col);p.setTextSize(size);p.setTypeface(bold?Typeface.create("sans-serif",Typeface.BOLD):Typeface.create("sans-serif",Typeface.NORMAL));
        c.drawText(value,x,baseline,p);
    }
    protected void center(Canvas c,String value,float x,float baseline,int size,int col,boolean bold){
        color(col);p.setTextSize(size);p.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));
        p.setTextAlign(Paint.Align.CENTER);c.drawText(value,x,baseline,p);p.setTextAlign(Paint.Align.LEFT);
    }
    protected void gradient(Canvas c,float x,float y,float w,float h,int top,int bottom){
        color(top);p.setShader(new LinearGradient(x,y,x,y+h,top,bottom,Shader.TileMode.CLAMP));
        c.drawRect(x,y,x+w,y+h,p);p.setShader(null);
    }
    protected void glow(Canvas c,float x,float y,float r,int col){
        color(col);p.setShader(new RadialGradient(x,y,r,col,Color.TRANSPARENT,Shader.TileMode.CLAMP));
        c.drawCircle(x,y,r,p);p.setShader(null);
    }
    protected void panel(Canvas c,float x,float y,float w,float h){rounded(c,x,y,w,h,20,argb(225,16,25,48));stroke(c,x,y,w,h,20,argb(105,150,202,255),1.6f);}
    protected void chip(Canvas c,String value,float x,float y,float w,float h,int border){
        rounded(c,x,y,w,h,16,argb(215,21,34,61));stroke(c,x,y,w,h,16,border,2);
        center(c,value,x+w/2,y+h/2+8,22,Color.WHITE,true);
    }
    protected void drawBar(Canvas c,float x,float y,float w,float h,float fraction,int background,int foreground){
        rounded(c,x,y,w,h,h/2,background);
        if(fraction>0)rounded(c,x,y,Math.max(0,w*Math.min(1,fraction)),h,h/2,foreground);
    }
    protected void starfield(Canvas c,int count,int seed,float speed){
        Random r=new Random(seed);for(int i=0;i<count;i++){
            float x=r.nextFloat()*W,y=r.nextFloat()*H,sz=.6f+r.nextFloat()*2;
            float shift=(time()*speed*(.1f+r.nextFloat()))%W;
            circle(c,(x+shift)%W,y,sz,argb(90+r.nextInt(120),185,226,255));
        }
    }
    protected String shortNum(int value){return String.format(Locale.GERMANY,"%,d",value);}
}
