//import java.math.*;

import java.awt.*;
import java.awt.peer.ComponentPeer;
import java.util.Random;
import java.awt.event.*;

public class Circles extends Panel implements Runnable
{
    Color BG_COLOR = Color.black;
    boolean isSuspended = false;
    Thread animThread;
    static boolean runningInBrowser = true;
    final static String title = 
        "The proof of Rabbi Abraham Ben Hiya Hanasi - An Illustration";
        
    public static void main(String[] args)
    {
        runningInBrowser = false;
	    Frame f = new Frame(title);
//	    f.setLayout(new GridLayout(1,1));
//	    f.setSize(MaxX, MaxY);
	    
	    f.addWindowListener(
	        new WindowAdapter() {
	                public void windowClosed(WindowEvent e)
	                {
	                    System.exit(0);
	                }
	        });
        Circles applet = new Circles();
	    applet.parseArgs(args);
	    f.add(applet);
	    f.pack();
        applet.init();
	    f.show();
        applet.run();
    }   
    
    final String usageMessage = 
        "Optional parameters:\n  -step [1.. in pixels] -delay [0.. in millisec] -radius [1.. in pixels]";
    
    void parseArgs(String[] args)
    {
        try
        {
            for (int i=0; i<args.length; i+=2)
                if (args[i].equalsIgnoreCase("-step"))
                    step = Integer.parseInt(args[i+1]);
                else
                if (args[i].equalsIgnoreCase("-delay"))
                    delayMillisec = Integer.parseInt(args[i+1]);
                else
                if (args[i].equalsIgnoreCase("-radius"))
                {
                    MaxY = Integer.parseInt(args[i+1]) * 5;
                    MaxX = MaxY * 3 / 2;
                }
                else
                    throw new Exception("Incorrect usage");
        }
        catch (Exception e)
        {
            System.out.println(usageMessage);
            System.exit(0);
        }
    }
    
    Image       buffImage; 
    Graphics    buffImage_gfx; 

    public Circles()
    {
		setLayout(null);
		setSize(MaxX,MaxY);        
		setBackground(BG_COLOR);		
        initControls();
    }

    public void init()
    {
        if (runningInBrowser)
            parseParams();
        drawCircles();
    }   

    void initControls()
    {
        Panel panel = new Panel(new GridLayout(1, (runningInBrowser) ? 2 : 3));
        add(panel);
        panel.setBounds(0, MaxY-BUTTON_HEIGHT, MaxX, BUTTON_HEIGHT);
        panel.add(createRestartButton());
        panel.add(createPauseResumeButton());
        if (!runningInBrowser)
            panel.add(createExitButton());
    }
    
    Button createRestartButton()
    {        
        Button button = new Button("Start / Restart");
        button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    isSuspended = false;
                    stop();
                    animThread = new Thread(Circles.this);
                    animThread.start();
                }            
            });
	    return button;
    }
    
    Button createPauseResumeButton()
    {
        Button button = new Button("Pause / Resume");
        button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (isSuspended)
                        animThread.resume();
                    else
                        animThread.suspend();
                    isSuspended = !isSuspended;
                }            
            });
        return button;
    }
    
    Button createExitButton()
    {
        Button button = new Button("Exit");
        button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    System.exit(0);
                }            
            });
	    return button;
    }
    
    public void stop()
    {
        if (animThread!=null && animThread.isAlive())
            animThread.stop(); //interrupt();
    }
    
    void parseParams()
    {
        String param = "3"; //getParameter("step");
        if (param != null)
            step = Integer.parseInt(param);
        param = "10"; // getParameter("delay");
        if (param != null)
            delayMillisec = Integer.parseInt(param);
        param = "80"; // getParameter("radius");
        if (param != null)
        {
            MaxY = Integer.parseInt(param) * 5;
            MaxX = MaxY * 3 / 2;
        }
    }
