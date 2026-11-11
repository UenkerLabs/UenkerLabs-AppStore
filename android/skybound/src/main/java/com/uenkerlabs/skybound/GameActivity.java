package com.uenkerlabs.skybound;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import com.uenkerlabs.gamekit.GameHost;
import com.uenkerlabs.gamekit.FrameGame;
import java.util.*;

public final class GameActivity extends GameHost {
    @Override protected FrameGame makeGame(){return new Shift(this);}

    /** Original 12-level side-scroller with physics, checkpoints, power-ups and saved stars. */
    static final class Shift extends FrameGame implements GameHost.BackHandler {
        static final int LEVELS=12;
        static final int MENU=0,PLAY=1,PAUSE=2,LOST=3,VICTORY=4,COMPLETE=5;
        static final String[] WORLDS={"Wolkengärten","Sternenwerft","Kristallhimmel"};
        static final int[] SKY_TOP={0xff223c61,0xff24274c,0xff4b2860};
        static final int[] SKY_BOTTOM={0xff85cdd5,0xffd399ba,0xff9d7ddc};
        static final int[] GROUND={0xff416e78,0xff6e577b,0xff594b83};
        static final class Ledge{float x,y,w;boolean moving;Ledge(float x,float y,float w){this.x=x;this.y=y;this.w=w;}}
        static final class Orb{float x,y;boolean taken;Orb(float x,float y){this.x=x;this.y=y;}}
        static final class Foe{float x,y,base,phase;boolean alive=true;Foe(float x,float y){this.x=x;this.y=y;this.base=x;}}
        static final class Spark{float x,y,vx,vy,ttl;int col;Spark(float x,float y,float vx,float vy,float ttl,int col){this.x=x;this.y=y;this.vx=vx;this.vy=vy;this.ttl=ttl;this.col=col;}}
        private final ArrayList<Ledge> ledges=new ArrayList<>();
        private final ArrayList<Orb> orbs=new ArrayList<>();
        private final ArrayList<Foe> foes=new ArrayList<>();
        private final ArrayList<Spark> sparks=new ArrayList<>();
        private float worldWidth,px=84,py=530,vx=0,vy=0,camera=0,runTime=0,shake=0,dashTime=0,invuln=0;
        private int mode=MENU,level=1,unlocked,hearts=3,collected=0,allOrbs=0,jumps=0,checkpoint=0,bestStars=0;
        private boolean onGround=true,firstTip=true;
        private String notice="";private float noticeTime=0;
        Shift(Activity a){super(a,"skybound_shift");unlocked=Math.max(1,save.getInt("unlocked",1));}
        @Override public void back(){if(mode==PLAY)mode=PAUSE;else if(mode==PAUSE)mode=PLAY;else mode=MENU;invalidate();}
        private boolean pitAt(float x){
            if(x<240||x>worldWidth-240)return false;
            int chunk=(int)(x/480);
            float local=x-chunk*480;
            return chunk%3!=0&&local>306&&local<416; // <110px pits, reachable by one jump
        }
        private void stage(int dest){
            level=dest;worldWidth=2600+level*145;mode=PLAY;
            px=90;py=535;vx=vy=0;camera=0;hearts=3;jumps=0;onGround=true;checkpoint=0;
            dashTime=0;invuln=0;runTime=0;collected=0;orbs.clear();foes.clear();ledges.clear();sparks.clear();
            for(int i=0;i<17+level;i++){
                float x=290+i*190f;float y=365-(i%3)*44;
                if(x<worldWidth-180)ledges.add(new Ledge(x,y,132));
            }
            for(int i=0;i<12+level*2;i++){
                float x=160+i*((worldWidth-360f)/(11+level*2f));
                float y=(i%3==1)?330-(i%2)*30:485;
                orbs.add(new Orb(x,y));
            }
            allOrbs=orbs.size();
            for(int i=0;i<4+level;i++){
                float x=390+i*((worldWidth-660f)/(3+level));
                if(!pitAt(x))foes.add(new Foe(x,522));
            }
            bestStars=save.getInt("stars_"+level,0);
            firstTip=level==1&&!save.getBoolean("tutorial",false);
        }
        private void alert(String msg){notice=msg;noticeTime=1.7f;}
        private void dust(float x,float y,int n,int col){for(int i=0;i<n;i++){
            float angle=(float)(i*2.39),vel=30+i*13;
            sparks.add(new Spark(x,y,(float)Math.cos(angle)*vel,(float)Math.sin(angle)*vel,1f,col));}}
        private void jump(){
            if(mode!=PLAY||firstTip)return;
            int max=level>=5?2:1;
            if(jumps<max){vy=-660-(level>=5&&jumps==1?25:0);jumps++;onGround=false;dust(px,py,6,0xffdbf4ff);}
        }
        private void dash(){if(level<9||dashTime>0)return;dashTime=.19f;vx=700*(vx<0?-1:1);dust(px,py,11,0xffe8c8ff);}
        private void hurt(){
            if(invuln>0)return;
            hearts--;invuln=1.6f;shake=.24f;dust(px,py,15,0xffffb1a1);
            if(hearts<=0){mode=LOST;return;}
            px=checkpoint==0?90:checkpoint;py=505;vx=vy=0;alert("CHECKPOINT · "+hearts+" HERZEN");
        }
        @Override protected void tick(float dt){
            if(mode!=PLAY)return;
            runTime+=dt;shake=Math.max(0,shake-dt);invuln=Math.max(0,invuln-dt);noticeTime=Math.max(0,noticeTime-dt);
            if(firstTip)return;
            float direction=(held(124,603,90)?-1:0)+(held(279,603,90)?1:0);
            vx+=direction*dt*1900;
            if(direction==0)vx*=(float)Math.pow(.06,dt*6);
            if(dashTime>0){dashTime-=dt;}else vx=Math.max(-335,Math.min(335,vx));
            px+=vx*dt;px=Math.max(30,Math.min(worldWidth-30,px));
            float prevFeet=py;
            vy=Math.min(1050,vy+1550*dt);py+=vy*dt;
            boolean landing=false;
            if(vy>=0){
                float ground=554;
                if(!pitAt(px)&&prevFeet<=ground&&py>=ground){py=ground;vy=0;landing=true;}
                for(Ledge l:ledges)if(px>l.x-22&&px<l.x+l.w+22&&prevFeet<=l.y&&py>=l.y){
                    py=l.y;vy=0;landing=true;break;
                }
            }
            if(landing){if(!onGround)dust(px,py,7,0xffdcecef);onGround=true;jumps=0;}else onGround=false;
            if(py>760){hurt();if(mode==PLAY){py=505;vx=vy=0;}}
            if(px>worldWidth/2&&checkpoint==0){checkpoint=(int)(worldWidth/2);alert("CHECKPOINT AKTIV");}
            for(Orb o:orbs)if(!o.taken&&Math.hypot(px-o.x,py-35-o.y)<45){
                o.taken=true;collected++;dust(o.x,o.y,10,0xffffeb94);
            }
            for(Foe f:foes){if(!f.alive)continue;
                f.phase+=dt;f.x=f.base+(float)Math.sin(f.phase*1.75)*49;
                if(Math.abs(px-f.x)<42&&Math.abs(py-38-f.y)<48){
                    if(vy>120&&prevFeet<=f.y-17){f.alive=false;vy=-390;dust(f.x,f.y,15,0xffffbf9c);}
                    else hurt();
                }
            }
            for(Iterator<Spark> it=sparks.iterator();it.hasNext();){Spark s=it.next();s.x+=s.vx*dt;s.y+=s.vy*dt;s.vy+=110*dt;s.ttl-=dt;if(s.ttl<=0)it.remove();}
            if(px>=worldWidth-105&&Math.abs(py-554)<100){finish();}
            camera=Math.max(0,Math.min(worldWidth-W,px-W*.33f));
        }
        private void finish(){
            mode=level==LEVELS?COMPLETE:VICTORY;
            int stars=collected>=allOrbs*.78f&&hearts==3?3:collected>=allOrbs*.5f?2:1;
            save.edit().putInt("stars_"+level,Math.max(stars,bestStars))
                .putInt("unlocked",Math.max(unlocked,Math.min(LEVELS,level+1))).putBoolean("tutorial",true).apply();
            unlocked=Math.max(unlocked,Math.min(LEVELS,level+1));bestStars=stars;
        }
        @Override protected void down(float x,float y,int pointer){
            if(mode==MENU){
                if(in(x,y,365,269,550,95))stage(unlocked);
                else if(in(x,y,310,409,650,173)){
                    int column=(int)((x-316)/107),row=(int)((y-414)/82),id=row*6+column+1;
                    if(id>=1&&id<=unlocked&&id<=LEVELS)stage(id);
                }
            } else if(mode==PLAY){
                if(in(x,y,1170,0,110,90)){mode=PAUSE;return;}
                if(firstTip){firstTip=false;save.edit().putBoolean("tutorial",true).apply();return;}
                if(in(x,y,1023,490,257,230))jump();
                if(in(x,y,843,520,162,195))dash();
            } else if(mode==PAUSE){if(y<470)mode=PLAY;else mode=MENU;}
            else if(mode==LOST){if(y<470)stage(level);else mode=MENU;}
            else if(mode==VICTORY){if(y<470)stage(level+1);else mode=MENU;}
            else if(mode==COMPLETE){mode=MENU;}
        }
        @Override protected void scene(Canvas c){
            if(mode==MENU){menu(c);return;}
            world(c);
            if(mode==PLAY){hud(c);if(firstTip)intro(c);return;}
            overlay(c);
        }
        private void world(Canvas c){
            int world=(level-1)/4;
            gradient(c,0,0,W,H,SKY_TOP[world],SKY_BOTTOM[world]);
            for(int i=0;i<40;i++){
                int xx=(i*131+71)%1280,yy=(i*63)%280;
                circle(c,(xx-camera*.1f+12800)%1280,yy,2+(i%3),0x99fff7ef);
            }
            // Parallax silhouettes
            for(int layer=0;layer<3;layer++){
                float parallax=.13f+layer*.13f;
                int base=layer==0?0x448bb0bc:layer==1?0x77777fa0:0xaa334e7b;
                for(int i=-3;i<12;i++){
                    float tx=i*226-camera*parallax;
                    Path m=new Path();m.moveTo(tx,600);m.lineTo(tx+108,287+layer*73+(i%3)*29);
                    m.lineTo(tx+226,600);m.close();color(base);c.drawPath(m,p);
                }
            }
            c.save();if(shake>0)c.translate((float)Math.sin(time()*79)*6,0);c.translate(-camera,0);
            // Ground segments and elegant beveled terrain
            for(int i=0;i<(worldWidth/40)+1;i++){
                float X=i*40;
                if(pitAt(X+20))continue;
                rect(c,X,555,41,165,GROUND[world]);
                rect(c,X,554,41,13,world==0?0xffaeecc7:world==1?0xffffc2be:0xffd6b6ff);
                rect(c,X+6,583+(i%4)*14,16,4,0x66483364);
            }
            for(Ledge l:ledges){
                rounded(c,l.x,l.y,l.w,24,9,0xff1f3855);rounded(c,l.x,l.y,l.w,11,5,0xffb9ded7);
                line(c,l.x+18,l.y+19,l.x+l.w-18,l.y+19,0x557bd9e8,2);
            }
            for(Orb orb:orbs)if(!orb.taken){
                glow(c,orb.x,orb.y,38,0x66ffe3a1);
                Path gem=new Path();float m=(float)Math.sin(time()*3+orb.x)*3;
                gem.moveTo(orb.x,orb.y-17+m);gem.lineTo(orb.x+13,orb.y+m);gem.lineTo(orb.x,orb.y+18+m);
                gem.lineTo(orb.x-13,orb.y+m);gem.close();color(0xffffe5a0);c.drawPath(gem,p);
                circle(c,orb.x,orb.y+m,4,0xffffffff);
            }
            for(Foe f:foes)if(f.alive){
                float Y=f.y+(float)Math.sin(f.phase*5)*5;
                glow(c,f.x,Y,46,0x55ffaab0);
                rounded(c,f.x-24,Y-19,48,40,17,0xffc9668f);
                rounded(c,f.x-13,Y-22,26,14,7,0xffe9a7b9);
                circle(c,f.x-8,Y-2,4,0xff202746);circle(c,f.x+8,Y-2,4,0xff202746);
            }
            // Checkpoint lantern
            float cp=worldWidth/2;
            rect(c,cp-4,420,8,133,0xffe7d0b4);glow(c,cp,415,63,checkpoint>0?0x88abfff3:0x449baee7);
            circle(c,cp,415,20,checkpoint>0?0xff92ffe8:0xff7392b2);
            // Level exit gate
            float gx=worldWidth-95;glow(c,gx,467,102,0x448fffd3);
            rounded(c,gx-34,422,68,132,23,0xff354f7a);
            stroke(c,gx-33,423,66,130,22,0xffb4ffe6,5);
            line(c,gx,434,gx,539,0xffc4fff0,5);
            // Player character, original masked sky traveller
            if(invuln<=0||((int)(time()*12))%2==0){
                glow(c,px,py-37,53,0x5576f6e8);
                circle(c,px,py-2,25,0x660d172b);
                rounded(c,px-21,py-58,42,45,17,0xffe9b982);
                rounded(c,px-26,py-46,52,48,15,0xff365d91);
                Path cape=new Path();cape.moveTo(px-16,py-30);cape.lineTo(px-29-vx*.02f,py+2);cape.lineTo(px+7,py-5);cape.close();color(0xffffcf8d);c.drawPath(cape,p);
                circle(c,px,py-67,22,0xffffdeb1);
                rounded(c,px-15,py-72,30,11,6,0xff34476d);
                circle(c,px+7,py-65,3,0xff152c3e);
                rect(c,px-18,py-8,14,11,0xff223855);rect(c,px+8,py-8,14,11,0xff223855);
            }
            for(Spark s:sparks){circle(c,s.x,s.y,Math.max(1,s.ttl*5),s.col);}
            c.restore();
        }
        private void hud(Canvas c){
            rounded(c,18,17,520,109,20,0xd914213d);
            text(c,"SKYBOUND SHIFT",39,54,26,0xfff2fbff,true);
            text(c,"WELT "+((level-1)/4+1)+" · LEVEL "+level+" / "+LEVELS,39,88,18,0xffc6d5ea,true);
            text(c,"❤ "+hearts+"    ✦ "+collected+" / "+allOrbs,365,89,20,0xffffe8a2,true);
            chip(c,"PAUSE Ⅱ",1130,17,132,54,0xff9aacc8);
            drawBar(c,38,105,445,7,px/worldWidth,0xff283c57,0xff8ff5e0);
            circle(c,127,608,84,held(124,603,90)?0xffa8d9e8:0xaa405d7d);
            center(c,"◀",127,619,40,0xffffffff,true);
            circle(c,279,608,84,held(279,603,90)?0xffa8d9e8:0xaa405d7d);
            center(c,"▶",279,619,40,0xffffffff,true);
            if(level>=9){circle(c,919,609,71,0xff855eaf);center(c,"DASH",919,620,18,0xffffffff,true);}
            circle(c,1138,601,95,0xff9ee6c2);
            center(c,level>=5?"DOPPEL-SPRUNG":"SPRUNG",1138,612,17,0xff153c40,true);
            if(noticeTime>0){rounded(c,464,137,353,54,14,0xdd25324a);center(c,notice,641,173,19,0xffdcfae5,true);}
        }
        private void intro(Canvas c){
            rect(c,0,0,W,H,0x9910192a);panel(c,260,169,760,368);
            center(c,"WILLKOMMEN IN DEN WOLKENGÄRTEN",640,244,31,0xfff3edcc,true);
            center(c,"Zwölf Level · drei Welten · Sammelkristalle · Checkpoints",640,301,20,0xffe5f0f8,false);
            center(c,"LINKS / RECHTS BEWEGEN · SPRUNG · SPÄTER DASH",640,362,19,0xff9ff4e6,true);
            center(c,"Der Ausgang leuchtet am Ende jeder Strecke",640,412,18,0xffc8cdeb,false);
            center(c,"BERÜHREN, UM ZU STARTEN",640,482,21,0xffffe0a5,true);
        }
        private void menu(Canvas c){
            gradient(c,0,0,W,H,0xff1e274d,0xff585381);starfield(c,80,112,2);
            glow(c,340,330,320,0x558de1ed);glow(c,900,320,330,0x5589b2fa);
            center(c,"U E N K E R L A B S   G A M E S",640,85,20,0xffc9f8ee,true);
            center(c,"SKYBOUND SHIFT",640,178,71,0xfff2fcff,true);
            center(c,"12 LEVEL · 3 WELTEN · EIN WEG DURCH DIE WOLKEN",640,233,18,0xffd2d9f3,true);
            panel(c,348,267,584,100);center(c,"SPIEL FORTSETZEN · LEVEL "+unlocked,640,331,28,0xffbbfff2,true);
            center(c,"LEVEL-AUSWAHL",640,405,21,0xfff9d6fc,true);
            for(int i=0;i<LEVELS;i++){
                int col=i%6,row=i/6;float xx=314+col*108,yy=415+row*81;
                rounded(c,xx,yy,99,69,12,i+1<=unlocked?0xff5a779d:0xff2b375b);
                center(c,String.valueOf(i+1),xx+50,yy+42,25,i+1<=unlocked?0xfff0faff:0xff8392ac,true);
                if(i+1<=unlocked)for(int z=0;z<save.getInt("stars_"+(i+1),0);z++)circle(c,xx+34+z*17,yy+59,4,0xffffe2a1);
            }
            center(c,"HANDGEMACHTER RETRO-MODERNER PLATFORMER · LOKALE SPIELSTÄNDE",640,642,17,0xffc7d5e9,false);
        }
        private void overlay(Canvas c){
            rect(c,0,0,W,H,0xb00f1628);panel(c,300,160,680,431);
            String title=mode==PAUSE?"PAUSE":mode==LOST?"NEUER ANLAUF":mode==VICTORY?"LEVEL GESCHAFFT":"DIE HIMMELSREISE ENDET";
            center(c,title,640,252,40,0xfff2faff,true);
            center(c,mode==VICTORY?"Kristalle "+collected+" / "+allOrbs+" · Sterne "+bestStars:
                mode==COMPLETE?"Alle zwölf Level abgeschlossen!":mode==PAUSE?"Dein Fortschritt wird gespeichert.":"Versuche das Level erneut.",640,320,21,0xffc6d9eb,false);
            rounded(c,371,370,540,97,16,0xff94dfe2);
            center(c,mode==PAUSE?"WEITERSPIELEN":mode==LOST?"ERNEUT VERSUCHEN":mode==VICTORY?"NÄCHSTES LEVEL":"ZUM MENÜ",640,429,27,0xff1a2c50,true);
            if(mode!=COMPLETE)center(c,"HAUPTMENÜ",640,528,20,0xffd9caff,true);
        }
    }
}
