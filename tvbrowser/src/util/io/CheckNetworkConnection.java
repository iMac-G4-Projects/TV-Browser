/*
 * TV-Browser
 * Copyright (C) 04-2003 Martin Oberhauser (martin@tvbrowser.org)
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
package util.io;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import tvbrowser.ui.mainframe.MainFrame;
import util.ui.UiUtilities;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Checks the Network and creates a Waiting-Dialog if neccesarry
 */
class CheckNetworkConnection {
  private static final util.ui.Localizer mLocalizer = util.ui.Localizer.getLocalizerFor(CheckNetworkConnection.class);

  private boolean mCheckRunning = true;

  private boolean mResult = false;

  private JDialog mWaitingDialog;

  /**
   * Check the Network
   * 
   * @return true, if the connection is working
   */
  public boolean checkConnection() {
    // Start Check in second Thread
    new Thread(new Runnable() {
      public void run() {
        mCheckRunning = true;
        mResult = false;
        try {
          URL url = new URL("http://tvbrowser.org");

          HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          connection.setConnectTimeout(15000);

          mResult = (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
              || (connection.getResponseCode() == HttpURLConnection.HTTP_SEE_OTHER);
        } catch (IOException e1) {
          e1.printStackTrace();
        }

        mCheckRunning = false;
      };
    }).start();

    int num = 0;
    // Wait till second Thread is finished
    while (mCheckRunning) {
      num++;
      if (num == 7) {
        // Show the Dialog after 700 MS
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            showDialog();
          };
        });
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    hideDialog();
    return mResult;
  }

  private void hideDialog() {
    mCheckRunning = false;
    if ((mWaitingDialog != null) && (mWaitingDialog.isVisible())) {
      mWaitingDialog.setVisible(false);
    }
  }

  private void showDialog() {
    if ((mCheckRunning) && (mWaitingDialog == null)) {
      Window comp = UiUtilities.getLastModalChildOf(MainFrame.getInstance());
      if (comp instanceof Dialog) {
        mWaitingDialog = new JDialog((Dialog) comp, false);
      } else {
        mWaitingDialog = new JDialog((Frame) comp, false);
      }
      mWaitingDialog.setUndecorated(true);
      mWaitingDialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));

      JPanel panel = (JPanel) mWaitingDialog.getContentPane();
      panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

      panel.setLayout(new FormLayout("3dlu, pref, 3dlu", "3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu"));
      CellConstraints cc = new CellConstraints();

      JLabel header = new JLabel(mLocalizer.msg("header", "Header"));
      header.setFont(header.getFont().deriveFont(Font.BOLD));

      panel.add(header, cc.xy(2, 2));

      panel.add(
          new JLabel(mLocalizer.msg("pleaseWait", "Please Wait")), cc
              .xy(2, 4));

      JProgressBar bar = new JProgressBar();
      bar.setIndeterminate(true);

      panel.add(bar, cc.xy(2, 6));

      mWaitingDialog.pack();
      UiUtilities.centerAndShow(mWaitingDialog);
    }
  }
}