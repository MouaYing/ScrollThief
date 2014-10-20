// XboxControllerTest.java

import ch.aplu.xboxcontroller.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.Color;

public class XboxControllerTest extends javax.swing.JFrame
{
  private class PanelInit implements Runnable
  {
    public void run()
    {
      gPane1.color(Color.red);
      gPane1.bgColor(Color.cyan);
      gPane1.enableRepaint(false);
      gPane2.color(Color.red);
      gPane2.bgColor(Color.cyan);
      gPane1.enableRepaint(false);
      gPane3.bgColor(Color.yellow);
      gPane3.enableRepaint(false);
      gPane3.window(-1, 1, -1, 1);
      gPane3.move(0, 0);
      gPane4.bgColor(Color.yellow);
      gPane4.enableRepaint(false);
      gPane4.window(-1, 1, -1, 1);
      gPane4.move(0, 0);
      gPane5.window(0, 110, 0, 100);
      gPane5.color(Color.black);
      gPane6.window(-1.3, 1.3, -1.3, 1.3);

      crossLeft();
      crossRight();
      showDpad(-1);
      for (int i = 0; i < 10; i++)
      {
        gPane5.move(10 + i * 10, 60);
        gPane5.circle(2);
        String label = "";
        switch (i)
        {
          case 0:
            label = "A";
            break;
          case 1:
            label = "B";
            break;
          case 2:
            label = "X";
            break;
          case 3:
            label = "Y";
            break;
          case 4:
            label = "bk";
            break;
          case 5:
            label = "st";
            break;
          case 6:
            label = "ls";
            break;
          case 7:
            label = "rs";
            break;
          case 8:
            label = "lt";
            break;
          case 9:
            label = "rt";
            break;
        }
        gPane5.text(9 + i * 10, 9, label);

      }
    }
  }

  private class MyXboxControllerAdapter extends XboxControllerAdapter
  {
    public void buttonA(boolean pressed)
    {
      actuateButton(0, pressed);
    }

    public void buttonB(boolean pressed)
    {
      actuateButton(1, pressed);
    }

    public void buttonX(boolean pressed)
    {
      actuateButton(2, pressed);
    }

    public void buttonY(boolean pressed)
    {
      actuateButton(3, pressed);
    }

    public void back(boolean pressed)
    {
      actuateButton(4, pressed);
    }

    public void start(boolean pressed)
    {
      actuateButton(5, pressed);
    }

    public void leftShoulder(boolean pressed)
    {
      actuateButton(6, pressed);
    }

    public void rightShoulder(boolean pressed)
    {
      actuateButton(7, pressed);
    }

    public void leftThumb(boolean pressed)
    {
      actuateButton(8, pressed);
    }

    public void rightThumb(boolean pressed)
    {
      actuateButton(9, pressed);
    }

    public void dpad(int direction, boolean pressed)
    {
      if (pressed)
        showDpad(direction);
      else
        showDpad(-1);
    }

    public void leftTrigger(double value)
    {
      gPane1.clear();
      gPane1.fillRectangle(0, 0, 1, value);
      gPane1.repaint();
    }

    public void rightTrigger(double value)
    {
      gPane2.clear();
      gPane2.fillRectangle(0, 0, 1, value);
      gPane2.repaint();
    }

    public void leftThumbMagnitude(double magnitude)
    {
      gPane3.clear();
      leftMagnitude = magnitude;
      gPane3.move(toX(leftMagnitude, leftDirection),
        toY(leftMagnitude, leftDirection));
      crossLeft();
    }

    public void leftThumbDirection(double direction)
    {
      gPane3.clear();
      leftDirection = direction;
      gPane3.move(toX(leftMagnitude, leftDirection),
        toY(leftMagnitude, leftDirection));
      crossLeft();
    }

    public void rightThumbMagnitude(double magnitude)
    {
      gPane4.clear();
      rightMagnitude = magnitude;
      gPane4.move(toX(rightMagnitude, rightDirection),
        toY(rightMagnitude, rightDirection));
      crossRight();
    }

    public void rightThumbDirection(double direction)
    {
      gPane4.clear();
      rightDirection = direction;
      gPane4.move(toX(rightMagnitude, rightDirection),
        toY(rightMagnitude, rightDirection));
      crossRight();
    }

    public void isConnected(boolean connected)
    {
      if (connected)
        setTitle(title + " - Controller connected");
      else
        setTitle(title + " - Controller disconnected");
    }
  }
  private final String title = "XboxControllerTest V1.0 (www.aplu.ch) ";
  private XboxController xp;
  private double leftMagnitude = 0;
  private double leftDirection = 0;
  private double rightMagnitude = 0;
  private double rightDirection = 0;

