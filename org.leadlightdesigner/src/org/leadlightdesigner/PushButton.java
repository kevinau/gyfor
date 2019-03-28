package org.leadlightdesigner;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class PushButton extends Button {

  public PushButton(Composite parent, String label, Consumer<SelectionEvent> action) {
    super(parent, SWT.PUSH);
    setText("  " + label + "  ");
    addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent event) {
        action.accept(event);
      }

    });
  }

  @Override
  protected void checkSubclass() {
  }

}
