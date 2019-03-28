package org.leadlightdesigner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Snippet900 {

  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    Button button = new Button(shell, SWT.NONE);
    button.setText("Click Me!");
    button.addMouseListener(new MouseListener() {
      private boolean doubleClick;

      public void mouseUp(MouseEvent e) {
        if (!doubleClick) {
          System.out.println("Single Click! (1)");
        }
      }

      public void mouseDoubleClick(MouseEvent e) {
        doubleClick = true;
        System.out.println("Double Click!");
      }

      public void mouseDown(MouseEvent e) {
        doubleClick = false;

        System.out.println(e.count);
        Display.getDefault().timerExec(Display.getDefault().getDoubleClickTime(), new Runnable() {
          public void run() {
            if (!doubleClick) {
              System.out.println("Single Click! (2)");
            }
          }
        });
      }
    });
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
  }
}
