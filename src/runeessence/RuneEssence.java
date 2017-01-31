package runeessence;

import org.powerbot.script.*;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.*;
import org.powerbot.script.rt6.ClientContext;
import util.antiban.AntiBan;
import util.paint.Painter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.Callable;

@Script.Manifest(name = "Aaimister's Rune Essence", description = "Mine essence.", properties = "version=1.00")
public class RuneEssence extends PollingScript<ClientContext> implements PaintListener, MouseListener, MessageListener {

    private static final Area CITY_AREA = new Area(new Tile(3234, 3387, 0), new Tile(3268, 3440, 0));
    private static final Area BANK_AREA = new Area(new Tile(3250, 3419, 0), new Tile(3257, 3427, 0));
    private static final Area WIZ_AREA = new Area(new Tile(3250, 3401, 0), new Tile(3252, 3399, 0), new Tile(3254, 3399, 0), new Tile(3256, 3400, 0), new Tile(3256, 3403, 0), new Tile(3253, 3405, 0), new Tile(3252, 3405, 0));
    private static final Area DOOR_AREA = new Area(new Tile(3251, 3397, 0), new Tile(3255, 3400, 0));

    private static final Tile BANK_TILE = new Tile(3254, 3419, 0);
    private static final Tile WIZ_TILE = new Tile(3253, 3401, 0);
    private static final Tile OUTSIDE_DOOR_TILE = new Tile(3253, 3397, 0);
    private static final Tile INSIDE_DOOR_TILE = new Tile(3253, 3400, 0);
    private static final Tile[] TO_BANK = new Tile[] { WIZ_TILE, new Tile(3258, 3410, 0), BANK_TILE };

    private static final String DOOR_NAME = "Door";
    private static final String WIZ_NAME = "Aubury";
    private static final String WIZ_ACTION = "Teleport";
    private static final String ESSENCE_NAME = "Rune Essence";
    private static final String ESSENCE_ACTION = "Mine";
    private static final String PORTAL_NAME = "Portal";
    private static final String PORTAL_ACTION = "Enter";

    private static final int MINE_ANIMATION = 6753;

    private Tile essenceTile;
    private Tile drawTile1;
    private Tile drawTile2;
    private Point doorPoint;
    private Polygon[] doorTri;
    private int[] doorBounds = { -244, 252, -836, -44, 204, 284 };
    private AntiBan antiBan;
    private Painter painter;

    private long lastTime;

