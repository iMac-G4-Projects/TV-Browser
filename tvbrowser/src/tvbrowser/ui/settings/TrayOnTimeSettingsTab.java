package tvbrowser.ui.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tvbrowser.core.Settings;
import tvbrowser.ui.settings.util.ColorButton;
import tvbrowser.ui.settings.util.ColorLabel;
import util.ui.Localizer;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import devplugin.SettingsTab;

/**
 * The settings tab for the ON_TIME_TYPE of the ProgramMenuItem.
 * 
 * @author Ren� Mach
 *
 */
public class TrayOnTimeSettingsTab implements SettingsTab {

  private JCheckBox mIsEnabled, mShowName, mShowIcon, mShowTime, mShowToolTip, mShowProgress;
  private static final Localizer mLocalizer = TrayBaseSettingsTab.mLocalizer;
  private JLabel mSeparator1, mSeparator2, mDarkLabel, mLightLabel, mHelpLabel; 
  private static boolean mTrayIsEnabled = Settings.propTrayIsEnabled.getBoolean();
  
  private ColorLabel mLightColorLb,mDarkColorLb;
  private ColorButton mLight, mDark;
  
  private static TrayOnTimeSettingsTab mInstance;
  
  public JPanel createSettingsPanel() {
    mInstance = this;
    
    CellConstraints cc = new CellConstraints();
    PanelBuilder builder = new PanelBuilder(new FormLayout("5dlu,12dlu,pref:grow,5dlu",
        "pref,5dlu,pref,10dlu,pref,5dlu,pref,pref,pref,pref,pref,3dlu,pref,fill:pref:grow,pref"));
    builder.setDefaultDialogBorder();
    
    mIsEnabled = new JCheckBox(mLocalizer.msg("onTimeEnabled","Show programs at..."),Settings.propTrayOnTimeProgramsEnabled.getBoolean());
        
    mShowName = new JCheckBox(mLocalizer.msg("showName","Show channel name"),Settings.propTrayOnTimeProgramsContainsName.getBoolean());
    mShowIcon = new JCheckBox(mLocalizer.msg("showIcon","Show channel icon"),Settings.propTrayOnTimeProgramsContainsIcon.getBoolean());
    mShowTime = new JCheckBox(mLocalizer.msg("showTime","Show start time"),Settings.propTrayOnTimeProgramsContainsTime.getBoolean());
    mShowToolTip = new JCheckBox(mLocalizer.msg("showToolTip","Show additional information of the program in a tool tip"),Settings.propTrayOnTimeProgramsContainsToolTip.getBoolean());
    mShowToolTip.setToolTipText(mLocalizer.msg("toolTipTip","Tool tips are small helper to something, like this one."));
    mShowProgress = new JCheckBox(mLocalizer.msg("showProgress","Show progress bar"), Settings.propTrayOnTimeProgramsShowProgress.getBoolean());
    
    mLightColorLb = new ColorLabel(Settings.propTrayOnTimeProgramsLightBackground.getColor());
    mLightColorLb.setStandardColor(Settings.propTrayOnTimeProgramsLightBackground.getDefaultColor());
    mDarkColorLb = new ColorLabel(Settings.propTrayOnTimeProgramsDarkBackground.getColor());
    mDarkColorLb.setStandardColor(Settings.propTrayOnTimeProgramsDarkBackground.getDefaultColor());
    
    mHelpLabel = new JLabel();
    
    mLight = new ColorButton(mLightColorLb);
    mDark = new ColorButton(mDarkColorLb);
    
    PanelBuilder colors = new PanelBuilder(new FormLayout(
        "default,5dlu,default,5dlu,default", "pref,2dlu,pref"));    
    
    mDarkLabel = colors.addLabel(
        mLocalizer.msg("progressLight",
            "Background color of the programs at..."), cc.xy(1, 1));
    colors.add(mLightColorLb, cc.xy(3, 1));
    colors.add(mLight,cc.xy(5, 1));

    mLightLabel = colors.addLabel(
        mLocalizer.msg("progressDark",
            "Progress color of the programs at..."), cc.xy(1, 3));
    colors.add(mDarkColorLb, cc.xy(3, 3));
    colors.add(mDark,cc.xy(5, 3));   
        
    JPanel c = (JPanel) builder.addSeparator(mLocalizer.msg("onTime","Programs at..."), cc.xyw(1,1,4));
    builder.add(mIsEnabled, cc.xyw(2,3,2));

    JPanel c1 = (JPanel) builder.addSeparator(mLocalizer.msg("settings","Settings"), cc.xyw(1,5,4));
    
    builder.add(mShowName, cc.xyw(2,7,2));
    builder.add(mShowIcon, cc.xyw(2,8,2));
    builder.add(mShowTime, cc.xyw(2,9,2));
    builder.add(mShowToolTip, cc.xyw(2,10,2));
    builder.add(mShowProgress, cc.xyw(2,11,2));
    builder.add(colors.getPanel(), cc.xy(3,13));
    builder.add(mHelpLabel, cc.xyw(1,15,4));
    
    mSeparator1 = (JLabel)c.getComponent(0);
    mSeparator2 = (JLabel)c1.getComponent(0);
    
    setEnabled(true);
    
    mIsEnabled.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setEnabled(false);
      }
    });
    
    mShowProgress.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        mLightColorLb.setEnabled(mIsEnabled.isSelected() && mShowProgress.isSelected());
        mDarkColorLb.setEnabled(mIsEnabled.isSelected() && mShowProgress.isSelected());
        mLight.setEnabled(mIsEnabled.isSelected() && mShowProgress.isSelected());
        mDark.setEnabled(mIsEnabled.isSelected() && mShowProgress.isSelected());
        mDarkLabel.setEnabled(mIsEnabled.isSelected() && mShowProgress.isSelected());
        mLightLabel.setEnabled(mIsEnabled.isSelected() && mShowProgress.isSelected());
      }
    });
    
    return builder.getPanel();
  }
  
  private void setEnabled(boolean trayStateChange) {
    if(mTrayIsEnabled)
      mHelpLabel.setText("");
    else
      mHelpLabel.setText(mLocalizer.msg("help","<html>The Tray is deactivated. To activate these settings activate the option <b>Tray activated</b> in the Tray Base settings.</html>"));
    
    if(trayStateChange) {
      mSeparator1.setEnabled(mTrayIsEnabled);
      mIsEnabled.setEnabled(mTrayIsEnabled);
    }
    
    TrayProgramsChannelsSettingsTab.setOnTimeIsEnabled(mIsEnabled.isSelected());
    mSeparator2.setEnabled(mTrayIsEnabled);
    mIsEnabled.setEnabled(mTrayIsEnabled);
    mShowName.setEnabled(mIsEnabled.isSelected() && mTrayIsEnabled);
    mShowIcon.setEnabled(mIsEnabled.isSelected() && mTrayIsEnabled);
    mShowTime.setEnabled(mIsEnabled.isSelected() && mTrayIsEnabled);
    mShowToolTip.setEnabled(mIsEnabled.isSelected() && mTrayIsEnabled);
    mShowProgress.setEnabled(mIsEnabled.isSelected() && mTrayIsEnabled);
    mLightColorLb.setEnabled(mIsEnabled.isSelected() && mShowProgress.isSelected() && mTrayIsEnabled);
    mDarkColorLb.setEnabled(mIsEnabled.isSelected() && mShowProgress.isSelected() && mTrayIsEnabled);
    mLight.setEnabled(mIsEnabled.isSelected() && mShowProgress.isSelected() && mTrayIsEnabled);
    mDark.setEnabled(mIsEnabled.isSelected() && mShowProgress.isSelected() && mTrayIsEnabled);
    mDarkLabel.setEnabled(mIsEnabled.isSelected() && mShowProgress.isSelected() && mTrayIsEnabled);
    mLightLabel.setEnabled(mIsEnabled.isSelected() && mShowProgress.isSelected() && mTrayIsEnabled);
  }

  public void saveSettings() {
    if(mIsEnabled != null)
      Settings.propTrayOnTimeProgramsEnabled.setBoolean(mIsEnabled.isSelected());
    if(mShowName != null)
      Settings.propTrayOnTimeProgramsContainsName.setBoolean(mShowName.isSelected());
    if(mShowIcon != null)
      Settings.propTrayOnTimeProgramsContainsIcon.setBoolean(mShowIcon.isSelected());
    if(mShowTime != null)
      Settings.propTrayOnTimeProgramsContainsTime.setBoolean(mShowTime.isSelected());
    if(mShowToolTip != null)
      Settings.propTrayOnTimeProgramsContainsToolTip.setBoolean(mShowToolTip.isSelected());
    if(mShowProgress != null)
      Settings.propTrayOnTimeProgramsShowProgress.setBoolean(mShowProgress.isSelected());
    if(mLightColorLb != null)
      Settings.propTrayOnTimeProgramsLightBackground.setColor(mLightColorLb.getColor());
    if(mDarkColorLb != null)
      Settings.propTrayOnTimeProgramsDarkBackground.setColor(mDarkColorLb.getColor());
  }

  public Icon getIcon() {
    return null;
  }

  public String getTitle() {
    return mLocalizer.msg("onTime","Programs at...");
  }
  
  protected static void setTrayIsEnabled(boolean value) {
    mTrayIsEnabled = value;
    if(mInstance != null)
      mInstance.setEnabled(true);
  }
}
