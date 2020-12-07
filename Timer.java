import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.imageio.IIOException;
import javax.swing.*;    // make the Swing GUI classes available
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;       // used for Color and GridLayout classes
import java.awt.event.*; // used for ActionEvent
import java.awt.image.BufferStrategy;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//TODO: Add only one buttonListener for all buttons, use e.getSource()

public class Timer {
    private static int seconds = 0;
    static JLabel hour,minute,second;
    static JFrame window = new JFrame("Timer");
    static JPanel content = new JPanel(new GridLayout(7,1));
    static JPanel clock = new JPanel(new GridLayout(1,5));
    static JButton startbut = new JButton(htmlformat("Start",5));
    static JButton stopbut = new JButton(htmlformat("Stop",5));
    static JButton thirty = new JButton(htmlformat("30 sec.",5));
    static JButton oneMin = new JButton(htmlformat("1 min.",5));
    static JButton fiveMin = new JButton(htmlformat("5 min.",5));
    static JButton tenMin = new JButton(htmlformat("10 min.", 5));
    static Thread timeThread;
    static int k = 0;
    static boolean timerRunning = false;
    static boolean showButtons = true;
    static Color mycolor;
    static ArrayList<JButton> buttonList = new ArrayList<>();
    static FileInputStream inFile;
    static AudioStream sound;
    static AudioPlayer player;

    public static String htmlformat( String text, int fontSize){
        switch(fontSize){
            case 1:
                return "<html><font size=\"1\">"+text+"</font>";
            case 2:
                return "<html><font size=\"2\">"+text+"</font>";
            case 3:
                return "<html><font size=\"3\">"+text+"</font>";
            case 4:
                return "<html><font size=\"4\">"+text+"</font>";
            case 5:
                return "<html><font size=\"5\">"+text+"</font>";
            case 6:
                return "<html><font size=\"6\">"+text+"</font>";
            case 7:
                return "<html><font size=\"7\">"+text+"</font>";
            case 8:
                return "<html><font size=\"8\">"+text+"</font>";
            case 9:
                return "<html><font size=\"9\">"+text+"</font>";
            case 10:
                return "<html><font size=\"10\">"+text+"</font>";
            case 11:
                return "<html><font size=\"11\">"+text+"</font>";
            case 12:
                return "<html><font size=\"12\">"+text+"</font>";

        }
        return null;
    }

    public static String clockFormat( int num, boolean hourOrMinute){
        String out = String.valueOf(num);
        if (out.length() == 1){
            if (hourOrMinute) return htmlformat(0+out+"       :",7);
            else return htmlformat(0+out,7);
        }
        if (hourOrMinute) return htmlformat(out+"       :",7);
        else return htmlformat(out,7);
    }

    public static void timer(int k){
        if (k == 0) return;
        seconds += k;
        timeThread = new Thread(() -> {
            for (int i = 0; i <= seconds;i++){
                if (seconds-i >= 3600){
                    int hours = (seconds-i) / 3600;
                    int minutes = ((seconds-i) / 60) % 60;
                    int secs = (seconds-i) % 60;
                    hour.setText(clockFormat(hours,true));
                    minute.setText(clockFormat(minutes,true));
                    second.setText(clockFormat(secs,false));
                    clock.revalidate();
                    clock.repaint();
                }
                else if (seconds-i > 59){
                    int div = (seconds-i)/60;
                    int mod = (seconds-i)%60;
                    hour.setText(clockFormat(00,true));
                    minute.setText(clockFormat(div,true));
                    second.setText(clockFormat(mod,false));
                    clock.revalidate();
                    clock.repaint();
                } else{
                    hour.setText(clockFormat(00,true));
                    minute.setText(clockFormat(00,true));
                    second.setText(clockFormat(seconds-i,false));
                    clock.revalidate();
                    clock.repaint();

                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                }catch (InterruptedException e ) { break;}

                if (i == seconds){
                    player.player.start(sound);

                    while (true){
                        stopbut.setBackground(Color.RED);
                        stopbut.repaint();
                        try {
                            TimeUnit.MILLISECONDS.sleep(500);
                        }catch (InterruptedException e) { break; }
                        stopbut.setBackground(Color.WHITE);
                        stopbut.repaint();
                        try {
                            TimeUnit.MILLISECONDS.sleep(500);
                        }catch (InterruptedException e) { break; }

                    }
                }
            }

        });
        timerRunning = true;
        timeThread.start();
    }