  /** Creates new form XboxControllerTest */
  public XboxControllerTest()
  {
    initComponents();
    Toolkit toolkit = Toolkit.getDefaultToolkit();  
    Dimension screenSize = toolkit.getScreenSize();  
    int x = (screenSize.width - getWidth()) / 2;  
    int y = (screenSize.height - getHeight()) / 2;  
    setLocation(x, y);     
    setTitle(title + " - connecting...");
    new Thread(new PanelInit()).start();
    xp = new XboxController();
    if (!xp.isConnected())
      setTitle(title + " - Controller disconnected");
    else
      setTitle(title + " - Controller connected");
    xp.addXboxControllerListener(new MyXboxControllerAdapter());
    xp.setLeftThumbDeadZone(0.2);
    xp.setRightThumbDeadZone(0.2);
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        xp.release();
      }
    });
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();
    jLabel5 = new javax.swing.JLabel();
    jPanel1 = new javax.swing.JPanel();
    gPane4 = new ch.aplu.util.GPane();
    jPanel2 = new javax.swing.JPanel();
    gPane3 = new ch.aplu.util.GPane();
    jPanel3 = new javax.swing.JPanel();
    gPane1 = new ch.aplu.util.GPane();
    jPanel4 = new javax.swing.JPanel();
    gPane2 = new ch.aplu.util.GPane();
    jPanel5 = new javax.swing.JPanel();
    gPane5 = new ch.aplu.util.GPane();
    jLabel6 = new javax.swing.JLabel();
    jPanel6 = new javax.swing.JPanel();
    gPane6 = new ch.aplu.util.GPane();
    jLabel7 = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setResizable(false);

    jLabel1.setFont(new java.awt.Font("Arial", 1, 12));
    jLabel1.setText("LeftTrigger");

    jLabel2.setFont(new java.awt.Font("Arial", 1, 12));
    jLabel2.setText("RightTrigger");

    jLabel3.setFont(new java.awt.Font("Arial", 1, 18));
    jLabel3.setText("Xbox Pad Test   -   Please press any actuator");

    jLabel4.setFont(new java.awt.Font("Arial", 1, 12));
    jLabel4.setText("LeftThumb");

    jLabel5.setFont(new java.awt.Font("Arial", 1, 12));
    jLabel5.setText("RightThumb");

    jPanel1.setBackground(new java.awt.Color(51, 255, 51));
    jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    jPanel1.setMinimumSize(new java.awt.Dimension(102, 102));
    jPanel1.setPreferredSize(new java.awt.Dimension(110, 110));

    javax.swing.GroupLayout gPane4Layout = new javax.swing.GroupLayout(gPane4);
    gPane4.setLayout(gPane4Layout);
    gPane4Layout.setHorizontalGroup(
      gPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 98, Short.MAX_VALUE)
    );
    gPane4Layout.setVerticalGroup(
      gPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 98, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(gPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 98, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(gPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 98, Short.MAX_VALUE)
    );

    jPanel2.setBackground(new java.awt.Color(51, 255, 51));
    jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    jPanel2.setMinimumSize(new java.awt.Dimension(102, 102));
    jPanel2.setPreferredSize(new java.awt.Dimension(110, 110));

    javax.swing.GroupLayout gPane3Layout = new javax.swing.GroupLayout(gPane3);
    gPane3.setLayout(gPane3Layout);
    gPane3Layout.setHorizontalGroup(
      gPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 98, Short.MAX_VALUE)
    );
    gPane3Layout.setVerticalGroup(
      gPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 98, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 98, Short.MAX_VALUE)
      .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(gPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 98, Short.MAX_VALUE))
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 98, Short.MAX_VALUE)
      .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(gPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 98, Short.MAX_VALUE))
    );

    jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
    jPanel3.setPreferredSize(new java.awt.Dimension(52, 102));

    javax.swing.GroupLayout gPane1Layout = new javax.swing.GroupLayout(gPane1);
    gPane1.setLayout(gPane1Layout);
    gPane1Layout.setHorizontalGroup(
      gPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 48, Short.MAX_VALUE)
    );
    gPane1Layout.setVerticalGroup(
      gPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 98, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(gPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, Short.MAX_VALUE)
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(gPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, Short.MAX_VALUE)
    );

    jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
    jPanel4.setPreferredSize(new java.awt.Dimension(52, 102));

    javax.swing.GroupLayout gPane2Layout = new javax.swing.GroupLayout(gPane2);
    gPane2.setLayout(gPane2Layout);
    gPane2Layout.setHorizontalGroup(
      gPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 48, Short.MAX_VALUE)
    );
    gPane2Layout.setVerticalGroup(
      gPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 98, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(gPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, Short.MAX_VALUE)
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(gPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, Short.MAX_VALUE)
    );

    jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

    javax.swing.GroupLayout gPane5Layout = new javax.swing.GroupLayout(gPane5);
    gPane5.setLayout(gPane5Layout);
    gPane5Layout.setHorizontalGroup(
      gPane5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 448, Short.MAX_VALUE)
    );
    gPane5Layout.setVerticalGroup(
      gPane5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 52, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
      jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(gPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
    );
    jPanel5Layout.setVerticalGroup(
      jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(gPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, Short.MAX_VALUE)
    );

    jLabel6.setFont(new java.awt.Font("Arial", 1, 12));
    jLabel6.setText("Digital Buttons");

    jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

    javax.swing.GroupLayout gPane6Layout = new javax.swing.GroupLayout(gPane6);
    gPane6.setLayout(gPane6Layout);
    gPane6Layout.setHorizontalGroup(
      gPane6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 112, Short.MAX_VALUE)
    );
    gPane6Layout.setVerticalGroup(
      gPane6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 113, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
      jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(gPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
    );
    jPanel6Layout.setVerticalGroup(
      jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(gPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
    );

    jLabel7.setFont(new java.awt.Font("Arial", 1, 12));
    jLabel7.setText("Direction Pad");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGap(186, 186, 186)
            .addComponent(jLabel6))
          .addGroup(layout.createSequentialGroup()
            .addGap(166, 166, 166)
            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addGap(193, 193, 193)
            .addComponent(jLabel7))
          .addGroup(layout.createSequentialGroup()
            .addGap(13, 13, 13)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addGroup(layout.createSequentialGroup()
                    .addGap(7, 7, 7)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addGroup(layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(jLabel2)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createSequentialGroup()
                    .addGap(38, 38, 38)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addGroup(layout.createSequentialGroup()
                    .addGap(54, 54, 54)
                    .addComponent(jLabel4)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                  .addGroup(layout.createSequentialGroup()
                    .addGap(30, 30, 30)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(16, 16, 16))
                  .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5)
                    .addGap(34, 34, 34))))))
          .addGroup(layout.createSequentialGroup()
            .addGap(34, 34, 34)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel1))
          .addGroup(layout.createSequentialGroup()
            .addGap(6, 6, 6)
            .addComponent(jLabel2))
          .addGroup(layout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addComponent(jLabel4))
          .addGroup(layout.createSequentialGroup()
            .addGap(4, 4, 4)
            .addComponent(jLabel5)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel6)
        .addGap(18, 18, 18)
        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jLabel7)
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private double toX(double r, double phi)
  {
    return r * Math.sin(phi / 180 * Math.PI);
  }

  private double toY(double r, double phi)
  {
    return r * Math.cos(phi / 180 * Math.PI);
  }

  private void crossLeft()
  {
    double x = gPane3.getPosX();
    double y = gPane3.getPosY();
    double d = 0.1;
    gPane3.line(x - d, y, x + d, y);
    gPane3.line(x, y - d, x, y + d);
    gPane3.repaint();
  }

  private void crossRight()
  {
    double x = gPane4.getPosX();
    double y = gPane4.getPosY();
    double d = 0.1;
    gPane4.line(x - d, y, x + d, y);
    gPane4.line(x, y - d, x, y + d);
    gPane4.repaint();
  }

  private void actuateButton(int id, boolean pressed)
  {
    if (pressed)
      gPane5.color(Color.red);
    else
      gPane5.color(Color.white);
    gPane5.move(10 * (id + 1), 60);
    gPane5.fillCircle(1.8);
  }

  private void showDpad(int id)
  {
    gPane6.clear();
    gPane6.move(0, 0);
    gPane6.color(new Color(100, 100, 100));
    gPane6.fillCircle(0.95);
    if (id != -1)
    {
      double alpha = id * Math.PI / 4;
      double x = Math.sin(alpha);
      double y = Math.cos(alpha);
      gPane6.color(Color.red);
      gPane6.move(x, y);
      gPane6.fillCircle(0.2);
    }
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String args[])
  {
    java.awt.EventQueue.invokeLater(new Runnable()
    {

      public void run()
      {
        new XboxControllerTest().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private ch.aplu.util.GPane gPane1;
  private ch.aplu.util.GPane gPane2;
  private ch.aplu.util.GPane gPane3;
  private ch.aplu.util.GPane gPane4;
  private ch.aplu.util.GPane gPane5;
  private ch.aplu.util.GPane gPane6;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JPanel jPanel6;
  // End of variables declaration//GEN-END:variables
}
