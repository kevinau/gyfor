/*******************************************************************************
 * Copyright (c) 2008 Kevin Holloway (kholloway@geckosoftware.com.au).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 ******************************************************************************/
package org.gyfor.object.plan;

import org.gyfor.object.EntryMode;
import org.gyfor.object.INode;


public interface IRuntimeModeProvider extends IRuntimeProvider {

  public EntryMode getEntryMode(Object instance);

}
