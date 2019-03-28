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

import java.text.MessageFormat;

/*
 * FormLayout example snippet: create a simple OK/CANCEL dialog using form layout
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ConfirmNoSaveDialog {

  private Shell dialog;
  private int result;

  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.pack();
    shell.open();

    ConfirmNoSaveDialog dialog = new ConfirmNoSaveDialog(shell, "Some File", "close");
    int result = dialog.show();
    System.out.println("Modal result " + result);
    
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }

  public ConfirmNoSaveDialog(Shell shell, String objectName, String managedFileName, String actionName) {
    dialog = new Shell(shell, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.TITLE);
    dialog.setText(MessageFormat.format("Save {0}?", objectName));
    dialog.addShellListener(ShellListener.shellClosedAdapter(e -> {
      result = SWT.CANCEL;
      dialog.dispose();
    }));

    int displayWidth = shell.getDisplay().getClientArea().width;

    Label message = new Label(dialog, SWT.WRAP);
    message.setText(MessageFormat.format("Save changes to ''{0}'' before {1}?", managedFileName, actionName));
    FontData fontData = message.getFont().getFontData()[0];
    Font font = new Font(shell.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
    message.setFont(font);

    Label explain = new Label(dialog, SWT.WRAP);
    explain.setText("Your changes will be lost if you don't save them.");

    Button saveButton = new PushButton(dialog, "&Save", ev -> {
      result = SWT.YES;
      dialog.dispose();
    });
    Button abandonButton = new PushButton(dialog, "&Don't Save", ev -> {
      result = SWT.NO;
      dialog.dispose();
    });
    Button cancelButton = new PushButton(dialog, "&Cancel", ev -> {
      result = SWT.CANCEL;
      dialog.dispose();
    });
    FormLayout form = new FormLayout();
    form.marginWidth = form.marginHeight = 16;
    dialog.setLayout(form);

    FormData explainData = new FormData();
    explainData.left = new FormAttachment(0);
    explainData.top = new FormAttachment(message, 8);
    explainData.width = displayWidth / 4;
    explain.setLayoutData(explainData);

    FormData cancelData = new FormData();
    cancelData.right = new FormAttachment(explain, 0, SWT.RIGHT);
    cancelData.top = new FormAttachment(explain, 16);
    cancelButton.setLayoutData(cancelData);

    FormData abandonData = new FormData();
    abandonData.right = new FormAttachment(cancelButton, -8);
    abandonData.top = new FormAttachment(cancelButton, 0, SWT.TOP);
    abandonButton.setLayoutData(abandonData);

    FormData saveData = new FormData();
    saveData.right = new FormAttachment(abandonButton, -8);
    saveData.top = new FormAttachment(abandonButton, 0, SWT.TOP);
    saveButton.setLayoutData(saveData);

    dialog.setDefaultButton(cancelButton);
    dialog.pack();
//    dialog.open();

  }

  public int getResult() {
    return result;
  }

  public int show() {
    dialog.open();
    while (!dialog.isDisposed()) {
      Display display = dialog.getDisplay();
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    return result;
  }

}
