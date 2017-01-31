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

    private final Area cityArea = new Area(new Tile(3234, 3387, 0), new Tile(3268, 3440, 0));
    private final Area bankArea = new Area(new Tile(3250, 3416, 0), new Tile(3257, 3427, 0));
    private final Area wizArea = new Area(new Tile(3250, 3401, 0), new Tile(3252, 3399, 0), new Tile(3254, 3399, 0), new Tile(3256, 3400, 0), new Tile(3256, 3403, 0), new Tile(3253, 3405, 0), new Tile(3252, 3405, 0));
    private final Area doorArea = new Area(new Tile(3251, 3397, 0), new Tile(3255, 3400, 0));

    private final Tile bankTile = new Tile(3254, 3419, 0);
    private final Tile wizTile = new Tile(3253, 3401, 0);
    private final Tile doorTileOutside = new Tile(3253, 3397, 0);
    private final Tile doorTileInside = new Tile(3253, 3400, 0);

    private final String bankBoothName = "Bank booth";
    private final String bankerName = "Banker";
    private final String bankAction = "Bank";
    private final String doorName = "Door";
    private final String doorAction = "Open";
    private final String wizName = "Aubury";
    private final String wizAction = "Teleport";
    private final String essenceName = "Rune Essence";
    private final String essenceAction = "Mine";
    private final String portalName = "Portal";
    private final String portalAction = "Enter";

    private final int mineAnimation = 6753;
    private final int[] doorBounds = { -244, 252, -836, -44, 204, 284 };

    private AntiBan antiBan;
    private Painter painter;

    private int idleCounter;
    private boolean mining;

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
            if (bankArea.contains(ctx.players.local())) {
                log("BANK");
                return State.BANK;
            } else {
                log("TO_BANK");
                return State.TO_BANK;
            }
        } else {
            if (cityArea.contains(ctx.players.local())) {
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
        painter = new Painter(downloadImage("http://i88.photobucket.com/albums/k170/aaimister/AaimistersEssenceMiner_zpsly9lgb8v.png"), downloadImage("http://i88.photobucket.com/albums/k170/aaimister/Atomm.png"), ctx);
        antiBan = new AntiBan(ctx);
    }

    private void log(String mes) {
        System.out.println("[" + (System.currentTimeMillis() - lastTime) + "]" + mes);
        System.out.flush();
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
                mining = false;
                // If we are in Varrock, walk to bankArea.
                if (cityArea.contains(ctx.players.local())) {
                    log("In Varrock");
                    // If we are in the wizard room, check the door.
                    if (wizArea.contains(ctx.players.local())) {
                        log("In Wizard ROom");
                        painter.setLocation("Wiz Room");
                        GameObject door = ctx.objects.select().within(doorArea).name(doorName).poll();
                        // If the door is valid, check if it is closed.
                        if (door.valid()) {
                            String[] actions = door.actions();
                            // If the door is closed, check if it's in the viewport.
                            if (actions.length > 1 && actions[0].equals(doorAction)) {
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
                                    if (walkOne(doorTileInside)) {
                                        antiBan.turnCamera(randomize(door.tile(), 2, 2));
                                    }
                                }
                            } else {
                                log("Walk to Bank");
                                // If the door is open, walk to bankArea.
                                painter.setStatus("Walking to Bank");
                                walkOne(bankTile);
                            }
                        } else {
                            log("Walk to Bank");
                            // If the door is not valid, walk to bankArea.
                            painter.setStatus("Walking to Bank");
                            walkOne(bankTile);
                        }
                    } else {
                        // If not in the wizard room, walk to bankArea.
                        painter.setLocation("Varrock");
                        painter.setStatus("Walking to Bank");
                        walkOne(bankTile);
                    }
                    antiBan.doAntiBan();
                } else {
                    log("In Essence Mine");
                    painter.setLocation("Essence Mine");
                    painter.setStatus("Entering Portal");
                    Npc portal = ctx.npcs.select().name(portalName).poll();
                    // If portal is valid, enter it.
                    if (portal.valid()) {
                        log("Click portal");
                        // If portal is in viewport, enter.
                        if (portal.inViewport()) {
                            if (portal.interact(portalAction)) {
                                Condition.wait(new Callable<Boolean>() {
                                    @Override
                                    public Boolean call() throws Exception {
                                        log("PORTAL_WAITT!! TEST");
                                        return !portal.valid();
                                        //return cityArea.contains(ctx.players.local());
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
                boolean useBooth = Random.nextBoolean();
                GameObject booth = useBooth ? ctx.objects.select().name(bankBoothName).within(bankArea).shuffle().poll() : null;
                Npc banker = !useBooth ? ctx.npcs.select().name(bankerName).within(bankArea).shuffle().poll() : null;
                // If the bankArea is in the viewport, open and deposit everything.
                if (useBooth ? booth.valid() && booth.inViewport() : banker.valid() && banker.inViewport()) {
                    if (!ctx.bank.opened()) {
                        log("Bank Not Opened");
                        if (openBank(booth, banker, useBooth)) {
                            log("Bank Opened Successfully");
                            Condition.wait(new Callable<Boolean>() {
                                @Override
                                public Boolean call() throws Exception {
                                    return ctx.bank.opened();
                                }
                            }, 100, 30);
                            if (ctx.bank.depositInventory()) {
                                log("Deposit All");
                                Condition.sleep(Random.nextInt(50, 450));
                                ctx.bank.close();
                            }
                        }
                    } else {
                        if (ctx.bank.depositInventory()) {
                            log("Deposit All 02");
                            Condition.sleep(Random.nextInt(50, 450));
                            ctx.bank.close();
                        }
                    }
                } else {
                    // If the bankArea is not in the viewport, walk to it and turn the camera.
                    if (walkOne(bankTile)) {
                        antiBan.turnCamera(randomize(bankTile, 2, 2));
                    }
                }
                break;
            case TO_MINE:
                Npc wiz = ctx.npcs.select().name(wizName).poll();
                // If bankArea is open, close it.
                if (ctx.bank.opened()) {
                    ctx.bank.close();
                }
                // If in the wizard room, don't worry about the door and just teleport.
                if (wizArea.contains(ctx.players.local())) {
                    painter.setLocation("Wiz Area");
                    painter.setStatus("Teleporting");
                    antiBan.turnCamera(randomize(wiz.tile(), 2, 2));
                    if (wiz.interact(true, wizAction)) {
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                log("WIZ_WAIIT!!");
                                return !wizArea.contains(ctx.players.local());
                            }
                        }, 100, 40);
                    }
                } else {
                    GameObject door = ctx.objects.select().name(doorName).within(doorArea).poll();
                    painter.setLocation(bankArea.contains(ctx.players.local()) ? "Bank" : "Varrock");
                    // If the door is valid, check if it is closed.
                    if (door.valid()) {
                        String[] actions = door.actions();
                        // If the door is closed, check if it's in the viewport.
                        if (actions.length > 1 && actions[0].equals(doorAction)) {
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
                                if (walkOne(doorTileOutside)) {
                                    antiBan.turnCamera(randomize(door.tile(), 2, 2));
                                }
                            }
                        } else {
                            // If the door is open & wizard is in the viewport, teleport.
                            if (wiz.inViewport()) {
                                painter.setStatus("Teleporting");
                                if (wiz.interact(true, wizAction)) {
                                    Condition.wait(new Callable<Boolean>() {
                                        @Override
                                        public Boolean call() throws Exception {
                                            log("WIZ_WAIT!!!");
                                            return !cityArea.contains(ctx.players.local());
                                        }
                                    }, 100, 40);
                                }
                            } else {
                                painter.setStatus("Walking to Wiz");
                                // Walk to the wizard tile if he is not in the viewport.
                                if (walkOne(wizTile)) {
                                    antiBan.turnCamera(randomize(wiz.tile(), 2, 2));
                                }
                            }
                        }
                    } else {
                        // If the door is not valid, walk to the wizard tile.
                        painter.setStatus("Walking to Wiz");
                        walkOne(wizTile);
                    }
                    antiBan.doAntiBan();
                }
                break;
            case MINE:
                log("Starting mining");
                painter.setLocation("Essence Mine");
                GameObject essence = ctx.objects.select().name(essenceName).nearest().poll();
                log("found");
                // If there is essence and we are not mining, go find it and mine.
                if (essence.valid() && !mining) {
                    log("Found and not mining");
                    painter.setStatus("Mining");
                    // If the essence is in the viewport, mine.
                    if (essence.inViewport()) {
                        log("Click essence");
                        if (mineEssence(essence)) {
                            Condition.wait(new Callable<Boolean>() {
                                @Override
                                public Boolean call() throws Exception {
                                    return mining = ctx.players.local().animation() == mineAnimation;
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
                        Npc portal = ctx.npcs.select().name(portalName).poll();
                        // If portal is valid, enter it.
                        if (portal.valid()) {
                            // If portal is in viewport, enter.
                            if (portal.inViewport()) {
                                if (portal.interact(portalAction)) {
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
                } else {
                    if (mining) {
                        if (ctx.players.local().idle()) {
                            idleCounter++;
                        } else {
                            idleCounter = 0;
                        }
                        mining = idleCounter < 15;
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

    private boolean openBank(GameObject booth, Npc banker, boolean useBooth) {
        return useBooth ? booth.interact(bankAction) : banker.interact(bankAction);
    }

    private boolean openDoor(GameObject door) {
        door.bounds(doorBounds);
        return door.interact("Open");
    }

    private boolean mineEssence(GameObject essence) {
        log("Mine Essence");
        Tile[] list = getNearTiles(essence.tile(), 1);
        Tile nearest = list[Random.nextInt(0, list.length - 1)];
        return nearest.matrix(ctx).interact(essenceAction);
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
    }

    private Tile[] getNearTiles(Tile center, int radius) {
        int diameter = radius * 2;
        Tile[] list = new Tile[(diameter + 1) * (diameter + 1) - 1];
        if (radius > 0) {
            int index = 0;
            int x = -radius;
            int y = -radius;
            int z = center.floor();
            while(y <= radius) {
                for (int c = 0; c <= diameter; c++) {
                    if (c == radius && x == 0 && y == 0) {
                        x++;
                        continue;
                    }
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
