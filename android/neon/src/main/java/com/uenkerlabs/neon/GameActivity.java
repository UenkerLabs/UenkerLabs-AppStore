package com.uenkerlabs.neon;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.app.Activity;
import com.uenkerlabs.gamekit.GameHost;
import com.uenkerlabs.gamekit.FrameGame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class GameActivity extends GameHost {
    @Override protected FrameGame makeGame(){return new Arena(this);}

    /** Original neon-lit raycast arena game, eight chapters with checkpoints & persistent progress. */
    static final class Arena extends FrameGame implements GameHost.BackHandler {
        private static final int SIZE=19, LAST=8, RAYS=256;
        private static final float FOV=1.12f;
        private final int[][] walls=new int[SIZE][SIZE];
        private final float[] depth=new float[RAYS];
        private final ArrayList<Drone> drones=new ArrayList<>();
        private final ArrayList<Pickup> pickups=new ArrayList<>();
        private final int[] palette={0xff2edbd0,0xffa182ff,0xffffba6e,0xff74b8ff};
        private float px=2.5f,py=2.5f,ang=.82f,hp=100,fireTimer=0,damageFlash=0,muzzle=0,stepTime=0;
        private float lookX=0,lookY=0;private int lookFinger=-1;
        private int level=1,mode=0,score=0,ammo=15,clip=15,totalKills=0,kills=0;
        private int unlocked,menuPage=0;
        private boolean tutorial=true;
        private String notification="";
        private float notificationTimer=0;
        private static final int MENU=0,PLAY=1,PAUSE=2,FAILED=3,VICTORY=4,COMPLETE=5;
        private static class Drone {float x,y,phase,cooldown;int hp,type;Drone(float x,float y,int health,int type){this.x=x;this.y=y;this.hp=health;this.type=type;}}
        private static class Pickup {float x,y;int type;Pickup(float x,float y,int type){this.x=x;this.y=y;this.type=type;}}
        Arena(Activity a){super(a,"neon_bastion");unlocked=Math.max(1,save.getInt("unlocked",1));score=save.getInt("score",0);}
        @Override public void back(){if(mode==PLAY)mode=PAUSE;else if(mode==PAUSE)mode=PLAY;else mode=MENU;invalidate();}
        private void start(int stage){
            level=stage;mode=PLAY;hp=100;ammo=15;clip=15;kills=0;damageFlash=0;
            px=2.5f;py=2.5f;ang=.78f;fireTimer=0;drones.clear();pickups.clear();
            for(int x=0;x<SIZE;x++)for(int y=0;y<SIZE;y++){
                int wall=(x==0||y==0||x==SIZE-1||y==SIZE-1)?1:0;
                if(x>3&&x<16&&y>3&&y<16){
                    if((x==5||x==10||x==14)&&(y==5||y==9||y==13))wall=1;
                    if(stage%2==0&&x==8&&y>=5&&y<=13&&y!=7&&y!=11)wall=1;
                    if(stage%3==0&&y==10&&x>=8&&x<=14&&x!=9&&x!=13)wall=1;
                    if(stage>=5&&(x==12&&y==7||x==6&&y==12))wall=1;
                }
                if(x<4||y<4||x>15||y>15)wall=(x==0||y==0||x==SIZE-1||y==SIZE-1)?1:0;
                walls[x][y]=wall;
            }
            int num=3+stage*2;
            for(int i=0;i<num;i++){
                float ex=4.5f+(i%4)*3.1f,ey=4.8f+((i/4)%4)*3.1f;
                if(!solid(ex,ey)&&Math.hypot(ex-px,ey-py)>2.5)drones.add(new Drone(ex,ey,(stage%4==0&&i==num-1)?8:1+stage/3,i%3));
            }
            for(int i=0;i<4;i++){
                float ax=3.5f+i*3.4f,ay=7.5f+(i%2)*6f;
                if(!solid(ax,ay))pickups.add(new Pickup(ax,ay,i%2));
            }
            notification="MISSION "+stage+": TORE SICHERN";notificationTimer=3.5f;
            tutorial=stage==1&&!save.getBoolean("tutorial_seen",false);
        }
        private boolean solid(float x,float y){int cx=(int)x,cy=(int)y;return cx<0||cy<0||cx>=SIZE||cy>=SIZE||walls[cx][cy]!=0;}
        private void walk(float dx,float dy){float rr=.21f;
            float nx=px+dx,ny=py+dy;
            if(!solid(nx+Math.signum(dx)*rr,py+rr)&&!solid(nx+Math.signum(dx)*rr,py-rr))px=nx;
            if(!solid(px+rr,ny+Math.signum(dy)*rr)&&!solid(px-rr,ny+Math.signum(dy)*rr))py=ny;
        }
        @Override protected void tick(float dt){
            if(mode!=PLAY)return;
            stepTime+=dt;fireTimer-=dt;damageFlash=Math.max(0,damageFlash-dt);muzzle=Math.max(0,muzzle-dt);notificationTimer=Math.max(0,notificationTimer-dt);
            float fw=0,side=0;Finger joy=null;
            for(Finger f:fingers.values())if(f.startX<360&&f.startY>360){joy=f;break;}
            if(joy!=null){fw=clamp((joy.startY-joy.y)/88);side=clamp((joy.x-joy.startX)/88);}
            float speed=dt*3.15f;
            walk((float)((Math.cos(ang)*fw-Math.sin(ang)*side)*speed),(float)((Math.sin(ang)*fw+Math.cos(ang)*side)*speed));
            if(held(1138,600,94)&&fireTimer<=0){shoot();fireTimer=.21f;}
            for(Drone d:drones){
                if(d.hp<=0)continue;
                d.phase+=dt;d.cooldown-=dt;
                float dx=px-d.x,dy=py-d.y;double dist=Math.hypot(dx,dy);
                if(dist<9&&dist>.78&&sight(d.x,d.y,px,py)){
                    float s=dt*(.54f+level*.055f);
                    float nx=d.x+(float)(dx/dist*s),ny=d.y+(float)(dy/dist*s);
                    if(!solid(nx+.24f,d.y)&&!solid(nx-.24f,d.y))d.x=nx;
                    if(!solid(d.x,ny+.24f)&&!solid(d.x,ny-.24f))d.y=ny;
                }
                if(dist<1.3&&d.cooldown<0){hp-=5+level;damageFlash=.45f;d.cooldown=.9f;
                    if(hp<=0){hp=0;mode=FAILED;break;}}
            }
            pickups.removeIf(item->{
                if(Math.hypot(px-item.x,py-item.y)>.64)return false;
                if(item.type==0){hp=Math.min(100,hp+24);note("SCHILD +24");}
                else {ammo=Math.min(50,ammo+12);note("ENERGIE +12");}
                return true;
            });
            if(drones.isEmpty()&&Math.hypot(px-(SIZE-2.5),py-(SIZE-2.5))<.9){complete();}
        }
        private float clamp(float v){return Math.max(-1,Math.min(1,v));}
        private void note(String message){notification=message;notificationTimer=1.5f;}
        private boolean sight(float x,float y,float xx,float yy){
            float len=(float)Math.hypot(xx-x,yy-y);int n=(int)(len*6);for(int i=1;i<n;i++){
                float t=i/(float)n;if(solid(x+(xx-x)*t,y+(yy-y)*t))return false;}
            return true;
        }
        private void shoot(){
            muzzle=.11f;
            if(ammo<=0){note("MUNITION LEER");return;}
            ammo--;
            Drone best=null;double nearest=100;
            for(Drone d:drones){
                float dx=d.x-px,dy=d.y-py;double dist=Math.hypot(dx,dy);double target=Math.atan2(dy,dx),delta=Math.atan2(Math.sin(target-ang),Math.cos(target-ang));
                if(dist<13&&Math.abs(delta)<Math.atan(.42/dist)+.037&&dist<nearest&&sight(px,py,d.x,d.y)){best=d;nearest=dist;}
            }
            if(best!=null){best.hp--;if(best.hp<=0){drones.remove(best);kills++;totalKills++;score+=110+10*level;save.edit().putInt("score",score).apply();note(drones.isEmpty()?"SEKTOR FREI · ZUM PORTAL!":"DROHNE DEAKTIVIERT");}}
            if(ammo==0&&pickups.isEmpty()&&!drones.isEmpty()){ammo=8;note("NOTFALL-LADUNG +8");}
        }
        private void complete(){
            mode=level==LAST?COMPLETE:VICTORY;
            unlocked=Math.max(unlocked,Math.min(LAST,level+1));
            save.edit().putInt("unlocked",unlocked).putInt("score",score).putBoolean("tutorial_seen",true).apply();
        }
        @Override protected void down(float x,float y,int id){
            if(mode==PLAY){
                if(in(x,y,1185,0,95,90)){mode=PAUSE;return;}
                if(tutorial){tutorial=false;save.edit().putBoolean("tutorial_seen",true).apply();}
                if(x>470&&x<1090&&y>90&&y<530){lookFinger=id;lookX=x;lookY=y;}
            } else if(mode==MENU){
                if(in(x,y,360,285,565,90))start(unlocked);
                else if(in(x,y,365,410,560,155)){
                    int col=(int)((x-380)/140),row=(int)((y-420)/80);int dest=row*4+col+1;
                    if(dest>=1&&dest<=unlocked&&dest<=LAST)start(dest);
                }
            }else if(mode==PAUSE){if(y<480){mode=PLAY;}else mode=MENU;}
            else if(mode==FAILED){if(y<470)start(level);else mode=MENU;}
            else if(mode==VICTORY){if(y<475)start(level+1);else mode=MENU;}
            else if(mode==COMPLETE){mode=MENU;}
        }
        @Override protected void move(float x,float y,int id){
            if(mode==PLAY&&id==lookFinger){ang+=clamp((x-lookX)/180f)*.45f;lookX=x;lookY=y;}
        }
        @Override protected void up(float x,float y,int id){if(id==lookFinger)lookFinger=-1;}
        @Override protected void scene(Canvas c){
            if(mode==MENU){menu(c);return;}
            world(c);
            if(mode==PLAY){hud(c);return;}
            overlay(c);
        }
        private void world(Canvas c){
            gradient(c,0,0,W,H/2,0xff0b152a,0xff3b285b);
            gradient(c,0,H/2,W,H/2,0xff253348,0xff080c1b);
            for(int i=0;i<44;i++){
                int x=(i*179+91)%1280,y=(i*71+31)%275;
                circle(c,x,y,1+(i%3),0x77dbe5ff);
            }
            // Real-time 3D perspective: distance-shaded ray-cast walls, independent of display pixel density.
            float proj=H*.80f;
            for(int i=0;i<RAYS;i++){
                double rayAngle=ang+Math.atan((i/(double)RAYS-.5)*2*Math.tan(FOV/2));
                float dx=(float)Math.cos(rayAngle),dy=(float)Math.sin(rayAngle);
                float distance=.03f;float rx=px,ry=py;int hit=0;
                while(distance<29){rx=px+dx*distance;ry=py+dy*distance;
                    if(solid(rx,ry)){hit=1;break;}distance+=.025f;}
                double perp=distance*Math.cos(rayAngle-ang);
                depth[i]=(float)perp;
                if(hit==0)continue;
                float height=(float)(proj/Math.max(.08,perp)),top=(H-height)/2f;
                int base=palette[(level-1)%palette.length];
                float shade=(float)Math.max(.12,1/(1+perp*.13));
                int rr=(int)(Color.red(base)*shade),gg=(int)(Color.green(base)*shade),bb=(int)(Color.blue(base)*shade);
                float X=i*W/RAYS+0.5f,CW=W/RAYS+1;
                rect(c,X,top,CW,height,Color.rgb(rr,gg,bb));
                boolean border=Math.abs((rx-(int)rx)-.5)<.47 || Math.abs((ry-(int)ry)-.5)<.47;
                if(i%8==0||!border)rect(c,X,top,CW,Math.min(4,height),argb(135,255,255,255));
                if(i%20==0)rect(c,X,top+height*.38f,CW,Math.max(2,height*.025f),argb(110,11,12,45));
            }
            drawSprite(c,SIZE-2.5f,SIZE-2.5f,0xff66edc6,drones.isEmpty()?1:3,0);
            ArrayList<Drone> sorted=new ArrayList<>(drones);
            Collections.sort(sorted,(a,b)->Double.compare(Math.hypot(b.x-px,b.y-py),Math.hypot(a.x-px,a.y-py)));
            for(Drone d:sorted)drawSprite(c,d.x,d.y,d.type==1?0xffffbf76:0xffff718f,0,d.phase);
            for(Pickup item:pickups)drawSprite(c,item.x,item.y,item.type==0?0xff8cffc3:0xff74bdff,2,time()*2);
            if(muzzle>0){glow(c,W*.5f,H*.74f,130,0x55fff6b0);circle(c,W*.5f,H*.72f,22,0xaafffcce);}
            rect(c,W/2-2,H/2-17,4,34,0xbbffffff);rect(c,W/2-17,H/2-2,34,4,0xbbffffff);
            if(damageFlash>0)rect(c,0,0,W,H,argb((int)(damageFlash*100),255,36,75));
        }
        private void drawSprite(Canvas c,float x,float y,int col,int type,float phase){
            float dx=x-px,dy=y-py;double dist=Math.hypot(dx,dy);if(dist<.25||dist>22)return;
            double diff=Math.atan2(Math.sin(Math.atan2(dy,dx)-ang),Math.cos(Math.atan2(dy,dx)-ang));
            if(Math.abs(diff)>FOV*.72)return;
            float sx=(float)(W/2+Math.tan(diff)*W/(2*Math.tan(FOV/2))),sh=(float)(H*.7/dist);
            int ray=(int)(sx/W*RAYS);if(ray<0||ray>=RAYS||depth[ray]+.20<dist)return;
            float cy=H/2+sh*.05f+(float)Math.sin(phase*4)*5;
            if(type==1){glow(c,sx,cy,sh*.58f,0x6664ffbb);rounded(c,sx-sh*.31f,cy-sh*.6f,sh*.62f,sh*1.2f,sh*.18f,col);stroke(c,sx-sh*.31f,cy-sh*.6f,sh*.62f,sh*1.2f,sh*.18f,0xffe9fff6,Math.max(1,sh*.03f));
                text(c,"➤",sx-sh*.15f,cy+sh*.1f,Math.max(10,(int)(sh*.3f)),0xff0e2635,true);return;}
            if(type==3){if(drones.isEmpty()){glow(c,sx,cy,sh*.9f,0x665cfbe0);stroke(c,sx-sh*.27f,cy-sh*.7f,sh*.54f,sh*1.4f,12,0xffa2ffe2,Math.max(1,sh*.05f));}return;}
            if(type==2){glow(c,sx,cy,sh*.4f,0x5589ffd4);rounded(c,sx-sh*.18f,cy-sh*.18f,sh*.36f,sh*.36f,5,col);return;}
            glow(c,sx,cy,sh*.48f,0x44ff466a);
            rounded(c,sx-sh*.23f,cy-sh*.32f,sh*.46f,sh*.66f,8,0xff293044);
            circle(c,sx,cy-sh*.28f,sh*.18f,col);rect(c,sx-sh*.12f,cy-sh*.31f,sh*.24f,sh*.05f,0xfffff9d6);
            line(c,sx-sh*.25f,cy+sh*.1f,sx-sh*.34f,cy+sh*.28f,col,Math.max(2,sh*.055f));
            line(c,sx+sh*.25f,cy+sh*.1f,sx+sh*.34f,cy+sh*.28f,col,Math.max(2,sh*.055f));
        }
        private void hud(Canvas c){
            rounded(c,22,16,390,86,20,0xdd10192c);
            text(c,"NEON BASTION",43,47,25,0xffecf4ff,true);
            text(c,"SEKTOR "+level+" / "+LAST,43,83,18,0xff9baef4,true);
            drawBar(c,43,102,260,9,hp/100,0xff352c43,0xff6cf4c4);
            rounded(c,434,20,360,55,18,0xdd10192c);
            center(c,"DROHNEN  "+drones.size()+"    ·    PUNKTE  "+shortNum(score),614,55,21,0xffeaf0ff,true);
            chip(c,"PAUSE Ⅱ",1130,18,134,56,0xff687f98);
            // Small map in top right
            float ox=1095,oy=110,s=7.8f;
            rounded(c,ox-12,oy-12,169,170,16,0xdd0a142c);
            for(int x=0;x<SIZE;x++)for(int y=0;y<SIZE;y++)if(walls[x][y]!=0)rect(c,ox+x*s,oy+y*s,s-1,s-1,0xff476a8a);
            circle(c,ox+px*s,oy+py*s,5,0xff7dffe5);
            for(Drone d:drones)circle(c,ox+d.x*s,oy+d.y*s,3,0xfffe708b);
            circle(c,ox+(SIZE-2.5f)*s,oy+(SIZE-2.5f)*s,5,0xffe7c278);
            // joystick: drag circle bottom left
            circle(c,148,590,106,0x55132848);stroke(c,43,484,210,210,102,0x886fa4c1,3);
            Finger joy=null;for(Finger f:fingers.values())if(f.startX<360&&f.startY>360){joy=f;break;}
            circle(c,joy==null?148:148+clamp((joy.x-joy.startX)/100)*55,joy==null?590:590+clamp((joy.y-joy.startY)/100)*55,43,0xaac3d9ee);
            circle(c,1138,601,91,held(1138,600,94)?0xfff6ad63:0xddc7657b);
            stroke(c,1049,512,178,178,89,0xffffd9b2,3);center(c,"FEUER",1138,614,25,0xff131b2d,true);
            chip(c,"ENERGIE " +ammo,827,619,206,59,0xff708bc9);
            if(notificationTimer>0)center(c,notification,647,118,19,0xffa9ffe9,true);
            if(tutorial){panel(c,317,195,648,258);center(c,"SO SPIELST DU",640,249,31,0xffc7f5ff,true);
                center(c,"Links bewegen · rechts ziehen: zielen · FEUER schießen",640,301,19,Color.WHITE,false);
                center(c,"Alle Drohnen deaktivieren, dann das Portal erreichen",640,347,18,0xffb4c5e1,false);
                center(c,"Berühren, um zu beginnen",640,399,19,0xffb8b1ff,true);
            }
        }
        private void menu(Canvas c){
            gradient(c,0,0,W,H,0xff080e23,0xff2d184b);starfield(c,100,114,3);
            glow(c,720,320,430,0x554d8dff);glow(c,325,440,320,0x447cf5c3);
            center(c,"U E N K E R L A B S   G A M E S",640,91,20,0xff8af9d1,true);
            center(c,"NEON BASTION",640,191,70,0xffeff6ff,true);
            center(c,"ACHT SEKTOREN · EINE VERLORENE ORBITALSTATION",640,236,20,0xffb3bee0,true);
            panel(c,340,278,600,98);center(c,"FORTSETZEN · SEKTOR "+unlocked,640,338,29,0xff91ffe4,true);
            center(c,"SEKTOR AUSWÄHLEN",640,412,20,0xffcfbaff,true);
            for(int i=0;i<LAST;i++){
                int ix=i%4,row=i/4;float x=368+ix*142,y=428+row*78;
                chip(c,(i+1)+((i+1)<=unlocked?"  ✓":"  •"),x,y,126,66,(i+1)<=unlocked?0xff90ffdb:0xff384662);
            }
            center(c,"3D-PERSPEKTIVE · LOKALER FORTSCHRITT · OFFLINE",640,659,17,0xffaab8d1,false);
        }
        private void overlay(Canvas c){
            rect(c,0,0,W,H,0xaa070b1c);panel(c,290,170,700,423);
            String title=mode==PAUSE?"PAUSE":mode==FAILED?"SIGNAL VERLOREN":mode==VICTORY?"SEKTOR GESICHERT":"BASTION BEFREIT";
            center(c,title,640,259,45,0xffe9ffff,true);
            center(c,mode==PAUSE?"SPIELSTAND GESPEICHERT":mode==FAILED?"Versuch es erneut":mode==COMPLETE?"Alle acht Sektoren abgeschlossen":"Drohnen deaktiviert: "+kills,640,326,22,0xffb9c9e4,false);
            rounded(c,370,368,540,95,16,0xff78cdbb);
            center(c,mode==PAUSE?"WEITERSPIELEN":mode==FAILED?"NEU STARTEN":mode==VICTORY?"NÄCHSTER SEKTOR":"ZUM MENÜ",640,424,27,0xff0e1932,true);
            if(mode!=COMPLETE){center(c,"ZURÜCK ZUM HAUPTMENÜ",640,527,19,0xffc4bce8,true);}
        }
    }
}
