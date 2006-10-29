/*
 * TV-Browser
 * Copyright (C) 04-2003 Martin Oberhauser (darras@users.sourceforge.net)
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


 /**
  * TV-Browser
  * @author Martin Oberhauser
  */

package tvbrowser.extras.reminderplugin;


import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.ui.Localizer;
import util.ui.UiUtilities;
import util.ui.WindowClosingIf;

public class ReminderDialog extends JDialog implements WindowClosingIf {

  private static final util.ui.Localizer mLocalizer
    = util.ui.Localizer.getLocalizerFor(ReminderDialog.class);

  public static final String[] SMALL_REMIND_MSG_ARR
    = new String[ReminderFrame.REMIND_MSG_ARR.length - 1];

  public static final int[] SMALL_REMIND_VALUE_ARR
    = new int[ReminderFrame.REMIND_VALUE_ARR.length - 1];
  
  static {
    // use the same entries as the ReminderFrame but without "don't remind me"
    System.arraycopy(ReminderFrame.REMIND_MSG_ARR, 1, SMALL_REMIND_MSG_ARR, 0,
      SMALL_REMIND_MSG_ARR.length);
    System.arraycopy(ReminderFrame.REMIND_VALUE_ARR, 1, SMALL_REMIND_VALUE_ARR, 0,
      SMALL_REMIND_VALUE_ARR.length);
  }

  private boolean mOkPressed=false;

  private JComboBox mList;

  private JCheckBox mRememberSettingsCb, mDontShowDialog;
  
  public ReminderDialog(Frame parent, devplugin.Program program, final java.util.Properties settings) {
    super(parent,true);
    createGui(program, settings);
  }
  
  public ReminderDialog(Dialog parent, devplugin.Program program, final java.util.Properties settings) {
    super(parent,true);
    createGui(program, settings);
  }
  
  private void createGui(devplugin.Program program, final java.util.Properties settings) {
    setTitle(mLocalizer.msg("title", "New reminder"));

    UiUtilities.registerForClosing(this);
    
    JPanel contentPane=(JPanel)getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    JPanel northPn = new JPanel();
    northPn.setLayout(new BoxLayout(northPn,BoxLayout.Y_AXIS));

    JLabel titleLabel=new JLabel(program.getChannel().getName()+": "+program.getTitle());
    JPanel infoPanel=new JPanel(new GridLayout(2,1));

    Font font=titleLabel.getFont();

    titleLabel.setFont(new Font(font.getName(),Font.BOLD,font.getSize()+4));

    infoPanel.add(new JLabel(program.getDateString()));
    infoPanel.add(new JLabel(program.getTimeString()));

    JPanel headerPanel=new JPanel(new BorderLayout(20,0));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
    headerPanel.add(titleLabel,BorderLayout.WEST);
    headerPanel.add(infoPanel,BorderLayout.EAST);
    
    
    northPn.add(headerPanel);
    
    mList=new JComboBox(SMALL_REMIND_MSG_ARR);
    
    
    String s=settings.getProperty("defaultReminderEntry");
    int reminderTime = 5;
    if (s!=null) {
      try {
        reminderTime = Integer.parseInt(s);
      }catch(NumberFormatException e) {
        // ignore
      }
    }
    if (reminderTime>=0 && reminderTime<SMALL_REMIND_MSG_ARR.length) {
      mList.setSelectedIndex(reminderTime);
    }
    else {
      mList.setSelectedIndex(5);
    }
    
    northPn.add(mList);
    northPn.add(Box.createRigidArea(new Dimension(0,3)));

    mRememberSettingsCb = new JCheckBox(mLocalizer.msg("rememberSetting","Remember setting"));
    JPanel pn1 = new JPanel(new BorderLayout());
    pn1.add(mRememberSettingsCb, BorderLayout.NORTH);
    
    mDontShowDialog = new JCheckBox(mLocalizer.msg("dontShow","Don't show this dialog anymore"));
    pn1.add(mDontShowDialog, BorderLayout.CENTER);    
    
    pn1.add(new JLabel(mLocalizer.msg("howToChange","You can change the behavior under Settings -> Reminder")), BorderLayout.SOUTH);
    
    northPn.add(pn1);
    
    JPanel btnPn=new JPanel(new FlowLayout(FlowLayout.TRAILING));
    btnPn.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
    
    JButton okBtn=new JButton(Localizer.getLocalization(Localizer.I18N_OK));
    okBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        mOkPressed=true;
        if (mRememberSettingsCb.isSelected()) {
          settings.setProperty("defaultReminderEntry",""+mList.getSelectedIndex());
        }
        settings.setProperty("showTimeSelectionDialog",String.valueOf(!mDontShowDialog.isSelected()));
        hide();
      }
    });
    btnPn.add(okBtn);
    getRootPane().setDefaultButton(okBtn);
    
    JButton cancelBtn=new JButton(Localizer.getLocalization(Localizer.I18N_CANCEL));
    cancelBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hide();
      }
    });
    btnPn.add(cancelBtn);
    
    contentPane.add(northPn,BorderLayout.NORTH);
    contentPane.add(btnPn,BorderLayout.SOUTH);
    
    pack();
  }

  
  
  public int getReminderMinutes() {
    int idx = mList.getSelectedIndex();
    return SMALL_REMIND_VALUE_ARR[idx];
  }

  
  
  public boolean getOkPressed() {
    return mOkPressed;
  }



  public void close() {
    setVisible(false);
  }

}