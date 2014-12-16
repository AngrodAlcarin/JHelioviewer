package org.helioviewer.jhv;

import java.awt.Dialog.ModalityType;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.*;

import org.helioviewer.jhv.base.Log;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunIdentifier;

/**
 * Routines to catch and handle all runtime exceptions.
 * 
 * @author Malte Nuhn
 */
public class JHVUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final JHVUncaughtExceptionHandler SINGLETON = new JHVUncaughtExceptionHandler();

    public static JHVUncaughtExceptionHandler getSingletonInstance() {
        return SINGLETON;
    }

    /**
     * This method sets the default uncaught exception handler. Thus, this
     * method should be called once when the application starts.
     */
    public static void setupHandlerForThread() {
        Thread.setDefaultUncaughtExceptionHandler(JHVUncaughtExceptionHandler.getSingletonInstance());
    }

    /**
     * Generates a simple error Dialog, allowing the user to copy the
     * errormessage to the clipboard.
     * <p>
     * As options it will show {"Quit JHelioviewer", "Continue"} and quit if
     * necessary.
     * 
     * @param title
     *            Title of the Dialog
     * @param msg
     *            Object to display in the main area of the dialog.
     */
    private static void showErrorDialog(final String title, final String msg, final Throwable e,final String log)
    {
        Vector<Object> objects = new Vector<Object>();
        
        JLabel head=new JLabel("Dang! You hit a bug in JHelioviewer.");
        head.setFont(head.getFont().deriveFont(Font.BOLD));
        
        objects.add(head);
        objects.add(Box.createVerticalStrut(10));
        objects.add(new JLabel("Here are some technical details about the problem:"));

        Font font = new JLabel().getFont();
        font = font.deriveFont(font.getStyle() ^ Font.ITALIC);

        JTextArea textArea = new JTextArea();
        textArea.setMargin(new Insets(5, 5, 5, 5));
        textArea.setText(msg);
        textArea.setEditable(false);
        JScrollPane sp = new JScrollPane(textArea);
        sp.setPreferredSize(new Dimension(600, 400));

        objects.add(sp);
        JCheckBox allowCrashReport = new JCheckBox("Send this anonymous crash report to the developers.",true);
        objects.add(allowCrashReport);
        objects.add(Box.createVerticalStrut(10));
        
        JOptionPane optionPane = new JOptionPane(title);
        optionPane.setMessage(objects.toArray());
        optionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
        optionPane.setOptions(new String[] { "Quit" });
        
        JFrame jf=new JFrame();
        jf.setUndecorated(true);
        jf.setLocationRelativeTo(null);
        jf.setIconImage(iconToImage(UIManager.getIcon("OptionPane.errorIcon")));
        jf.setVisible(true);
        
        JDialog dialog = optionPane.createDialog(jf, title);
        dialog.setAutoRequestFocus(true);
        dialog.setIconImage(iconToImage(UIManager.getIcon("OptionPane.errorIcon")));
        dialog.setResizable(true);
        dialog.setModalityType(ModalityType.TOOLKIT_MODAL);
        dialog.setVisible(true);
        
        jf.dispose();
        
        if(allowCrashReport.isSelected())
        {
            RaygunClient client = new RaygunClient("SchjoS2BvfVnUCdQ098hEA==");
            client.SetVersion(JHVGlobals.VERSION_AND_RELEASE);
            Map<String, String> customData = new HashMap<String, String>();
            customData.put("Log",log);
            customData.put("JVM", System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + " (JRE " + System.getProperty("java.specification.version") + ")");

            RaygunIdentifier user = new RaygunIdentifier(Settings.getProperty("UUID"));
            client.SetUser(user);
            client.Send(e,null,customData);
        }
        
        Runtime.getRuntime().halt(0);
    }

    private JHVUncaughtExceptionHandler() {
    }

    // we do not use the logger here, since it should work even before logging
    // initialization
    @SuppressWarnings("deprecation")
    public void uncaughtException(final Thread t, final Throwable e)
    {
        if(!EventQueue.isDispatchThread())
        {
            try
            {
                EventQueue.invokeAndWait(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        uncaughtException(t,e);
                    }
                });
                return;
            }
            catch(Exception _e)
            {
                _e.printStackTrace();
                
                //even that didn't work? let's use our good
                //luck and try to do the rest of the show
                //off of the event dispatcher thread
            }
        }
        
        //stop recursive error reporting
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread _t,Throwable _e)
            {
                //IGNORE all other exceptions
            }
        });
        
        // STOP THE WORLD to avoid exceptions piling up
        // Close all threads (excluding systemsthreads, just stop the timer thread from the system)
        for(Thread thr:Thread.getAllStackTraces().keySet())
            if(thr!=Thread.currentThread() && (!thr.getThreadGroup().getName().equalsIgnoreCase("system") || thr.getName().contains("Timer")))
                try
                {
                    thr.suspend();
                }
                catch(Throwable _th)
                {
                }
        for(Thread thr:Thread.getAllStackTraces().keySet())
        	if(thr!=Thread.currentThread() && (!thr.getThreadGroup().getName().equalsIgnoreCase("system") || thr.getName().contains("Timer")))
                try
                {
                    thr.stop();
                }
                catch(Throwable _th)
                {
                }
        
        final String finalLog = Log.GetLastFewLines(6);
        String msg = "JHelioviewer: " + JHVGlobals.VERSION_AND_RELEASE + "\n";
        msg += "Date: " + new Date() + "\n";
        msg += "JVM: " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + " (JRE " + System.getProperty("java.specification.version") + ")\n";
        msg += "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version") + "\n\n";
        msg += finalLog;
        
        try(StringWriter st=new StringWriter())
        {
            try(PrintWriter pw=new PrintWriter(st))
            {
                e.printStackTrace(pw);
                msg+=st.toString();
            }
        }
        catch(IOException e1)
        {
            e1.printStackTrace();
        }

        for(Frame f:Frame.getFrames())
            f.setVisible(false);
        
        e.printStackTrace();
        
        final String finalMsg=msg;
        final Throwable finalE=e;
        
        //DO NOT USE THIS. will kill the repaint manager
        //try to drain the awt-eventqueue, throwing everything away
        /*try
        {
            EventQueue eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
            while(eq.peekEvent()!=null)
                eq.getNextEvent();
        }
        catch(InterruptedException e2)
        {
            e2.printStackTrace();
        }*/
        
        //this wizardry forces the creation of a new awt event queue, if needed
        new Thread(new Runnable()
        {
            public void run()
            {	
                EventQueue.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        JHVUncaughtExceptionHandler.showErrorDialog("JHelioviewer: Fatal error", finalMsg, finalE, finalLog);
                    }
                });
            }
        }).start();
    }
    
    private static Image iconToImage(Icon icon)
    {
        if(icon instanceof ImageIcon)
            return ((ImageIcon)icon).getImage();
        else
        {
            int w=icon.getIconWidth();
            int h=icon.getIconHeight();
            GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd=ge.getDefaultScreenDevice();
            GraphicsConfiguration gc=gd.getDefaultConfiguration();
            BufferedImage image=gc.createCompatibleImage(w,h);
            Graphics2D g=image.createGraphics();
            icon.paintIcon(null,g,0,0);
            g.dispose();
            return image;
        }
    }
}