/*    
    public void start()
    {        
        animThread = new Thread(this);
        animThread.start();
    }
*/
    public void run()
    {        
        circles();
    }
    
    public void update(Graphics g)
    {
        updateScreen(g);
    }
    
    public void paint(Graphics g)
    {
        updateScreen(g);
    }        

    void updateScreen(Graphics g)
    {
        if (buffImage!=null && g!=null)
		    g.drawImage(buffImage, 0, 0, getBackground(), this);
		else
		    super.paint(g);
    }

    static int MaxX = 600;
    static int MaxY = 400;
    static int DEF_STEP = 3;
    static int DEF_DELAY_MILLISEC = 10; // delay
    int step = DEF_STEP;
    int delayMillisec = DEF_DELAY_MILLISEC; // hundredths of sec delay
    final int BUTTON_HEIGHT = 30;
    
    void circles()
    {
        drawCircles();
        animate();
    }
    
    void drawCircles()
    {
        Color color;
        int r;
        Random randomGen = new Random(0);

        buffImage = createImage(MaxX, MaxY-BUTTON_HEIGHT); 
        buffImage_gfx = buffImage.getGraphics(); 
        
        for (r = MaxY/5; r > 0; r -= step)
	    {
            color = Color.getHSBColor(randomGen.nextFloat(),randomGen.nextFloat(), .9f);
//			buffImage_gfx.setColor(new Color((int)(randomGen.nextFloat()*255), (int)(randomGen.nextFloat()*255), (int)(randomGen.nextFloat()*255)));
            buffImage_gfx.setColor(color);
    	    //g.setColor(new Color(255-color ,255-color, color));
			buffImage_gfx.drawArc(MaxX/2-r, MaxY/2-r, r*2,r*2, 0, 360);
		}
		repaint();
	}
	
	void animate()
	{
        int r;
		Random randomGen = new Random(0);
        Color color;

        for (r = MaxY/5; r > 0; r -= step)
	    {
//			color = new Color((int)(randomGen.nextFloat()*255), (int)(randomGen.nextFloat()*255), (int)(randomGen.nextFloat()*255));
            color = Color.getHSBColor(randomGen.nextFloat(),randomGen.nextFloat(), .9f);
//	        System.out.println(r);
//	        System.out.println(2*Math.PI*r);
			flatten(MaxX/2, MaxY/2, r, color, delayMillisec);
		}
	}
	
    int flatten(double cx, double cy, double r, Color color, int delayMillisec)
    {
        double diameter = r*2;
        double step     = r / 10;
        double rx, lx;
        double coef     = 1 / r;
        double r_old_eangle;
        double to_deg   = 180/Math.PI;
        int   r_eangle = 90, l_eangle = 90;
        boolean done;
        double  old_rx = cx,
	        old_lx = cx;
	    int   c;

        Image       bgImage = createImage(MaxX, MaxY-BUTTON_HEIGHT); 
        Graphics    bgImage_gfx = bgImage.getGraphics(); 
        
	    bgImage_gfx.drawImage(buffImage, 0, 0, BG_COLOR, this);
	    bgImage_gfx.setColor(getBackground()); 
	    // erase the outmost circle:
	    bgImage_gfx.drawArc((int)(cx-r), (int)(cy-r), (int)diameter, (int)diameter, 0, 360);

        for (rx = cx + step, lx = cx - step, done = false; !done ;
	        old_rx = rx, rx += step, old_lx = lx, lx -= step)
        {
            r_old_eangle = r_eangle;
	        r_eangle = (int)(90 - coef * (rx-cx) * to_deg);
    //	    arc(old_rx, cy, -85, r_old_eangle, r);
    //	    arc(old_lx, cy, -180 - r_old_eangle, -95, r);        
	        if (r_eangle <= -90) // -85)
	        {
			        done = true;
			        rx = cx + Math.PI*r;
			        lx = cx - Math.PI*r;
			        r_eangle = -90;
	        }
	        l_eangle = -180 - r_eangle;
    	    
	        buffImage_gfx.drawImage(bgImage, 0, 0, BG_COLOR, this);
    	    buffImage_gfx.setColor(color);
	        buffImage_gfx.drawArc((int)(rx-r), (int)(cy-r), (int)diameter, (int)diameter, -90, r_eangle+90);
    //	    arc(rx,      cy, -90, r_eangle,     r);
	        buffImage_gfx.drawArc((int)(lx-r), (int)(cy-r), (int)diameter, (int)diameter, l_eangle, -90-l_eangle);
    //	    arc(lx,      cy, l_eangle, -90,     r);
	        buffImage_gfx.drawLine((int)lx,(int)(cy + r), (int)rx,(int)(cy + r));
    //	    line(lx,cy + r, rx,cy + r);
            
            repaint();
            try {
		        Thread.sleep(delayMillisec);
		    } catch (InterruptedException e) {
		        e.printStackTrace();
		    }
        }
        
	    return 0;
    }
}
