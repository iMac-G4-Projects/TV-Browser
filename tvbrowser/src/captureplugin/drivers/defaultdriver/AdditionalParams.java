/*
 * CapturePlugin by Andreas Hessel (Vidrec@gmx.de), Bodo Tasche
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
package captureplugin.drivers.defaultdriver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import util.ui.ImageUtilities;
import util.ui.Localizer;
import util.ui.UiUtilities;
import captureplugin.drivers.defaultdriver.configpanels.ParamDescriptionPanel;

/**
 * A DialogBox for the additional Parameters
 */
public class AdditionalParams extends JDialog {
    /** Translator */
    private static final Localizer mLocalizer = Localizer.getLocalizerFor(AdditionalParams.class);
    
    
    /** List of ParamEntries */
    private JList mList;
    /** ListModell */
    private DefaultListModel mListModel;
    /** Current Name */
    private JTextField mName;
    /** Current Params */
    private JTextArea mParam;
    /** Current ParamEnty */
    private ParamEntry selectedEntry;
    /** Config */
    private DeviceConfig mConfig;
    /** currently deleting */
    private boolean mDeleting = false;
    
    /**
     * Create Dialog
     * @param parent Parent Dialog
     * @param config Configuration
     */
    public AdditionalParams(JDialog parent, DeviceConfig config) {
        super(parent, true);
        mConfig = config;
        
        fillModel(config);
        createGUI();
    }

    private void fillModel(DeviceConfig config) {

      Vector vec = new Vector(config.getParamList());
      mListModel = new DefaultListModel();
      
      for (int i=0;i<vec.size();i++) {
        mListModel.addElement(vec.get(i));
      }
      
      if (vec.size() == 0) {
        mListModel.addElement(new ParamEntry());
      }
      
    }
    
    /**
     *  Create GUI
     */
    private void createGUI() {
        setTitle(mLocalizer.msg("Additional","Additional Parameters"));

        JPanel content = (JPanel) getContentPane();
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        content.setLayout(new BorderLayout());

        content.add(createListPanel(), BorderLayout.WEST);

        content.add(createDetailsPanel(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton ok = new JButton(mLocalizer.msg("OK","OK"));
        JButton cancel = new JButton(mLocalizer.msg("Cancel","Cancel"));

        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                okPressed();
            }

        });

        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                hide();
            }

        });

        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        content.add(buttonPanel, BorderLayout.SOUTH);

        mList.setSelectedIndex(0);
        
        setSize(550, 450);
    }

    /**
     * Create List-Panel
     * @return List-Panel
     */
    private Component createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 2));

        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        mList = new JList(mListModel);

        mList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                selectionChanged();
            }

        });

        panel.add(new JScrollPane(mList), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton add = new JButton(ImageUtilities.createImageIconFromJar("captureplugin/drivers/defaultdriver/imgs/New16.gif",this.getClass()));
        
        add.setToolTipText(mLocalizer.msg("Add","Add"));

        add.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addPressed();
            }

        });

        buttons.add(add);

        JButton remove = new JButton(ImageUtilities.createImageIconFromJar("captureplugin/drivers/defaultdriver/imgs/Delete16.gif",this.getClass()));
        remove.setToolTipText(mLocalizer.msg("Remove","Remove"));

        remove.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                removePressed();
            }

        });

        buttons.add(remove);

        JButton up = new JButton(ImageUtilities.createImageIconFromJar("captureplugin/drivers/defaultdriver/imgs/Up16.gif",this.getClass()));
        up.setToolTipText(mLocalizer.msg("Remove","Remove"));
        buttons.add(up);

        up.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent e) {
            upPressed();
          }
          
        });
        
        JButton down = new JButton(ImageUtilities.createImageIconFromJar("captureplugin/drivers/defaultdriver/imgs/Down16.gif",this.getClass()));
        down.setToolTipText(mLocalizer.msg("Remove","Remove"));
        buttons.add(down);

        down.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent e) {
            downPressed();
          }
          
        });
        
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private void upPressed() {
      UiUtilities.moveSelectedItems(mList, -1);
    }
    
    private void downPressed() {
      UiUtilities.moveSelectedItems(mList, 1);
    }
    
    /**
     * Create Details-Panel
     * @return Details-Panel
     */
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.weighty = 0.5;
        c.weightx = 1.0;
        c.insets = new Insets(0, 0, 5, 0);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;

        GridBagConstraints l = new GridBagConstraints();

        l.weightx = 1.0;
        l.insets = new Insets(0, 0, 5, 0);
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridwidth = GridBagConstraints.REMAINDER;

        panel.add(new JLabel(mLocalizer.msg("Name","Name")+":"), l);

        mName = new JTextField();

        panel.add(mName, l);

        panel.add(new JLabel(mLocalizer.msg("Parameter","Parameter")+":"), l);

        mParam = new JTextArea();

        mParam.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    ke.consume();
                }
            }
        });

        panel.add(new JScrollPane(mParam), c);

        panel.add(new JScrollPane(new ParamDescriptionPanel()), c);
        return panel;
    }
    
    /**
     * OK was pressed 
     */
    protected void okPressed() {
        saveSelected();
        
        Vector l = new Vector();
        
        for (int i = 0; i < mListModel.size(); i++) {
            ParamEntry e = (ParamEntry) mListModel.get(i);
            
            if ((e.getName().trim().length() > 0) || (e.getParam().trim().length() > 0)) {
                if (e.getName().trim().length() == 0) {
                    e.setName("?");
                }
                
                l.add(e);
            }
            
        }
        
        mConfig.setParamList(l);
        hide();
    }
    
    /**
     *  Remove was pressed
     */
    protected void removePressed() {
        if (mList.getSelectedValue() == null) { return; }

        mDeleting = true;
        int result = JOptionPane.showConfirmDialog(this, mLocalizer.msg("Delete","Delete Parameter?"),mLocalizer.msg("Additional","Additional Parameters"), JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {

            selectedEntry = null;

            int num = mList.getSelectedIndex();

            mListModel.removeElement(mList.getSelectedValue());

            if (num+1 > mListModel.size()) {
                mList.setSelectedIndex(mListModel.size()-1);
            } else if (mListModel.size() > 0) {
                mList.setSelectedIndex(num);
            }
        }
        mDeleting = false;
    }

    /**
     *  Add was pressed
     */
    protected void addPressed() {
        ParamEntry n = new ParamEntry();
        mListModel.addElement(n);
        mList.setSelectedValue(n, true);
    }

    /**
     * Save data
     */
    private void saveSelected() {
        if ((selectedEntry != null)){
            selectedEntry.setName(mName.getText());
            selectedEntry.setParam(mParam.getText());
        }
    }

    /**
     * Selection changed
     */
    private void selectionChanged() {
        
        if (mDeleting) {
            return;
        }
        
        saveSelected();

        if ((ParamEntry) mList.getSelectedValue() != null) {
            selectedEntry = (ParamEntry) mList.getSelectedValue();
            mName.setText(selectedEntry.getName());
            mParam.setText(selectedEntry.getParam());
        }
    }

}