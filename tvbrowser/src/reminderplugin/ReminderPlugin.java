/*
 * TV-Browser
 * Copyright (C) 04-2003 Martin Oberhauser (darras@users.sourceoforge.net)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * CVS information:
 *  $RCSfile$
 *   $Source$
 *     $Date$
 *   $Author$
 * $Revision$
 */

package reminderplugin;
import devplugin.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.applet.*;
import java.net.URL;
import util.exc.*;
import util.ui.UiUtilities;

/**
 * TV-Browser
 *
 * @author Martin Oberhauser
 */
public class ReminderPlugin extends Plugin implements ReminderTimerListener {
  private static final util.ui.Localizer mLocalizer
    = util.ui.Localizer.getLocalizerFor(ReminderPlugin. class );
  private ReminderList reminderList= null ;private Properties settings;
  private Icon icon= null ;
  
  public void timeEvent(ReminderListItem item) {
    if ("true" .equals(settings.getProperty( "usesound" ))) {
      String fName=settings.getProperty( "soundfile" );
      try {
        URL url = new File(fName).toURL();
        AudioClip clip=Applet.newAudioClip(url);
        clip.play();
      } catch (java.net.MalformedURLException exc) {
        String msg = mLocalizer.msg( "error.1" ,"Error loading reminder sound file!\n({0})" , fName, exc);
        ErrorHandler.handle(msg, exc);
      }
    }
    
    if ("true" .equals(settings.getProperty( "usemsgbox" ))) {
      new ReminderFrame(reminderList, item);
    }
    if ("true" .equals(settings.getProperty( "useexec" ))) {
      String fName=settings.getProperty( "execfile" );
      try {
        Runtime.getRuntime().exec(fName);
      } catch (IOException exc) {
        String msg = mLocalizer.msg( "error.2" ,"Error executing reminder program!\n({0})" , fName, exc);
        ErrorHandler.handle(msg, exc);
      }
      
    } // remove the item
    if (item.getReminderMinutes() <= 0) {
      item.getProgram().unmark( this );
      reminderList.remove(item);
    }
    
  }
  
  public PluginInfo getInfo() {
    String name = mLocalizer.msg( "pluginName" ,"Reminder" );
    String desc = mLocalizer.msg( "description" ,"Eine einfache Implementierung einer Erinnerungsfunktion." );
    String author = "Martin Oberhauser (darras@users.sourceforge.net)" ;
    return new PluginInfo(name, desc, author, new Version(1, 0));
    
  }
  
  
  public void loadData(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    reminderList = (ReminderList) in.readObject();

    reminderList.removeExpiredItems();
    reminderList.setReminderTimerListener( this ); // mark the programs
    Iterator iter = reminderList.getReminderItems();
    while (iter.hasNext()) {
      ReminderListItem item = (ReminderListItem) iter.next();
      item.getProgram().mark( this );
    }
  }

  
  
  public void storeData(ObjectOutputStream out) throws IOException {
    out.writeObject(reminderList);
  }
  
  
  
  public Properties storeSettings() {
    return settings;
  }
  
  public void loadSettings(Properties settings) {
    if (settings== null ) {
      settings= new Properties();
    }
    
    this .settings=settings;
  }
  
  public String getContextMenuItemText() {
    return mLocalizer.msg( "contextMenuText" ,"Remind me" );
  }
  
  public String getButtonText() {
    return mLocalizer.msg( "buttonText" ,"Reminder list" );
  }
  
  public devplugin.SettingsTab getSettingsTab() {
    return new ReminderSettingsTab(settings);
  }
  
  public void execute(devplugin.Program[] programArr) {
    if (programArr == null) return;
    if (programArr.length == 1) {
      ReminderDialog dlg = new ReminderDialog(parent, programArr[0]);
      UiUtilities.centerAndShow(dlg);
      if (dlg.getOkPressed()) {
        int minutes = dlg.getReminderMinutes();
        addToReminderList(programArr[0], minutes);
      }
      dlg.dispose();
    } else {
      // multiple program execution
      int minutes = 3;
      for (int i = 0; i < programArr.length; i++) {
        addToReminderList(programArr[i], minutes);
      }
    }
  }

  

  public void execute() {
    JDialog dlg= new ReminderListDialog(parent, reminderList);
    dlg.setSize(600,350);
    UiUtilities.centerAndShow(dlg);
    dlg.dispose();
  }

  
  
  private void addToReminderList(Program program, int minutes) {
    if (reminderList == null ) {
      reminderList = new ReminderList();
    }
    
    ReminderListItem item = reminderList.getItemWithProgram(program);
    if (item == null) {
      program.mark(this);
      item = new ReminderListItem(program, minutes);
      reminderList.add(item);
    } else {
      item.setReminderMinutes(minutes);
    }
  }
  
  
  
  public String getMarkIconName() { return "reminderplugin/TipOfTheDay16.gif" ; }
  public String getButtonIconName() { return "reminderplugin/TipOfTheDay16.gif" ; }

  public boolean supportMultipleProgramExecution() {
    return true;
  }
  
} 