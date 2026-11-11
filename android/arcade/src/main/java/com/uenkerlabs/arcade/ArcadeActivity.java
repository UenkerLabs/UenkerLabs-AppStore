package com.uenkerlabs.arcade;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.widget.*;
import java.util.*;

public final class ArcadeActivity extends Activity {
    private static final int BACKGROUND=0xfff3f7fd;
    private static final int INK=0xff14253d;
    private static final int BLUE=0xffc9e0ff;
    private final Random random=new Random();
    private final Handler handler=new Handler(Looper.getMainLooper());
    private LinearLayout root, stage;
    private TextView title, status;
    private int mode=0,round=0,score=0;
    private int arithmeticResult;
    private final Set<Integer> pattern=new HashSet<>();
    private final Set<Integer> choices=new HashSet<>();
    private final ArrayList<Integer> sequence=new ArrayList<>();
    private final ArrayList<Button> sequenceButtons=new ArrayList<>();
    private int inputIndex;
    private boolean canAnswer, isReady;
    private long readyAt;
    private SlidePuzzle puzzle;

    private int dp(int n){return (int)(getResources().getDisplayMetrics().density*n+.5f);}
    private Button button(String s,Runnable action){
        Button b=new Button(this);b.setText(s);b.setAllCaps(false);
        b.setTextSize(17);b.setMinHeight(dp(60));
        b.setOnClickListener(v->action.run());return b;
    }
    private TextView label(String s,int size){
        TextView t=new TextView(this);t.setText(s);t.setTextColor(INK);t.setTextSize(size);
        t.setPadding(dp(8),dp(9),dp(8),dp(9));return t;
    }
    @Override public void onCreate(Bundle b){super.onCreate(b);renderShell();switchGame(0);}
    @Override protected void onDestroy(){handler.removeCallbacksAndMessages(null);super.onDestroy();}
    private void renderShell(){
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(10),dp(14),dp(10),dp(8));root.setBackgroundColor(BACKGROUND);
        setContentView(root);
        title=label("UenkerLabs Spiele",26);title.setGravity(Gravity.CENTER);
        root.addView(title);
        HorizontalScrollView navScroll=new HorizontalScrollView(this);
        navScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout nav=new LinearLayout(this);nav.setOrientation(LinearLayout.HORIZONTAL);
        String[] names={"Zahlen","Merkfeld","Reaktion","Muster","Schieben"};
        for(int i=0;i<names.length;i++){final int n=i;nav.addView(button(names[i],()->switchGame(n)));}
        navScroll.addView(nav);root.addView(navScroll);
        status=label("",17);status.setGravity(Gravity.CENTER);
        root.addView(status);
        ScrollView scroll=new ScrollView(this);
        stage=new LinearLayout(this);stage.setOrientation(LinearLayout.VERTICAL);
        stage.setPadding(dp(3),dp(12),dp(3),dp(12));
        scroll.addView(stage);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        root.addView(label("Offline · Keine Werbung · Keine Datensammlung",12));
    }
    private void switchGame(int which){
        handler.removeCallbacksAndMessages(null);
        mode=which;round=0;score=0;canAnswer=false;isReady=false;
        stage.removeAllViews();
        String[] names={"Zahlensprung","Merkfeld","Reaktionslabor","Musterfolge","Schiebepuzzle"};
        title.setText("UenkerLabs · "+names[mode]);
        switch(mode){
            case 0: math();break;
            case 1: memory();break;
            case 2: reaction();break;
            case 3: sequenceStart();break;
            default: sliding();break;
        }
    }
    private void menuRestart(){stage.addView(button("Neu starten",()->switchGame(mode)));}
    private void math(){
        stage.removeAllViews();round++;
        if(round>10){status.setText("Zahlensprung beendet · "+score+" von 10 Punkten");menuRestart();return;}
        int x=random.nextInt(40)+1,y=random.nextInt(40)+1;
        boolean subtraction=random.nextBoolean();
        if(subtraction&&y>x){int swap=x;x=y;y=swap;}
        arithmeticResult=subtraction?x-y:x+y;
        status.setText("Aufgabe "+round+"/10 · Punkte: "+score);
        TextView question=label(x+(subtraction?" − ":" + ")+y+" = ?",29);
        question.setGravity(Gravity.CENTER);stage.addView(question);
        Set<Integer> opts=new LinkedHashSet<>();opts.add(arithmeticResult);
        while(opts.size()<4){
            int other=arithmeticResult+random.nextInt(21)-10;
            if(other>=0&&other!=arithmeticResult)opts.add(other);
        }
        ArrayList<Integer> shuffled=new ArrayList<>(opts);Collections.shuffle(shuffled,random);
        for(int i=0;i<shuffled.size();i+=2){
            LinearLayout row=new LinearLayout(this);
            for(int j=i;j<Math.min(i+2,shuffled.size());j++){
                final int answer=shuffled.get(j);
                row.addView(button(""+answer,()->{
                    if(!canAnswer)return;
                    canAnswer=false;if(answer==arithmeticResult)score++;
                    Toast.makeText(this,answer==arithmeticResult?"Richtig!":"Richtig wäre "+arithmeticResult,Toast.LENGTH_SHORT).show();
                    handler.postDelayed(()->{if(mode==0)math();},650);
                }),new LinearLayout.LayoutParams(0,dp(80),1));
            }
            stage.addView(row);
        }
        canAnswer=true;
    }
    private void memory(){
        stage.removeAllViews();round++;
        if(round>5){status.setText("Merkfeld beendet · "+score+" von 5 Punkten");menuRestart();return;}
        status.setText("Runde "+round+"/5 · Merke dir die drei Felder.");
        pattern.clear();choices.clear();canAnswer=false;
        while(pattern.size()<3)pattern.add(random.nextInt(9));
        final ArrayList<Button> tiles=new ArrayList<>();
        for(int i=0;i<3;i++){
            LinearLayout row=new LinearLayout(this);
            for(int j=0;j<3;j++){
                final int n=i*3+j;
                Button tile=button(""+(n+1),()->{
                    if(!canAnswer)return;
                    if(choices.contains(n))choices.remove(n);else choices.add(n);
                    for(int k=0;k<9;k++)tiles.get(k).setBackgroundTintList(
                        ColorStateList.valueOf(choices.contains(k)?BLUE:0xffe6eaf0));
                    if(choices.size()==3){
                        canAnswer=false;
                        if(choices.equals(pattern)){score++;status.setText("Treffer! "+score+" Punkte");}
                        else status.setText("Nicht ganz. "+score+" Punkte");
                        for(int k=0;k<9;k++)tiles.get(k).setBackgroundTintList(
                            ColorStateList.valueOf(pattern.contains(k)?0xff9bd5ab:0xffe6eaf0));
                        handler.postDelayed(()->{if(mode==1)memory();},1200);
                    }
                });
                tile.setContentDescription("Feld "+(n+1));
                tile.setBackgroundTintList(ColorStateList.valueOf(pattern.contains(n)?BLUE:0xffe6eaf0));
                tiles.add(tile);row.addView(tile,new LinearLayout.LayoutParams(0,dp(91),1));
            }
            stage.addView(row);
        }
        handler.postDelayed(()->{
            if(mode!=1)return;
            for(Button tile:tiles)tile.setBackgroundTintList(ColorStateList.valueOf(0xffe6eaf0));
            status.setText("Tippe die drei gemerkten Felder an.");
            canAnswer=true;
        },1600);
    }
    private void reaction(){
        stage.removeAllViews();
        status.setText("Tippe erst, wenn das Feld GRÜN wird.");
        isReady=false;canAnswer=true;
        Button target=button("WARTEN …",()->{
            if(!canAnswer)return;
            canAnswer=false;
            if(!isReady){
                status.setText("Zu früh! Noch einmal.");
                handler.postDelayed(()->{if(mode==2)reaction();},800);
            }else{
                long elapsed=SystemClock.elapsedRealtime()-readyAt;
                status.setText("Reaktionszeit: "+elapsed+" ms");
                stage.removeAllViews();
                stage.addView(label("Messwert ist eine ungefähre Touchscreen-Reaktionszeit, kein medizinischer Test.",15));
                menuRestart();
            }
        });
        target.setBackgroundTintList(ColorStateList.valueOf(0xffe9bdc0));
        target.setMinHeight(dp(220));stage.addView(target);
        handler.postDelayed(()->{
            if(mode!=2||!canAnswer)return;
            isReady=true;readyAt=SystemClock.elapsedRealtime();
            target.setText("JETZT TIPPEN");target.setBackgroundTintList(ColorStateList.valueOf(0xff92d3a5));
            status.setText("Jetzt!");
        },1300+random.nextInt(2300));
    }
    private void sequenceStart(){
        stage.removeAllViews();sequence.clear();sequenceButtons.clear();
        round=0;score=0;
        for(int i=0;i<2;i++){
            LinearLayout row=new LinearLayout(this);
            for(int j=0;j<2;j++){
                final int n=i*2+j;
                Button b=button("Feld "+(n+1),()->sequenceInput(n));
                b.setMinHeight(dp(120));
                sequenceButtons.add(b);row.addView(b,new LinearLayout.LayoutParams(0,dp(126),1));
            }
            stage.addView(row);
        }
        stage.addView(label("Merke dir die angezeigte Reihenfolge. Danach dieselben Felder antippen.",15));
        playSequence();
    }
    private int buttonColor(int i){
        return new int[]{0xffdbeafe,0xffd8f3dc,0xffffe4cb,0xffe8dbfb}[i];
    }
    private void paintSequenceButtons(int active){
        for(int k=0;k<sequenceButtons.size();k++){
            Button b=sequenceButtons.get(k);
            b.setBackgroundTintList(ColorStateList.valueOf(k==active?0xff87bdfb:buttonColor(k)));
        }
    }
    private void playSequence(){
        canAnswer=false;inputIndex=0;
        sequence.add(random.nextInt(4));round++;
        status.setText("Muster "+round+" · Schau zu!");
        paintSequenceButtons(-1);
        for(int i=0;i<sequence.size();i++){
            final int n=sequence.get(i);
            final int delay=i*1000;
            handler.postDelayed(()->{if(mode==3){paintSequenceButtons(n);}},delay+420);
            handler.postDelayed(()->{if(mode==3){paintSequenceButtons(-1);}},delay+930);
        }
        handler.postDelayed(()->{if(mode==3){status.setText("Jetzt die Folge eingeben");canAnswer=true;}},sequence.size()*1000+100);
    }
    private void sequenceInput(int tapped){
        if(!canAnswer)return;
        if(sequence.get(inputIndex)!=tapped){
            canAnswer=false;status.setText("Reihenfolge falsch · "+score+" Punkte");
            stage.addView(button("Neue Runde",()->switchGame(3)));
            return;
        }
        inputIndex++;
        if(inputIndex==sequence.size()){
            canAnswer=false;score=round;
            if(round>=8){status.setText("Geschafft! Alle acht Muster.");menuRestart();}
            else{status.setText("Richtig! "+score+" Punkte");
                handler.postDelayed(()->{if(mode==3)playSequence();},800);}
        }
    }
    private void sliding(){
        stage.removeAllViews();
        puzzle=new SlidePuzzle();puzzle.shuffle(random);
        status.setText("Bringe die Zahlen von 1 bis 8 in die richtige Reihenfolge.");
        showSlidingBoard();
    }
    private void showSlidingBoard(){
        stage.removeAllViews();int[] items=puzzle.snapshot();
        for(int i=0;i<3;i++){
            LinearLayout row=new LinearLayout(this);
            for(int j=0;j<3;j++){
                int index=i*3+j;
                int value=items[index];
                Button tile=button(value==0?" ":String.valueOf(value),()->{
                    if(puzzle.move(index)){showSlidingBoard();
                        if(puzzle.solved()){status.setText("Gelöst in "+puzzle.moves()+" Zügen!");
                            stage.addView(button("Noch einmal",()->sliding()));}
                    }
                });
                tile.setContentDescription(value==0?"Leeres Feld":"Zahl "+value);
                tile.setEnabled(value!=0);
                tile.setBackgroundTintList(ColorStateList.valueOf(value==0?0xfff3f7fd:BLUE));
                row.addView(tile,new LinearLayout.LayoutParams(0,dp(86),1));
            }
            stage.addView(row);
        }
        stage.addView(label("Züge: "+puzzle.moves()+" · Tippe ein Feld direkt neben der freien Stelle.",16));
        stage.addView(button("Neu mischen",()->sliding()));
    }
}
