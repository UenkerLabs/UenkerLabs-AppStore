package com.uenkerlabs.ember;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import com.uenkerlabs.gamekit.GameHost;
import com.uenkerlabs.gamekit.FrameGame;
import java.util.*;

public final class GameActivity extends GameHost {
  @Override protected FrameGame makeGame(){return new Atlas(this);}

  /** Eight original fantasy chapters, complete objectives, character upgrades and stored progress. */
  static final class Atlas extends FrameGame implements GameHost.BackHandler {
    static final int CHAPTERS=8, TILE=62, COLS=40, ROWS=25;
    static final int MENU=0, PLAY=1, PAUSE=2, DOWN=3, VICTORY=4, FINISHED=5;
    static final String[] NAMES={"Die Mooswarte","Das Nebelmoor","Flüsternde Wipfel","Sternenbruch",
        "Bernsteinpfad","Glasklippen","Der Nachtgarten","Herz der Asche"};
    static final String[] STORIES={
        "Das Licht der alten Wege erlischt. Finde die drei Funken.",
        "Im Moor schlafen Erinnerungen. Wecke die Steine.",
        "Die Wächter des Waldes brauchen deine Hilfe.",
        "Ein Wächter bewacht den ersten Sternenkern.",
        "Folge den Spuren der verlorenen Karawane.",
        "Die gläsernen Tore brechen. Halte die Linie.",
        "Der Garten verbirgt den Weg zum letzten Kern.",
        "Schließe den Kreis und entfache den Ember Atlas."};
    static final int[] BG={0xff203b39,0xff283b55,0xff1c4340,0xff47314a,0xff63412f,0xff25485c,0xff44365c,0xff482b37};
    static final int[] ACCENT={0xff9ee8a6,0xff9bd1f4,0xffaee76c,0xffffb8dc,0xffffd38b,0xff95ebdf,0xffc8afff,0xffffa984};
    static final class Beast {float x,y,hp,cd,phase;int type;Beast(float x,float y,int type,float hp){this.x=x;this.y=y;this.type=type;this.hp=hp;}}
    static final class Relic {float x,y;boolean taken;Relic(float x,float y){this.x=x;this.y=y;}}
    private final int[][] terrain=new int[COLS][ROWS];
    private final ArrayList<Beast> beasts=new ArrayList<>();
    private final ArrayList<Relic> relics=new ArrayList<>();
    private float x=2*TILE,y=12*TILE,health=100,mana=100,attackCool=0,magicCool=0,slash=0,hitFlash=0;
    private float cameraX=0,cameraY=0,worldTime=0;
    private int chapter=1,unlocked=1,mode=MENU,shards=0,coins=0,levelXP=0,power=1,armor=1,speed=1;
    private boolean storyOpen=true,portalActive=false;
    private float banner=0;
    private String message="";
    Atlas(Activity a){super(a,"ember_atlas");unlocked=Math.max(1,save.getInt("unlocked",1));coins=save.getInt("coins",0);power=save.getInt("power",1);armor=save.getInt("armor",1);speed=save.getInt("speed",1);}
    @Override public void back(){if(mode==PLAY)mode=PAUSE;else if(mode==PAUSE)mode=PLAY;else mode=MENU;invalidate();}
    private boolean blocked(float xx,float yy){
      int xx1=(int)(xx/TILE),yy1=(int)(yy/TILE);
      return xx1<0||yy1<0||xx1>=COLS||yy1>=ROWS||terrain[xx1][yy1]==1;
    }
    private void start(int chap){
      chapter=chap;mode=PLAY;shards=0;levelXP=0;worldTime=0;storyOpen=true;banner=0;
      health=100+armor*12;mana=100;slash=0;attackCool=0;magicCool=0;
      x=2.5f*TILE;y=12.5f*TILE;beasts.clear();relics.clear();portalActive=false;
      for(int i=0;i<COLS;i++)for(int j=0;j<ROWS;j++){
        boolean out=i==0||j==0||i==COLS-1||j==ROWS-1;
        boolean decor=i>4&&i<COLS-3&&j>1&&j<ROWS-2 && (i*19+j*13+chapter*7)%17==0;
        if(j>=10&&j<=14)decor=false;  // Guaranteed navigable story corridor.
        if(i<5||i>COLS-4)decor=false;
        terrain[i][j]=out||decor?1:0;
      }
      for(int i=0;i<3;i++){
        int cx=7+i*10,cy=7+(i%2)*10;
        terrain[cx][cy]=0;relics.add(new Relic((cx+.5f)*TILE,(cy+.5f)*TILE));
      }
      int count=4+chap*2;
      for(int i=0;i<count;i++){
        int col=7+(i%7)*4,row=5+((i*3)%14);
        if(!blocked((col+.5f)*TILE,(row+.5f)*TILE))beasts.add(new Beast((col+.5f)*TILE,(row+.5f)*TILE,i%3,45+chap*11));
      }
      if(chap==4||chap==8)beasts.add(new Beast(34.5f*TILE,12.5f*TILE,3,250+chap*35));
    }
    private void message(String msg){message=msg;banner=2.3f;}
    private void sword(){if(attackCool>0)return;attackCool=.38f;slash=.22f;
      for(Beast b:beasts)if(b.hp>0&&Math.hypot(x-b.x,y-b.y)<96){b.hp-=25+power*18;levelXP+=5;}
      clean();
    }
    private void spell(){if(magicCool>0||mana<22)return;magicCool=.9f;mana-=22;
      Beast nearest=null;double min=350;
      for(Beast b:beasts){double d=Math.hypot(x-b.x,y-b.y);if(d<min){min=d;nearest=b;}}
      if(nearest!=null){nearest.hp-=56+power*13;message("FUNKE GETROFFEN");}
      else message("KEIN ZIEL IN REICHWEITE");
      clean();
    }
    private void clean(){int before=beasts.size();beasts.removeIf(b->b.hp<=0);coins+=(before-beasts.size())*5;}
    @Override protected void tick(float dt){
      if(mode!=PLAY)return;worldTime+=dt;attackCool-=dt;magicCool-=dt;slash=Math.max(0,slash-dt);hitFlash=Math.max(0,hitFlash-dt);banner=Math.max(0,banner-dt);
      if(storyOpen)return;
      Finger joy=null;for(Finger f:fingers.values())if(f.startX<380&&f.startY>385){joy=f;break;}
      float dx=0,dy=0;
      if(joy!=null){dx=Math.max(-1,Math.min(1,(joy.x-joy.startX)/84));dy=Math.max(-1,Math.min(1,(joy.y-joy.startY)/84));}
      float len=(float)Math.hypot(dx,dy);if(len>1){dx/=len;dy/=len;}
      float vel=(175+speed*18)*dt;
      if(!blocked(x+dx*vel+Math.signum(dx)*19,y))x+=dx*vel;
      if(!blocked(x,y+dy*vel+Math.signum(dy)*19))y+=dy*vel;
      if(held(1126,593,100))sword();
      if(held(920,609,78))spell();
      mana=Math.min(100,mana+dt*7);
      for(Beast b:beasts){
        b.phase+=dt;b.cd-=dt;
        float diffx=x-b.x,diffy=y-b.y;double dist=Math.hypot(diffx,diffy);
        if(dist<350&&dist>43){float step=dt*(29+chapter*3+(b.type==3?10:0));
          float bx=b.x+(float)(diffx/dist*step),by=b.y+(float)(diffy/dist*step);
          if(!blocked(bx+Math.signum(diffx)*18,b.y))b.x=bx;
          if(!blocked(b.x,by+Math.signum(diffy)*18))b.y=by;
        }
        if(dist<64&&b.cd<0){health-=Math.max(2,12+chapter-armor*2);b.cd=1.1f;hitFlash=.35f;
          if(health<=0){health=0;mode=DOWN;return;}}
      }
      for(Relic r:relics)if(!r.taken&&Math.hypot(x-r.x,y-r.y)<56){r.taken=true;shards++;coins+=10;message("FUNKE GEFUNDEN  "+shards+" / 3");}
      portalActive=shards==3&&beasts.isEmpty();
      if(portalActive&&Math.hypot(x-37*TILE,y-12.5f*TILE)<64){finish();}
      cameraX=Math.max(0,Math.min((COLS*TILE)-W,x-W*.5f));
      cameraY=Math.max(0,Math.min((ROWS*TILE)-H,y-H*.5f));
    }
    private void finish(){
      mode=chapter==CHAPTERS?FINISHED:VICTORY;
      unlocked=Math.max(unlocked,Math.min(CHAPTERS,chapter+1));
      save.edit().putInt("unlocked",unlocked).putInt("coins",coins).apply();
    }
    private void upgrade(int type){
      if(type==0)power++;else if(type==1)armor++;else speed++;
      save.edit().putInt("power",power).putInt("armor",armor).putInt("speed",speed).apply();start(Math.min(CHAPTERS,chapter+1));
    }
    @Override protected void down(float px,float py,int id){
      if(mode==MENU){
        if(in(px,py,365,285,550,100))start(unlocked);
        else if(in(px,py,364,428,560,140)){
          int ix=(int)((px-373)/70);int chapterID=ix+1;
          if(chapterID>=1&&chapterID<=unlocked&&chapterID<=CHAPTERS)start(chapterID);
        }
      }else if(mode==PLAY){
        if(in(px,py,1170,0,110,95)){mode=PAUSE;return;}
        if(storyOpen){storyOpen=false;return;}
      }else if(mode==PAUSE){if(py<480)mode=PLAY;else mode=MENU;}
      else if(mode==DOWN){if(py<470)start(chapter);else mode=MENU;}
      else if(mode==VICTORY){if(py>=360&&py<490){int type=(int)((px-310)/225);upgrade(Math.max(0,Math.min(2,type)));}else if(py>500)mode=MENU;}
      else if(mode==FINISHED){mode=MENU;}
    }
    @Override protected void scene(Canvas c){
      if(mode==MENU){menu(c);return;}
      drawWorld(c);
      if(mode==PLAY){hud(c);if(storyOpen)story(c);return;}
      overlay(c);
    }
    private void drawWorld(Canvas c){
      gradient(c,0,0,W,H,0xff0d1520,BG[chapter-1]);
      c.save();c.translate(-cameraX,-cameraY);
      int left=Math.max(0,(int)(cameraX/TILE)-1),right=Math.min(COLS,(int)((cameraX+W)/TILE)+2);
      int top=Math.max(0,(int)(cameraY/TILE)-1),bottom=Math.min(ROWS,(int)((cameraY+H)/TILE)+2);
      for(int i=left;i<right;i++)for(int j=top;j<bottom;j++){
        float tx=i*TILE,ty=j*TILE;
        int base=BG[chapter-1];int shade=(i*17+j*31)%3;
        rect(c,tx,ty,TILE+1,TILE+1,shade==0?0xff152c2d:shade==1?0xff20343c:0xff243443);
        if((i+j*9)%5==0){circle(c,tx+15,ty+31,4,0x554be5be);circle(c,tx+25,ty+34,2,0x88b2d4ca);}
        if(terrain[i][j]==1){
          rounded(c,tx+5,ty+5,TILE-10,TILE-10,12,chapter%2==0?0xff4d4e65:0xff3d635c);
          rounded(c,tx+7,ty+8,TILE-14,16,6,0x6692b8a2);
          circle(c,tx+31,ty+29,11,ACCENT[chapter-1]);
          circle(c,tx+31,ty+29,6,0xff324450);
        }else if((i*7+j*11)%13==0){
          line(c,tx+11,ty+42,tx+22,ty+31,0x555fbd9f,2);line(c,tx+22,ty+31,tx+36,ty+38,0x557bcab7,2);
        }
      }
      // Glowing progression gate on eastern border
      float ex=37*TILE,ey=12.5f*TILE;
      glow(c,ex,ey,portalActive?114:68,portalActive?0x558dfbcc:0x557b79a4);
      rounded(c,ex-33,ey-60,66,120,22,portalActive?0xff74bdb8:0xff303f5c);
      stroke(c,ex-32,ey-59,64,118,22,portalActive?0xffd4ffec:0xff73839c,5);
      for(Relic r:relics)if(!r.taken){
        glow(c,r.x,r.y,65,0x55ffdb7f);
        c.save();c.rotate((worldTime*48)%360,r.x,r.y);
        Path diamond=new Path();diamond.moveTo(r.x,r.y-24);diamond.lineTo(r.x+18,r.y);diamond.lineTo(r.x,r.y+24);diamond.lineTo(r.x-18,r.y);diamond.close();
        color(0xffffe59b);c.drawPath(diamond,p);c.restore();
      }
      for(Beast b:beasts){
        float by=b.y+(float)Math.sin(b.phase*4)*4;
        glow(c,b.x,by,48,b.type==3?0x55ff9faf:0x55ff704e);
        circle(c,b.x,b.y+15,b.type==3?36:23,0x7704071a);
        circle(c,b.x,by,b.type==3?40:25,b.type==3?0xffcb607f:b.type==1?0xffc08cf7:0xffee986c);
        circle(c,b.x-8,by-3,4,0xff09162b);circle(c,b.x+8,by-3,4,0xff09162b);
        line(c,b.x-7,by+12,b.x+7,by+12,0xff502947,3);
        if(b.type==3)drawBar(c,b.x-45,by-58,90,8,b.hp/(250+chapter*35),0xff472f41,0xffff8fa9);
      }
      glow(c,x,y,66,0x555ef7ef);
      circle(c,x,y+13,26,0x66060920);
      rounded(c,x-17,y-28,34,47,14,0xfff1d6b1);
      rounded(c,x-20,y-13,40,40,13,0xff408baf);
      circle(c,x,y-30,20,0xffeec6a1);
      line(c,x-13,y-42,x+13,y-42,0xff364779,9);
      circle(c,x+6,y-32,3,0xff192c40);
      line(c,x+14,y-10,x+34,y+14,0xfff2d9a9,8);
      if(slash>0){glow(c,x+47,y,80,0x66d4fff1);stroke(c,x-26,y-65,136,130,64,0xffedffff,5);}
      c.restore();
      if(hitFlash>0)rect(c,0,0,W,H,0x44ff587d);
    }
    private void hud(Canvas c){
      rounded(c,17,14,520,116,20,0xee101a2c);
      text(c,"EMBER ATLAS",37,47,28,0xffffedd0,true);
      text(c,"KAPITEL "+chapter+": "+NAMES[chapter-1],37,79,19,ACCENT[chapter-1],true);
      drawBar(c,37,93,192,11,health/(100+armor*12),0xff572f49,0xffe7899b);
      drawBar(c,248,93,162,11,mana/100,0xff283849,0xff82cfff);
      text(c,"✦ "+shards+"/3",553,49,25,0xffffd99b,true);
      text(c,"Gegner "+beasts.size()+" · Münzen "+coins,551,79,19,0xffd8e8fb,true);
      chip(c,"PAUSE Ⅱ",1130,14,132,55,0xff587e94);
      // Left movement orb and separate attack/spell buttons
      circle(c,141,589,105,0x66101c34);stroke(c,36,484,210,210,105,0x997b9ed2,3);
      Finger f=null;for(Finger q:fingers.values())if(q.startX<380&&q.startY>385){f=q;break;}
      circle(c,f==null?141:141+Math.max(-54,Math.min(54,f.x-f.startX)),f==null?589:589+Math.max(-54,Math.min(54,f.y-f.startY)),42,0xffa0c6dd);
      circle(c,920,608,78,held(920,609,78)?0xff91e9ff:0xff497da6);
      center(c,"MAGIE",920,617,19,0xffffffff,true);
      circle(c,1126,590,92,held(1126,593,100)?0xffffdfbd:0xffdca978);
      center(c,"ANGRIFF",1126,599,21,0xff263044,true);
      if(banner>0){rounded(c,449,141,379,51,14,0xdd142538);center(c,message,640,176,20,0xfffce9bc,true);}
      if(portalActive)center(c,"DAS TOR IST OFFEN · NACH OSTEN!",648,239,22,0xffb3ffe4,true);
    }
    private void story(Canvas c){
      rect(c,0,0,W,H,0x99081018);panel(c,245,177,790,363);
      center(c,"KAPITEL "+chapter+" · "+NAMES[chapter-1],640,250,33,0xffffd6a7,true);
      center(c,STORIES[chapter-1],640,315,19,0xffd1e0f4,false);
      center(c,"Sammle 3 Funken, besiege die Gegner und finde das Tor.",640,369,20,0xffb7dfc8,false);
      center(c,"BEWEGUNG LINKS · ANGRIFF / MAGIE RECHTS",640,420,16,0xffc2b4fa,true);
      center(c,"ZUM BEGINNEN BERÜHREN",640,480,19,0xffe8c79b,true);
    }
    private void menu(Canvas c){
      gradient(c,0,0,W,H,0xff111c2a,0xff3c2447);starfield(c,60,225,1);
      glow(c,650,330,335,0x447fbdcc);glow(c,270,330,290,0x337eeeb0);
      center(c,"U E N K E R L A B S  ·  E I N  A B E N T E U E R",640,95,20,0xffb9eacd,true);
      center(c,"EMBER ATLAS",640,195,72,0xfff9e4c6,true);
      center(c,"ACHT KAPITEL · DREI LICHTFUNKEN JE KAPITEL",640,243,18,0xffc7cade,true);
      panel(c,339,283,602,104);center(c,"ABENTEUER FORTSETZEN",640,326,26,0xffffe7c5,true);
      center(c,"KAPITEL "+unlocked+" · "+NAMES[unlocked-1],640,355,17,0xff9ee9cf,false);
      center(c,"KAPITELWÄHLER",640,418,19,0xffe9c8ff,true);
      for(int i=0;i<CHAPTERS;i++){
        float xx=365+i*69;
        rounded(c,xx,438,61,76,13,i+1<=unlocked?0xff597c78:0xff25324a);
        center(c,String.valueOf(i+1),xx+31,481,26,i+1<=unlocked?Color.WHITE:0xff677896,true);
      }
      center(c,"EIGENE GESCHICHTE · FIGUREN-AUFSTIEG · OFFLINE-SPIELSTAND",640,632,17,0xffb7c1d6,false);
    }
    private void overlay(Canvas c){
      rect(c,0,0,W,H,0xaa090d1a);panel(c,300,163,680,429);
      center(c,mode==PAUSE?"PAUSE":mode==DOWN?"DU BIST GEFALLEN":mode==VICTORY?"KAPITEL VOLLENDET":"DAS LICHT KEHRT ZURÜCK",640,248,39,0xfff2e4ce,true);
      if(mode==VICTORY){
        center(c,"Wähle einen permanenten Ausbau für dein Abenteuer:",640,318,19,0xffced9ec,false);
        String[] upgrades={"KLINGE +1","SCHUTZ +1","TEMPO +1"};
        for(int i=0;i<3;i++){float xx=319+i*225;rounded(c,xx,365,210,110,13,0xff395e6e);center(c,upgrades[i],xx+105,430,21,0xffffffff,true);}
        center(c,"Alternativ unten tippen für das Hauptmenü",640,533,16,0xffaeb7d2,false);
      } else {
        center(c,mode==PAUSE?"Das Abenteuer kann fortgesetzt werden":mode==DOWN?"Wiederhole das aktuelle Kapitel":"Acht Kapitel abgeschlossen · Die Welt ist gerettet",640,317,20,0xffc6d0e3,false);
        rounded(c,376,375,528,93,15,0xff8bc5bd);
        center(c,mode==PAUSE?"WEITERSPIELEN":mode==DOWN?"KAPITEL NEUSTART":"ZUM HAUPTMENÜ",640,431,26,0xff132c37,true);
        if(mode!=FINISHED)center(c,"HAUPTMENÜ",640,525,20,0xffe2ccff,true);
      }
    }
  }
}
