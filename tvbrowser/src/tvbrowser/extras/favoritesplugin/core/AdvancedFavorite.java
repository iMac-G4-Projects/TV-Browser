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

package tvbrowser.extras.favoritesplugin.core;

import tvbrowser.extras.favoritesplugin.FavoriteConfigurator;
import tvbrowser.core.filters.ShowAllFilter;
import devplugin.*;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import util.ui.SearchFormSettings;
import util.ui.SearchForm;
import util.exc.TvBrowserException;
import util.exc.ErrorHandler;

import javax.swing.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.PanelBuilder;

public class AdvancedFavorite extends Favorite {

  public static final util.ui.Localizer mLocalizer
        = util.ui.Localizer.getLocalizerFor(AdvancedFavorite.class);


  private SearchFormSettings mSearchFormSettings;

  public static final String TYPE_ID = "advanced";

  private ProgramFilter mFilter;


  public AdvancedFavorite(ObjectInputStream in) throws IOException, ClassNotFoundException {
    super(in);
    in.readInt();   // version
    mSearchFormSettings = new SearchFormSettings(in);
  }


  /**
   * @deprecated
   * @param obj
   * @param in
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public AdvancedFavorite(Object obj, ObjectInputStream in) throws IOException, ClassNotFoundException {
    super();
    readOldFavorite(in);
  }

  public AdvancedFavorite(String searchText) {
    super();
    mSearchFormSettings = new SearchFormSettings(searchText);
  }

  public String getTypeID() {
    return TYPE_ID;
  }

  public String getName() {
    if (super.getName() != null) {
      return super.getName();
    }
    return mSearchFormSettings.getSearchText();
  }

  public FavoriteConfigurator createConfigurator() {
    return new Configurator();
  }

  protected void internalWriteData(ObjectOutputStream out) throws IOException {
    out.writeInt(1); // version
    mSearchFormSettings.writeData(out);
  }



  private void readOldFavorite(ObjectInputStream in) throws IOException, ClassNotFoundException {
    int version = in.readInt();

    if (version <= 2) {
      String term = (String) in.readObject();
      in.readBoolean(); // searchInTitle
      boolean searchInText = in.readBoolean();
      int searchMode = in.readInt();

      mSearchFormSettings.setSearchText(term);
      if (searchInText) {
        mSearchFormSettings.setSearchIn(SearchFormSettings.SEARCH_IN_ALL);
      } else {
        mSearchFormSettings.setSearchIn(SearchFormSettings.SEARCH_IN_TITLE);
      }

      switch (searchMode) {
        case 1: mSearchFormSettings.setSearcherType(PluginManager.SEARCHER_TYPE_EXACTLY); break;
        case 2: mSearchFormSettings.setSearcherType(PluginManager.SEARCHER_TYPE_KEYWORD); break;
        case 3: mSearchFormSettings.setSearcherType(PluginManager.SEARCHER_TYPE_REGULAR_EXPRESSION); break;
      }
    } else {
      mSearchFormSettings = new SearchFormSettings(in);
    }

    if (version >=5) {
      super.setName((String)in.readObject());
    }
    else {
      super.setName(mSearchFormSettings.getSearchText());
    }

    boolean useCertainChannel = in.readBoolean();

    if (version < 6) {
      String certainChannelServiceClassName = (String) in.readObject();
      String certainChannelId;
      if (version==1) {
        certainChannelId=""+in.readInt();
      }else{
        certainChannelId=(String)in.readObject();
      }
      Channel ch = Channel.getChannel(certainChannelServiceClassName, certainChannelId);
      if (ch != null) {
        getLimitationConfiguration().setChannels(new Channel[]{ch});
      }
    }
    else {
      if (useCertainChannel) {
        int cnt = in.readInt();
        ArrayList list = new ArrayList();
        for (int i=0; i<cnt; i++) {
          String certainChannelServiceClassName = (String) in.readObject();
          String certainChannelId;
          if (version==1) {
            certainChannelId=""+in.readInt();
          }else{
            certainChannelId=(String)in.readObject();
          }

          Channel channel = Channel.getChannel(certainChannelServiceClassName, certainChannelId);
          if (channel != null) {
            list.add(channel);
          }
        }

        getLimitationConfiguration().setChannels((Channel[])list.toArray(new Channel[list.size()]));
      }
    }



    boolean useCertainTimeOfDay = in.readBoolean();
    int certainFromTime = in.readInt();
    int certainToTime = in.readInt();

    if (useCertainTimeOfDay) {
      getLimitationConfiguration().setTime(certainFromTime, certainToTime);
    }


    // Don't save the programs but only their date and id
    int size = in.readInt();
    ArrayList programList = new ArrayList(size);
    for (int i = 0; i < size; i++) {
      Date date = new Date(in);
      String progID = (String) in.readObject();
      Program program = Plugin.getPluginManager().getProgram(date, progID);
      if (program != null) {
        programList.add(program);
      }
    }

    Program[] mProgramArr = new Program[programList.size()];
    programList.toArray(mProgramArr);

    if (version >=4) {
        boolean useFilter = in.readBoolean();  // useFilter
        String filterName = (String)in.readObject();
        
        if(useFilter)
          mFilter = getFilterByName(filterName);
        else
          mFilter = null;
    } else {
        mFilter = null;
    }

    if (version >= 7) {
      size = in.readInt();
      for (int i = 0; i < size; i++) {
        /* For compatibility reasons we read the programs here.
           Later we perform an complete refresh.
         */
        Date programDate = new Date(in);
        String programId = (String) in.readObject();
      }
    }

    getReminderConfiguration().setReminderServices(new String[] {});

    try {
      this.updatePrograms();
    } catch (TvBrowserException exc) {
      ErrorHandler.handle("Could not update favorites.", exc);

    }

  }


  /**
   * Returns a specific Filter
   * @param name Name of the Filter
   * @return The Filter if found, otherwise null
   */
  private ProgramFilter getFilterByName(String name ){
    ProgramFilter[] flist = Plugin.getPluginManager().getAvailableFilters();

    for (int i=0; i<flist.length;i++) {
      if (flist[i].getName().equals(name)) {
        return flist[i];
      }
    }

    return null;
  }

  protected Program[] internalSearchForPrograms(Channel[] channelArr) throws TvBrowserException {

    SearchFormSettings searchForm = mSearchFormSettings;

    ProgramSearcher searcher = searchForm.createSearcher();
    Program[] progArr = searcher.search(searchForm.getFieldTypes(),
                                                new devplugin.Date(),
                                                1000,
                                                channelArr,
                                                false
                                                );

    if (mFilter != null) {
      ArrayList list = new ArrayList();
      for (int i=0; i<progArr.length; i++) {
        if (mFilter.accept(progArr[i])) {
          list.add(progArr[i]);
        }
      }
      return (Program[])list.toArray(new Program[list.size()]);
    }
    else {
      return progArr;
    }

  }


  class Configurator implements FavoriteConfigurator {

    private SearchForm mSearchForm;
    private JCheckBox mFilterCheckbox;
    private JComboBox mFilterCombo;
    public Configurator() {

    }

    public JPanel createConfigurationPanel() {
      mSearchForm = new SearchForm(true, false, false, SearchForm.LAYOUT_HORIZONTAL);
      mSearchForm.setSearchFormSettings(mSearchFormSettings);


      CellConstraints cc = new CellConstraints();
      PanelBuilder panelBuilder = new PanelBuilder(new FormLayout("pref, 3dlu, pref, pref:grow", "pref, 5dlu, pref"));

      panelBuilder.add(mSearchForm, cc.xyw(1, 1, 4));
      panelBuilder.add(mFilterCheckbox = new JCheckBox(mLocalizer.msg("useFilter","Use filter:")), cc.xy(1, 3));
      panelBuilder.add(mFilterCombo = new JComboBox(Plugin.getPluginManager().getAvailableFilters()), cc.xy(3, 3));

      if (mFilter != null) {
        mFilterCheckbox.setSelected(true);
        mFilterCombo.setSelectedItem(mFilter);
      }
      else {
        mFilterCombo.setEnabled(false);
      }

      mFilterCheckbox.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
          mFilterCombo.setEnabled(mFilterCheckbox.isSelected());
        }
      });

      return panelBuilder.getPanel();


    }

    public void save() {
      SearchFormSettings settings = mSearchForm.getSearchFormSettings();
      mSearchFormSettings.setCaseSensitive(settings.getCaseSensitive());
      mSearchFormSettings.setNrDays(settings.getNrDays());
      mSearchFormSettings.setSearcherType(settings.getSearcherType());
      mSearchFormSettings.setSearchIn(settings.getSearchIn());
      mSearchFormSettings.setSearchText(settings.getSearchText());
      mSearchFormSettings.setUserDefinedFieldTypes(settings.getUserDefinedFieldTypes());
      if (mFilterCheckbox.isSelected()) {
        mFilter = (ProgramFilter)mFilterCombo.getSelectedItem();
        if (mFilter instanceof ShowAllFilter) {
          mFilter = null;
        }
      }
      else {
        mFilter = null;
      }
    }
  }


}
