package util.antiban;

import org.powerbot.script.*;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Npc;

import java.awt.*;

public class AntiBan {

    private ClientContext ctx;
    private Dimension game;

    private long antiBanTime;

    private boolean on;

    public AntiBan(ClientContext ctx) {
        this.ctx = ctx;
        game = new Dimension(800, 580);
        setOn(true);
    }

    private void log(String s) {
        System.err.println("[AntiBan]: " + s);
        System.err.flush();
    }

    public void doAntiBan() {
        if (!on) return;
        if (antiBanTime <= System.currentTimeMillis()) {
            antiBanTime = System.currentTimeMillis() + nextAntiBanTime();
            switch(Random.nextInt(0, 3)) {
                case 0: //Random camera rotation.
                    int angle = ctx.camera.yaw() + Random.nextInt(20, 340);
                    log("Random Camera Rotation: " + angle);
                    turnCamera(angle);
                    break;
                case 1: //Random mouse movement.
                    Point p = new Point(Random.nextInt(1, game.width - 1), Random.nextInt(1, game.height - 1));
                    log("Random Mouse Movement: (" + p.x + ", " + p.y + ")");
                    ctx.input.move(p);
                    break;
                case 2: //Move mouse off screen.
                    log("Move Mouse Offscreen: " + moveOffScreen());
                    break;
                case 3: //Examine random object
                    log("Examine Random Object: " + examineObject());
                    break;
                case 4: //Examine random player
                    log("Exanube Random Player: " + examinePlayer());
                    break;
            }
        }
    }

    private String moveOffScreen() {
        switch(Random.nextInt(0, 3)) {
            case 0: //Up
                ctx.input.move(Random.nextInt(-10, game.width + 10), Random.nextInt(-100, -10));
                return "Up";
            case 1: //Down
                ctx.input.move(Random.nextInt(-10, game.width + 10), game.height + Random.nextInt(10, 100));
                return "Down";
            case 2: //Left
                ctx.input.move(Random.nextInt(-100, -10), Random.nextInt(-10, game.height + 10));
                return "Left";
            case 3: //Right
                ctx.input.move(game.width + Random.nextInt(10, 100), Random.nextInt(-10, game.height + 10));
                return "Right";
        }
        return "None";
    }

    private String examineObject() {
        GameObject go = ctx.objects.select().select(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject gameObject) {
                return gameObject.name().length() > 0;
            }
        }).viewable().shuffle().poll();

        if (go.interact("Examine")) {
            Condition.sleep(Random.nextInt(120, 500));
        }

        return go.name();
    }

    private String examinePlayer() {
        //TODO
        return "NULL";
    }

    public void turnCamera(int angle) {
        new Thread() {
            public void run() {
                ctx.camera.angle(angle);
            }
        }.start();
    }

    public void turnCamera(GameObject go) {
        new Thread() {
            public void run() {
                ctx.camera.turnTo(go);
            }
        }.start();
    }

    public void turnCamera(Tile t) {
        new Thread() {
            public void run() {
                ctx.camera.turnTo(t);
            }
        }.start();
    }

    public void turnCamera(Npc npc) {
        new Thread() {
            public void run() {
                ctx.camera.turnTo(npc);
            }
        }.start();
    }

    public void setOn(boolean on) {
        this.on = on;
        if (on) {
            if (antiBanTime <= System.currentTimeMillis()) {
                antiBanTime = System.currentTimeMillis() + nextAntiBanTime();
            }
        }
    }

    private long nextAntiBanTime() {
        return Random.nextInt(15000, 90000);
    }

}
