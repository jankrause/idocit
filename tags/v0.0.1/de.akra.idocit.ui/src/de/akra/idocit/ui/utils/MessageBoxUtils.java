/*******************************************************************************
 * Copyright 2011 AKRA GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.akra.idocit.ui.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.akra.idocit.ui.constants.DialogConstants;

/**
 * Collection of messages boxes.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public final class MessageBoxUtils
{

	/**
	 * Shows an error message box with {@link SWT#OK} button.
	 * 
	 * @param parentShell
	 *            The parent shell for modal displaying.
	 * @param text
	 *            The displayed text in the message box.
	 */
	public static void openErrorBox(Shell parentShell, String text)
	{
		MessageBox messageBox = new MessageBox(parentShell, SWT.OK | SWT.ICON_ERROR);
		messageBox.setText(DialogConstants.DIALOG_TITLE);
		messageBox.setMessage(text);
		messageBox.open();
	}

	/**
	 * 
	 * Shows an question message box with {@link SWT#YES} and {@link SWT#NO} buttons.
	 * 
	 * @param parentShell
	 *            The parent shell for modal displaying.
	 * @param text
	 *            The displayed text in the message box.
	 * @return true, if the SWT.YES button was clicked in the dialog. If SWT.NO was
	 *         clicked, false is returned.
	 */
	public static boolean openQuestionDialogBox(Shell parentShell, String text)
	{
		MessageBox messageBox = new MessageBox(parentShell, SWT.YES | SWT.NO
				| SWT.ICON_QUESTION);
		messageBox.setText(DialogConstants.DIALOG_TITLE);
		messageBox.setMessage(text);
		return messageBox.open() == SWT.YES;
	}
	
	/**
	 * 
	 * Shows an warning message box with {@link SWT#YES} and {@link SWT#NO} buttons.
	 * 
	 * @param parentShell
	 *            The parent shell for modal displaying.
	 * @param text
	 *            The displayed text in the message box.
	 * @return true, if the SWT.YES button was clicked in the dialog. If SWT.NO was
	 *         clicked, false is returned.
	 */
	public static boolean openYesNoWarningDialogBox(Shell parentShell, String text)
	{
		MessageBox messageBox = new MessageBox(parentShell, SWT.YES | SWT.NO
				| SWT.ICON_WARNING);
		messageBox.setText(DialogConstants.DIALOG_TITLE);
		messageBox.setMessage(text);
		return messageBox.open() == SWT.YES;
	}
}