    @Override
    public void mouseClicked(MouseEvent e) {
        if (painter != null) {
            painter.mouseClicked(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (painter != null) {
            painter.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (painter != null) {
            painter.mouseReleased(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (painter != null) {
            painter.mouseEntered(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (painter != null) {
            painter.mouseExited(e);
        }
    }

    @Override
    public void messaged(MessageEvent messageEvent) {
        painter.messaged(messageEvent);
    }

    private enum State { TO_BANK, BANK, TO_MINE, MINE }

    private State getState() {
        if (ctx.backpack.select().count() >= 28) {
            if (BANK_AREA.contains(ctx.players.local())) {
                log("BANK");
                return State.BANK;
            } else {
                log("TO_BANK");
                return State.TO_BANK;
            }
        } else {
            if (CITY_AREA.contains(ctx.players.local())) {
            //if (ctx.players.local().tile().x() < 10000) {
                log("TO_MINE");
                return State.TO_MINE;
            } else {
                log("MINE");
                return State.MINE;
            }
        }
    }

    @Override
    public void start() {
        painter = new Painter(downloadImage("http://i88.photobucket.com/albums/k170/aaimister/AaimistersEssenceMiner_zpsayjzqorp.png"), downloadImage("http://i88.photobucket.com/albums/k170/aaimister/Atomm.png"), ctx);
        antiBan = new AntiBan(ctx);
    }

    private void log(String mes) {
        //System.out.println("[" + (System.currentTimeMillis() - lastTime) + "]" + mes);
        //System.out.flush();
    }

    @Override
    public void poll() {
        lastTime = System.currentTimeMillis();
        log("START LOOP");
        //if (true) return;
        if (ctx.camera.pitch() <= 79) {
            ctx.camera.pitch(true);
        }
        switch(getState()) {
            case TO_BANK:
                // If we are in Varrock, walk to bank.
                if (CITY_AREA.contains(ctx.players.local())) {
                    log("In Varrock");
                    // If we are in the wizard room, check the door.
                    if (WIZ_AREA.contains(ctx.players.local())) {
                        log("In Wizard ROom");
                        painter.setLocation("Wiz Room");
                        GameObject door = ctx.objects.select().within(DOOR_AREA).name(DOOR_NAME).poll();
                        // If the door is valid, check if it is closed.
                        if (door.valid()) {
                            String[] actions = door.actions();
                            // If the door is closed, check if it's in the viewport.
                            if (actions.length > 1 && actions[0].equals("Open")) {
                                // If it is in the viewport, open it.
                                painter.setStatus("Opening Door");
                                if (door.inViewport()) {
                                    //ctx.camera.turnTo(door);
                                    if (openDoor(door)) {
                                        Condition.wait(new Callable<Boolean>() {
                                            @Override
                                            public Boolean call() throws Exception {
                                                log("DOOR_WAIT!!");
                                                return !door.valid() || door.actions()[0].equals("Close");
                                            }
                                        }, 100, 30);
                                    }
                                } else {
                                    // Walk to the door tile if the door is not in the viewport.
                                    if (walkOne(INSIDE_DOOR_TILE)) {
                                        antiBan.turnCamera(randomize(door.tile(), 2, 2));
                                    }
                                }
                            } else {
                                log("Walk to Bank");
                                // If the door is open, walk to bank.
                                painter.setStatus("Walking to Bank");
                                walkOne(BANK_TILE);
                            }
                        } else {
                            log("Walk to Bank");
                            // If the door is not valid, walk to bank.
                            painter.setStatus("Walking to Bank");
                            walkOne(BANK_TILE);
                        }
                    } else {
                        // If not in the wizard room, walk to bank.
                        painter.setLocation("Varrock");
                        painter.setStatus("Walking to Bank");
                        walkOne(BANK_TILE);
                    }
                    antiBan.doAntiBan();
                } else {
                    log("In Essence Mine");
                    painter.setLocation("Essence Mine");
                    painter.setStatus("Entering Portal");
                    Npc portal = ctx.npcs.select().name(PORTAL_NAME).poll();
                    // If portal is valid, enter it.
                    if (portal.valid()) {
                        log("Click portal");
                        // If portal is in viewport, enter.
                        if (portal.inViewport()) {
                            if (portal.interact(PORTAL_ACTION)) {
                                Condition.wait(new Callable<Boolean>() {
                                    @Override
                                    public Boolean call() throws Exception {
                                        log("PORTAL_WAITT!! TEST");
                                        return !portal.valid();
                                        //return CITY_AREA.contains(ctx.players.local());
                                    }
                                }, 100, 45);
                            }
                        } else {
                            // If portal not in viewport, walk to it.
                            if (walkOne(portal.tile())) {
                                antiBan.turnCamera(randomize(portal.tile(), 2, 2));
                            }
                        }
                    }
                }
                break;
            case BANK:
                painter.setLocation("Bank");
                painter.setStatus("Banking");
                // If the bank is in the viewport, open and deposit everything.
                if (ctx.bank.inViewport()) {
                    if (!ctx.bank.opened()) {
                        if (ctx.bank.open()) {
                            if (ctx.bank.depositInventory()) {
                                Condition.sleep(Random.nextInt(50, 450));
                                ctx.bank.close();
                            }
                        }
                    } else {
                        if (ctx.bank.depositInventory()) {
                            Condition.sleep(Random.nextInt(50, 450));
                            ctx.bank.close();
                        }
                    }
                } else {
                    // If the bank is not in the viewport, walk to it and turn the camera.
                    if (walkOne(BANK_TILE)) {
                        antiBan.turnCamera(randomize(BANK_TILE, 2, 2));
                    }
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            log("BANK_WAIT!!");
                            return ctx.bank.inViewport();
                        }
                    }, 100, 15);
                }
                break;
            case TO_MINE:
                Npc wiz = ctx.npcs.select().name(WIZ_NAME).poll();
                GameObject door = ctx.objects.select().name(DOOR_NAME).within(DOOR_AREA).poll();
                // If bank is open, close it.
                if (ctx.bank.opened()) {
                    ctx.bank.close();
                }
                // If in the wizard room, don't worry about the door and just teleport.
                if (WIZ_AREA.contains(ctx.players.local())) {
                    painter.setLocation("Wiz Area");
                    painter.setStatus("Teleporting");
                    antiBan.turnCamera(randomize(wiz.tile(), 2, 2));
                    if (wiz.interact(true, WIZ_ACTION)) {
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                log("WIZ_WAIIT!!");
                                return !WIZ_AREA.contains(ctx.players.local());
                            }
                        }, 100, 30);
                    }
                } else {
                    painter.setLocation(BANK_AREA.contains(ctx.players.local()) ? "Bank" : "Varrock");
                    // If the door is valid, check if it is closed.
                    if (door.valid()) {
                        String[] actions = door.actions();
                        // If the door is closed, check if it's in the viewport.
                        if (actions.length > 1 && actions[0].equals("Open")) {
                            painter.setStatus("Opening Door");
                            // If it is in the viewport, open it.
                            if (door.inViewport()) {
                                if (openDoor(door)) {
                                    Condition.wait(new Callable<Boolean>() {
                                        @Override
                                        public Boolean call() throws Exception {
                                            log("DOOR_WAIT!!");
                                            return !door.valid() || door.actions()[0].equals("Close");
                                        }
                                    }, 100, 30);
                                }
                            } else {
                                // Walk to the door tile if the door is not in the viewport.
                                if (walkOne(OUTSIDE_DOOR_TILE)) {
                                    antiBan.turnCamera(randomize(door.tile(), 2, 2));
                                }
                            }
                        } else {
                            // If the door is open & wizard is in the viewport, teleport.
                            if (wiz.inViewport()) {
                                painter.setStatus("Teleporting");
                                if (wiz.interact(true, WIZ_ACTION)) {
                                    Condition.wait(new Callable<Boolean>() {
                                        @Override
                                        public Boolean call() throws Exception {
                                            log("WIZ_WAIT!!!");
                                            return !CITY_AREA.contains(ctx.players.local());
                                        }
                                    }, 100, 30);
                                }
                            } else {
                                painter.setStatus("Walking to Wiz");
                                // Walk to the wizard tile if he is not in the viewport.
                                if (walkOne(WIZ_TILE)) {
                                    antiBan.turnCamera(randomize(wiz.tile(), 2, 2));
                                }
                            }
                        }
                    } else {
                        // If the door is not valid, walk to the wizard tile.
                        painter.setStatus("Walking to Wiz");
                        walkOne(WIZ_TILE);
                    }
                    antiBan.doAntiBan();
                }
                break;
            case MINE:
                log("Starting mining");
                painter.setLocation("Essence Mine");
                GameObject essence = ctx.objects.select().name(ESSENCE_NAME).nearest().poll();
                //GameObject essence = ctx.objects.select().nearest().name(ESSENCE_NAME).poll();

                log("found");
                // If there is essence and we are not mining, go find it and mine.
                if (essence.valid() && ctx.players.local().animation() != MINE_ANIMATION) {
                    log("Found and not mining");
                    painter.setStatus("Mining");
                    // If the essence is in the viewport, mine.
                    if (essence.inViewport()) {
                        log("Click essence");
                        if (mineEssence(essence)) {
                            Condition.wait(new Callable<Boolean>() {
                                @Override
                                public Boolean call() throws Exception {
                                    return ctx.players.local().animation() == MINE_ANIMATION;
                                }
                            }, 100, 20);
                        } else {
                            antiBan.turnCamera(randomize(essence.tile(), 2, 2));
                        }
                    } else {
                        log("Walking to essence");
                        // If the essence is not in the viewport, walk to it.
                        painter.setStatus("Walking to Essence");
                        if (walkOne(essence.tile())) {
                            antiBan.turnCamera(randomize(essence.tile(), 2, 2));
                        }
                    }
                    // If can't reach, enter portal
                    if (ctx.movement.reachable(ctx.players.local(), essence)) {
                        log("Getting unstuck");
                        painter.setStatus("Getting Unstuck");
                        Npc portal = ctx.npcs.select().name(PORTAL_NAME).poll();
                        // If portal is valid, enter it.
                        if (portal.valid()) {
                            // If portal is in viewport, enter.
                            if (portal.inViewport()) {
                                if (portal.interact(PORTAL_ACTION)) {
                                    Condition.wait(new Callable<Boolean>() {
                                        @Override
                                        public Boolean call() throws Exception {
                                            return !portal.valid();
                                        }
                                    }, 100, 45);
                                }
                            } else {
                                // If portal not in viewport, walk to it.
                                if (walkOne(portal.tile())) {
                                    antiBan.turnCamera(randomize(portal.tile(), 2, 2));
                                }
                            }
                        }
                    }
                }
                antiBan.doAntiBan();
                break;
            default:
                // Should never reach this.  Will be stuck! =O
                painter.setLocation("ERROR");
                painter.setStatus("ERROR");
                break;
        }

        log("END LOOP\n");
    }

    private boolean openDoor(GameObject door) {
        door.bounds(doorBounds);
        return door.interact("Open");
    }

    private boolean mineEssence(GameObject essence) {
        log("Mine Essence");
        Tile[] list = getNearTiles(essence.tile(), 1);
        Tile nearest = list[Random.nextInt(0, list.length - 1)];
//        Tile myTile = ctx.players.local().tile();
//        Tile nearest = null;
//        double current = 10;
//        for (Tile t : list) {
//            double next = tileDistance(myTile, t);
//            if (nearest == null) {
//                nearest = t;
//                current = next;
//            } else {
//                if (next < current) {
//                    nearest = t;
//                    current = next;
//                }
//            }
//        }
        return nearest.matrix(ctx).interact(ESSENCE_ACTION);
    }

    private double tileDistance(Tile t1, Tile t2) {
        if (t1.x() == -1 || t1.y() == -1 || t2.x() == -1 || t2.y() == -1) return -1;
        return Math.sqrt(Math.pow(t2.x() - t1.x(), 2) + Math.pow(t2.y() - t1.y(), 2));
    }

    private boolean walkOne(Tile t) {
        Tile flag = ctx.movement.destination();
        double distanceToEnd = tileDistance(t, flag);
        if (distanceToEnd == -1 || distanceToEnd > 3) {
            double distanceToFlag = tileDistance(ctx.players.local().tile(), flag);
            if (!ctx.players.local().inMotion() || (distanceToFlag != -1 && distanceToFlag <= 5)) {
                return ctx.movement.step(randomize(t, 1, 1));
            }
        }
        return false;
    }

    private Tile randomize(Tile t, int x, int y) {
        return new Tile(t.x() + Random.nextInt(-x, x), t.y() + Random.nextInt(-y, y));
    }

    private boolean isTileOnScreen(Tile t) {
        TileMatrix tm = t.matrix(ctx);
        return tm.inViewport();
    }

    private void drawTile(Graphics g, Tile tile, Color color) {
        TileMatrix tm = tile.matrix(ctx);
        if (tm.inViewport()) {
            g.setColor(Color.BLACK);
            g.drawPolygon(tm.bounds());
            g.setColor(color);
            g.fillPolygon(tm.bounds());
        }
    }

    @Override
    public void repaint(Graphics g) {
        if (painter != null) {
            painter.paint(g);
        }

        if (drawTile1 != null) {
            drawTile(g, drawTile1, new Color(0, 115, 0, 120));
        }
        if (drawTile2 != null) {
            drawTile(g, drawTile2, new Color(0, 115, 0, 120));
        }

        if (doorPoint != null) {
            //g.setColor(Color.RED);
            //g.drawRect(doorPoint.x, doorPoint.y, 1, 1);
        }

        if (doorTri != null) {
            g.setColor(Color.RED);
            for (Polygon p : doorTri) {
                g.drawPolygon(p);
            }
        }

        if (essenceTile != null) {
            if (isTileOnScreen(essenceTile)) {
                Tile[] list = getNearTiles(essenceTile, 1);
                for (Tile t : list) {
                    drawTile(g, t, new Color(0, 115, 0, 120));
                }
            }
        }
    }

    private Tile[] getNearTiles(Tile center, int radius) {
        int diameter = radius * 2;
        Tile[] list = new Tile[(diameter + 1) * (diameter + 1)];
        if (radius > 0) {
            int index = 0;
            int x = -radius;
            int y = -radius;
            int z = center.floor();
            while(y <= radius) {
                for (int c = 0; c <= diameter; c++) {
                    list[index++] = new Tile(center.x() + x++, center.y() + y, z);
                }
                x = -radius;
                y++;
            }
            return list;
        }
        return new Tile[] { center };
    }

}