    public static void main(String[] args){

        try {
            inFile = new FileInputStream("TimerSound.wav");
            sound = new AudioStream(inFile);
        }catch (FileNotFoundException e ) { System.err.println("File not found");}
        catch (IOException d) {d.printStackTrace(); }

        ActionListener startlisten = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               if (!timerRunning) timer(k);
            }
        };

        ActionListener stoplisten = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                player.player.stop(sound);
                if (!(timeThread == null)){
                    if (timerRunning){
                        timeThread.interrupt();
                        stopbut.setBackground(Color.WHITE);
                    }
                }
                stopbut.repaint();
                hour.setText(clockFormat(00,true));
                minute.setText(clockFormat(00,true));
                second.setText(htmlformat("00",7));
                k = 0;
                seconds = 0;
                timerRunning = false;
                clock.revalidate();
                clock.repaint();
            }
        };

        ActionListener timeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == thirty){
                    if (!timerRunning) {
                        k += 30;
                    }else{
                        seconds += 30;
                    }
                }else if (e.getSource() == oneMin){
                    if (!timerRunning) {
                        k += 60;
                    }else seconds += 60;
                }else if (e.getSource() == fiveMin){
                    if (!timerRunning) {
                        k += 300;
                    }else seconds += 300;
                } else {
                    if (!timerRunning) {
                        k += 600;
                    }else seconds += 600;
                }
                if (!timerRunning) {
                    if (k >= 3600) {
                        hour.setText(clockFormat(k / 3600, true));
                        minute.setText(clockFormat((k / 60) % 60, true));
                        second.setText(clockFormat(k % 60, false));
                        clock.revalidate();
                        clock.repaint();
                    } else {
                        minute.setText(clockFormat(k / 60, true));
                        second.setText(clockFormat(k % 60, false));
                        clock.revalidate();
                        clock.repaint();
                    }
                }
            }
        };

        MouseListener mouseListener = new MouseListener() {
            public void mouseClicked(MouseEvent e){}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                if (e.getSource() == startbut){
                    startbut.setBackground(mycolor);
                    startbut.repaint();
                }else if (e.getSource() == stopbut){
                    stopbut.setBackground(mycolor);
                    stopbut.repaint();
                }else if (e.getSource() == oneMin){
                    oneMin.setBackground(mycolor);
                    oneMin.repaint();
                }else if (e.getSource() == thirty){
                    thirty.setBackground(mycolor);
                    thirty.repaint();
                }else if (e.getSource() == fiveMin){
                    fiveMin.setBackground(mycolor);
                    fiveMin.repaint();
                }else if (e.getSource() == tenMin){
                    tenMin.setBackground(mycolor);
                    tenMin.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getSource() == startbut){
                    startbut.setBackground(Color.white);
                    startbut.repaint();
                }else if (e.getSource() == stopbut){
                    stopbut.setBackground(Color.white);
                    stopbut.repaint();
                }else if (e.getSource() == oneMin){
                    oneMin.setBackground(Color.white);
                    oneMin.repaint();
                }else if (e.getSource() == thirty){
                    thirty.setBackground(Color.white);
                    thirty.repaint();
                }else if (e.getSource() == fiveMin){
                    fiveMin.setBackground(Color.white);
                    fiveMin.repaint();
                }else if (e.getSource() == tenMin){
                    tenMin.setBackground(Color.white);
                    tenMin.repaint();
                }
            }
        };

        startbut.addActionListener(startlisten);
        stopbut.addActionListener(stoplisten);

        mycolor = new Color(176,212,240);

        window.add(content);
        window.setResizable(false);
        content.add(clock);
        buttonList.add(startbut);
        buttonList.add(stopbut);
        buttonList.add(thirty);
        buttonList.add(oneMin);
        buttonList.add(fiveMin);
        buttonList.add(tenMin);

        for (JButton button : buttonList){
            content.add(button);
            button.addMouseListener(mouseListener);
            button.setOpaque(true);
            button.setBackground(Color.white);
            button.setFocusPainted(false);
            if (button == thirty || button == oneMin || button == fiveMin || button == tenMin){
                button.addActionListener(timeListener);
            }
        }

        clock.setBackground(Color.BLACK);
        clock.add(hour = new JLabel(clockFormat(00,true)));
        clock.add(minute = new JLabel(clockFormat(00,true)));
        clock.add(second = new JLabel(htmlformat("00",7)));
        hour.setHorizontalAlignment(SwingConstants.RIGHT);
        minute.setHorizontalAlignment(SwingConstants.CENTER);
        second.setHorizontalAlignment(SwingConstants.LEFT);
        hour.setForeground(Color.green);
        minute.setForeground(Color.green);
        second.setForeground(Color.green);
        clock.setVisible(true);
        hour.setVisible(true);
        minute.setVisible(true);
        second.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocation(100,150);
        window.setSize(260,430);
        window.setVisible(true);
    }
}
