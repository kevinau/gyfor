/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.leadlightdesigner;

import java.nio.file.Paths;

/*
 * CTabFolder example snippet: prevent an item from closing
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 *
 * @since 3.0
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.leadlightdesigner.guide.GuideTabItem;

public class Snippet82 {

public static void main(String[] args) {
  Display display = new Display();
  Shell shell = new Shell(display);
  shell.setLayout(new FillLayout());
  
  Image image = new Image(display, "C:\\Users\\Kevin\\code\\leadlight2\\org.leadlightdesigner\\src\\icons\\obj16\\design.png");

  EditorTabFolder folder = new EditorTabFolder(shell);
  
  for (int i = 0; i < 6; i++) {
    TabItem item = new GuideTabItem(folder, Paths.get("workspace"), Paths.get("17 Burwood Ave.jpg"), Paths.get("guides/topCentrePanel.png"));
    item.setText("Item " + i + " ");
    item.setImage(image);
    Text text = new Text(folder, SWT.MULTI);
    text.setText("Content for Item " + i);
    item.setControl(text);
  }
  folder.addMouseListener(new MouseListener() {
    @Override
    public void mouseDoubleClick(MouseEvent arg0) {
    }

    @Override
    public void mouseDown(MouseEvent ev) {
      EditorTabFolder tabFolder = (EditorTabFolder)ev.getSource();
      System.out.println(".... mouse down " + ev);
      for (TabItem item : tabFolder.getIItems()) {
        if (item.getBounds().contains(ev.x, ev.y)) {
          System.out.println("mouse down over " + item.getText());
          
          Menu menu = new Menu (shell, SWT.POP_UP);
          item.buildPopupMenu(menu);
          tabFolder.setMenu(menu);
          break;
        }
      }
    }

    @Override
    public void mouseUp(MouseEvent arg0) {
    }
  });

  shell.pack();
  shell.open();
  while (!shell.isDisposed()) {
    if (!display.readAndDispatch())
      display.sleep();
  }
  display.dispose();
}
}