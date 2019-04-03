/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
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

/*
 * GridLayout snippet: align widgets in a GridLayout
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 *
 * @since 3.0
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Snippet172 {

  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    GridLayout layout = new GridLayout(4, false);
    shell.setLayout(layout);

    Button b = new Button(shell, SWT.PUSH);
    b.setText("LEFT, TOP");
    b.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("LEFT, CENTER");
    b.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("LEFT, BOTTOM");
    b.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("LEFT, FILL");
    b.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("CENTER, TOP");
    b.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("CENTER, CENTER");
    b.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("CENTER, BOTTOM");
    b.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("CENTER, FILL");
    b.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("RIGHT, TOP");
    b.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("RIGHT, CENTER");
    b.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("RIGHT, BOTTOM");
    b.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("RIGHT, FILL");
    b.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("FILL, TOP");
    b.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("FILL, CENTER");
    b.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
    b = new Button(shell, SWT.PUSH);
    b.setText("FILL, BOTTOM");
    b.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 1));
    ScrolledComposite sc = new ScrolledComposite(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    Label label = new Label(sc, SWT.BORDER);
    label.setText("FILL, FILL, SCROLLED");
    label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    sc.setLayout(new FillLayout());
    sc.setSize(200, 200);
    sc.setContent(label);
    
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
}
