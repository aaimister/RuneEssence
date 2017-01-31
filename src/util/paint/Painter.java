package util.paint;

import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GeItem;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Painter implements MessageListener {

    private final Font CAM_8 = new Font("Cambria Math", Font.BOLD, 8);
    private final Font CAM_12 = new Font("Cambria Math", Font.BOLD, 12);

    private final Color PercentGreen = new Color(0, 163, 4, 150);
    private final Color PercentRed = new Color(163, 4, 0, 150);
    private final Color White90 = new Color(255, 255, 255, 90);
    private final Color White = new Color(255, 255, 255);
    private final Color Black = new Color(0, 0, 0);
    //Sunny Yellow, Blue, Black, Brown, Desert Sand, Clover Meadow, Chocolate, Cranberry, Cyan, Green, Hollyhock, Kiwi, Lime, Moonstruck, Orange, Pansey Purple, Pink,
    //Purple, Red, Smokey Plum, Taffy, White, Yellow, Autumn, Random
    private final Color ClickC[] = { new Color(187, 0, 0), new Color(187, 0, 0),  new Color(187, 0, 0), new Color(187, 0, 0),
            new Color(187, 0, 0), new Color(187, 0, 0), new Color(187, 0, 0), new Color(0, 0, 0),
            new Color(187, 0, 0), new Color(187, 0, 0), new Color(0, 0, 0), new Color(187, 0, 0),
            new Color(187, 0, 0), new Color(187, 0, 0),     new Color(187, 0, 0), new Color(187, 0, 0),
            new Color(187, 0, 0), new Color(187, 0, 0), new Color(0, 0, 0), new Color(187, 0, 0),
            new Color(187, 0, 0), new Color(187, 0, 0), new Color(187, 0, 0), new Color(187, 0, 0) };
    private final Color MainColor[] = { new Color(245, 223, 113), new Color(0, 0, 100), new Color(0, 0, 0), new Color(92, 51, 23),
            new Color(165, 136, 105), new Color(106, 168, 82), new Color(137, 104, 89),     new Color(184, 69, 67),
            new Color(0, 255, 255), new Color(0, 100, 0), new Color(212, 104, 126), new Color(224, 225, 135),
            new Color(0, 220, 0), new Color(68, 103, 161), new Color(255, 127, 0), new Color(112, 75, 105),
            new Color(238, 18, 137), new Color(104, 34, 139), new Color(100, 0, 0), new Color(134, 106, 125),
            new Color(255, 199, 174), new Color(255, 255, 255), new Color(238, 201, 0),     new Color(199, 113, 64) };
    private final Color ThinColor[] = { new Color(245, 223, 113, 70),     new Color(0, 0, 100, 70), new Color(0, 0, 0, 70), new Color(92, 51, 23, 70),
            new Color(165, 136, 105, 70), new Color(106, 168, 82, 70), new Color(137, 104, 89, 70), new Color(184, 69, 67, 70),
            new Color(0, 255, 255, 70),     new Color(0, 100, 0, 70), new Color(212, 104, 126, 70), new Color(224, 225, 135, 70),
            new Color(0, 220, 0, 70), new Color(68, 103, 161, 70), new Color(255, 127, 0, 70), new Color(112, 75, 105, 70),
            new Color(238, 18, 137, 70), new Color(104, 34, 139, 70), new Color(100, 0, 0, 70), new Color(134, 106, 125, 70),
            new Color(255, 199, 174, 70), new Color(255, 255, 255, 70), new Color(238, 201, 0, 70), new Color(199, 113, 64, 70) };
    private final Color LineColor[] = { new Color(0, 0, 0), new Color(255, 255, 255), new Color(255, 255, 255), new Color(255, 255, 255),
            new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(255, 255, 255), new Color(0, 0, 0),
            new Color(255, 255, 255), new Color(255, 255, 255), new Color(0, 0, 0), new Color(0, 0, 0),
            new Color(255, 255, 255), new Color(0, 0, 0), new Color(255, 255, 255), new Color(0, 0, 0),
            new Color(255, 255, 255), new Color(255, 255, 255), new Color(255, 255, 255), new Color(0, 0, 0),
            new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), };
    private final Color BoxColor[] = { new Color(245, 223, 113), new Color(0, 0, 100), new Color(0, 0, 0), new Color(92, 51, 23),
            new Color(165, 136, 105), new Color(106, 168, 82), new Color(137, 104, 89),     new Color(184, 69, 67),
            new Color(0, 255, 255), new Color(0, 100, 0), new Color(212, 104, 126), new Color(224, 225, 135),
            new Color(0, 220, 0), new Color(68, 103, 161), new Color(255, 127, 0), new Color(112, 75, 105),
            new Color(238, 18, 137), new Color(104, 34, 139), new Color(100, 0, 0), new Color(134, 106, 125),
            new Color(255, 199, 174), new Color(255, 255, 255), new Color(238, 201, 0),     new Color(199, 113, 64) };

    private final NumberFormat formatter = new DecimalFormat("#,###,###");
    private final NumberFormat nf = NumberFormat.getInstance();

    private long startTime;
    private long runTime;
    private int color = 2;

    private final Image logo;
    private final Image atom;

    private final ClientContext ctx;

    private final Dimension game;

    private GeItem essence = new GeItem(1436);

    private String status = "Loading";
    private String location = "Unknown";

    private long dotTimer;
    private int dotCounter;
    private int price;
    private long totalGP;
    private long hourGP;
    private long totalEssence;
    private long hourEssence;
    private long toLevelEssence;

    private long gainedXP;
    private long hourXP;
    private long timeToLevel;
    private long toLevelXP;
    private int currentSkill = 14;
    private long gainedLevel;

    private long startXP;
    private long currentXP;

    public Painter(Image logo, Image atom, ClientContext ctx) {
        this.logo = logo;
        this.atom = atom;
        this.ctx = ctx;
        game = new Dimension(800, 580);
        startTime = System.currentTimeMillis();
        startXP = currentXP = ctx.skills.experience(currentSkill);
        price = essence.price;
    }

    private String getDots() {
        String d[] = { "", ".", "..", "..." };
        if (dotTimer <= System.currentTimeMillis()) {
            dotCounter++;
            if (dotCounter > 3) {
                dotCounter = 0;
            }
            dotTimer = System.currentTimeMillis() + 1000;
        }
        return d[dotCounter];
    }

    private String formatTime(long milliseconds) {
        long t_seconds = milliseconds / 1000;
        long t_minutes = t_seconds / 60;
        long t_hours = t_minutes / 60;
        int seconds = (int) (t_seconds % 60);
        int minutes = (int) (t_minutes % 60);
        int hours = (int) (t_hours % 60);
        return (nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds));
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void mouseClicked(MouseEvent e) {
        //Do nothing.
    }

    public void mousePressed(MouseEvent e) {
        //Do nothing.
    }

    public void mouseReleased(MouseEvent e) {
        //Do nothing.
    }

    public void mouseEntered(MouseEvent e) {
        //Do nothing.
    }

    public void mouseExited(MouseEvent e) {
        //Do nothing.
    }

    private void calculateStat() {
        if (startTime > 0) {
            currentXP = ctx.skills.experience(currentSkill);
            toLevelXP = ctx.skills.experienceAt(ctx.skills.realLevel(currentSkill) + 1) - currentXP;
            gainedXP = currentXP - startXP;
            hourXP = ((int) ((3600000.0 / (double) runTime) * gainedXP));
            if (hourXP != 0) {
                timeToLevel = (int) (((double) toLevelXP / (double) hourXP) * 3600000.0);
            }
            totalEssence = (int) (gainedXP / 5);
            totalGP = (int) (totalEssence * price);
            hourEssence = (int) ((3600000.0 / (double) runTime) * totalEssence);
            hourGP = (int) ((3600000.0 / (double) runTime) * totalGP);
            toLevelEssence = (int) (toLevelXP / 5);
        }
    }

    public int getPercent(int skill, final int endLvl) {
        int lvl = ctx.skills.realLevel(skill);
        if (skill == 24 && (lvl == 120 || endLvl > 120)) {
            return 0;
        } else if (lvl == 99 || endLvl > 99) {
            return 0;
        }
        int xpTotal = ctx.skills.experienceAt(endLvl) - ctx.skills.experienceAt(lvl);
        if (xpTotal == 0) {
            return 0;
        }
        int xpDone = ctx.skills.experience(skill) - ctx.skills.experienceAt(lvl);
        return 100 * xpDone / xpTotal;
    }

    private void drawMouse(final Graphics g) {
        Point loc = ctx.input.getLocation();
        long mpt = System.currentTimeMillis() - ctx.input.getPressWhen();
        g.setColor(mpt < 500 ? ClickC[color] : ThinColor[color]);
        g.drawLine(0, loc.y, game.width, loc.y);
        g.drawLine(loc.x, 0, loc.x, game.height);
        g.setColor(MainColor[color]);
        g.drawLine(0, loc.y + 1, game.width, loc.y + 1);
        g.drawLine(0, loc.y - 1, game.width, loc.y - 1);
        g.drawLine(loc.x + 1, 0, loc.x + 1, game.height);
        g.drawLine(loc.x - 1, 0, loc.x - 1, game.height);
    }

    public void paint(Graphics g) {
        long totalTime = System.currentTimeMillis() - startTime;
        runTime = (System.currentTimeMillis() - startTime);

        //if (painting) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        //}

        //Background
        g.setColor(MainColor[color]);
        g.fillRect(3, 370, 569, 189);
        g.setColor(LineColor[color]);
        g.drawRect(3, 370, 569, 189);
        //Logo
        g.drawImage(logo, 56, 396, null);
        g.drawImage(atom, 32, 415, null);
        g.setColor(LineColor[color]);
        g.setFont(CAM_8);
        g.drawString("By Aaimister (c) " + "1.00", 464, 550);
//        //Prev Button
//        g.setColor(BoxColor[color]);
//        g.fillRect(21, 488, 17, 15);
//        g.setColor(LineColor[color]);
//        g.setFont(CAM_12);
//        g.drawString("<", 25, 500);
//        g.drawRect(21, 488, 17, 15);
//            //Shadow
//        g.setColor(White90);
//        g.fillRect(21, 488, 17, 7);
//        //Next Button
//        g.setColor(BoxColor[color]);
//        g.fillRect(481, 488, 17, 15);
//        g.setColor(LineColor[color]);
//        g.setFont(CAM_12);
//        g.drawString(">", 484, 500);
//        g.drawRect(481, 488, 17, 15);
//            //Shadow
//        g.setColor(White90);
//        g.fillRect(481, 488, 17, 7);
        //Main Box
        g.setColor(BoxColor[color]);
        g.fillRect(56, 430, 402, 124);
        g.setColor(LineColor[color]);
        g.drawRect(56, 430, 402, 124);
        calculateStat();
            //Column 1
        g.setColor(LineColor[color]);
        g.setFont(CAM_12);
        g.drawString("Time running: " + formatTime(totalTime), 63, 447);
        g.drawString("Location: " + location, 63, 461);
        g.drawString("Status: " + status + getDots(), 63, 475);
        g.drawString("Current Essence: " + "Normal", 63, 489);
        g.drawString("Total Min. XP: " + formatter.format(gainedXP), 63, 504);
        g.drawString("Total Min. XP/h: " + formatter.format(hourXP), 63, 518);
        g.drawString("Current Lvl: " + ctx.skills.realLevel(currentSkill), 63, 532);
        g.drawString("Gained Lvl(s): " + gainedLevel, 63, 546);
            //Split
        g.setColor(LineColor[color]);
        g.fillRect(257, 434, 1, 116);
            //Column 2
        g.setColor(LineColor[color]);
        g.setFont(CAM_12);
        g.drawString("Price of Essence: " + price, 264, 447);
        g.drawString("Total Money: $" + formatter.format(totalGP), 264, 461);
        g.drawString("Money / Hour: $" + formatter.format(hourGP), 264, 475);
        g.drawString("Total Essence: " + formatter.format(totalEssence), 264, 489);
        g.drawString("Essence / Hour: " + formatter.format(hourEssence), 264, 504);
        g.drawString("Essence to Lvl: " + formatter.format(toLevelEssence), 264, 518);
        g.drawString("Level In: " + formatTime(timeToLevel), 264, 532);
        g.drawString("Min. XP to Lvl: " + formatter.format(toLevelXP), 264, 546);
            //Shadow
        g.setColor(White90);
        g.fillRect(56, 430, 402, 62);
        //Percent Bar
        g.setColor(PercentRed);
        g.fillRect(3, 375, 569, 20);
            //Green Percentage
        int bar = (int) (getPercent(currentSkill, ctx.skills.realLevel(currentSkill) + 1) * 5.69);
        g.setColor(PercentGreen);
        g.fillRect(3, 375, bar, 20);
        g.setColor(LineColor[color]);
        g.drawRect(3, 375, 569, 20);
            //Text Percent
        g.setColor(White);
        g.setFont(CAM_12);
        g.drawString("" + getPercent(currentSkill, ctx.skills.realLevel(currentSkill) + 1) + "% to lvl " + (ctx.skills.realLevel(currentSkill) + 1) + " " + "Mining", 230, 390);
            //Shadow
        g.setColor(White90);
        g.fillRect(3, 375, 569, 10);
        //Minimize Button
        g.setColor(Color.WHITE);
        g.setFont(CAM_12);
            //Minimize Icon
        g.drawString("__", 560, 554);
            //Maximize Icon
        //g.drawRect(560, 547, 9, 9);
        g.drawRect(557, 544, 15, 15);
            //Shadow
        g.setColor(White90);
        g.fillRect(557, 544, 15, 7);

        drawMouse(g);
    }

    @Override
    public void messaged(MessageEvent e) {
        if (e.text().contains("just advanced a M")) {
            gainedLevel++;
        }
    }}
