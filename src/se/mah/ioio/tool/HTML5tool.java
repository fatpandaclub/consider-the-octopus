/**
 * you can put a one sentence description of your tool here.
 *
 * ##copyright##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author		##author##
 * @modified	##date##
 * @version		##tool.prettyVersion##
 */

package se.mah.ioio.tool;

import javafx.application.Platform;
//import processing.app.Editor;
import processing.app.tools.Tool;
//import template.tool.Base;
import processing.app.Base;
import processing.app.tools.Tool;
import processing.app.ui.Editor;
import se.mah.ioio.tool.web.WebInterface;


public class HTML5tool implements Tool {
	private WebInterface web;
	private Editor editor;
	  Base base;
	
	public void init(Editor theEditor) {
		editor = theEditor;
	}
	
	public void run() {
	    // Get the currently active Editor to run the Tool on it
	    Editor editor = base.getActiveEditor();

	    					// We may only call launch once so if web is null we will
		if(web == null) {	// Make sure it's the first time we run the Tool
			new Thread() {
				@Override
				public void run() {
					Platform.setImplicitExit(false);
					javafx.application.Application.launch(WebInterface.class);
				}
			}.start();
			// Get the reference to the instance started by launch.
			web = WebInterface.waitForInitialization(editor);
			while(web.browser == null) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {		// Otherwise we try to show/hide the window (Doesn't seem to work this way.)
			Platform.runLater(new Runnable() {
				@Override public void run() {
					web.showOrHide();
				}
			});
		}
	}
	
	  public void init(Base base) {
		    // Store a reference to the Processing application itself
		    this.base = base;
		  }

	public Editor getEditor() {
		return editor;
	}
	
	public String getMenuTitle() {
		return "##tool.name##";
	}
 
 }



