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
package tvbrowserdataservice;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;

import devplugin.Channel;
import devplugin.Date;
import devplugin.PluginInfo;
import devplugin.Version;

import tvbrowserdataservice.file.ChannelList;
import tvbrowserdataservice.file.DayProgramFile;
import tvbrowserdataservice.file.Mirror;
import tvdataservice.SettingsPanel;
import tvdataservice.TvDataBase;
import util.exc.TvBrowserException;
import util.io.DownloadManager;
import util.io.IOUtilities;
import util.tvdataservice.AbstractTvDataService;

/**
 * 
 * 
 * @author Til Schneider, www.murfman.de
 */
public class TvBrowserDataService extends AbstractTvDataService {

  private static java.util.logging.Logger mLog
    = java.util.logging.Logger.getLogger(TvBrowserDataService.class.getName());
  
  private static final int MAX_META_DATA_AGE = 7;
  private static final int MAX_UP_TO_DATE_CHECKS = 10;
  private static final int MAX_LAST_UPDATE_DAYS = 3;
  private static final String PRIMARY_SERVER_URL
    = "http://192.168.1.2/tvdata/";
    // = "http://tvbrowser.sourceforge.net/tvdata/";
  
  private Properties mSettings;
  
  private File mDataDir;
  
  private Channel[] mAvailableChannelArr;
  
  private String[] mSubsribedLevelArr;
  
  private int mDirectlyLoadedBytes;
  
  private ArrayList mChangedProgramFileList;
  
  
  
  public TvBrowserDataService() {
    mSettings = new Properties();
    
    mDataDir = new File("tvbrowsertvdata");
    
    // TODO: Make the subscribed levels configurable
    mSubsribedLevelArr = DayProgramFile.LEVEL_ARR;
  }



  /**
   * Updates the TV data provided by this data service.
   * 
   * @throws TvBrowserException
   */  
  public void updateTvData(TvDataBase dataBase, Channel[] channelArr,
    Date startDate, int dateCount)
    throws TvBrowserException
  {
    mDirectlyLoadedBytes = 0;
    mChangedProgramFileList = new ArrayList();
    
    // Ensure that the data directory is present
    if (! mDataDir.exists()) {
      mDataDir.mkdir();
    }
    
    // load the mirror list
    Mirror[] mirrorArr = loadMirrorList();
    
    // Get a random Mirror that is up to date
    Mirror mirror = chooseUpToDateMirror(mirrorArr);
    
    // Update the channel list
    // NOTE: We have to load the channel list before the programs, because
    //       we need it for the programs.
    updateChannelList(mirror);
    
    // Create a download manager and add all the jobs
    DownloadManager manager = new DownloadManager(mirror.getUrl());
    TvDataBaseUpdater updater = new TvDataBaseUpdater(this, dataBase);
    
    // Add a receive or a update job for each channel and day
    DayProgramReceiveDH receiveDH = new DayProgramReceiveDH(mDataDir, updater);
    DayProgramUpdateDH updateDH   = new DayProgramUpdateDH(mDataDir, updater);
    Date date = startDate;
    for (int day = 0; day < dateCount; day++) {
      for (int levelIdx = 0; levelIdx < mSubsribedLevelArr.length; levelIdx++) {
        String level = mSubsribedLevelArr[levelIdx];
        
        for (int i = 0; i < channelArr.length; i++) {
          addDownloadJob(dataBase, manager, date, level, channelArr[i].getId(),
                         channelArr[i].getCountry(), receiveDH, updateDH);
        }
      }
      
      date = date.addDays(1);
    }
    
    // Let the download begin
    try {
      manager.runDownload();
    }
    finally {
      // Update the programs for which the update suceed in every case
      updater.updateTvDataBase();
    }
  }



  private Mirror[] loadMirrorList() throws TvBrowserException {
    updateMetaFile(PRIMARY_SERVER_URL, Mirror.MIRROR_LIST_FILE_NAME);
    File file = new File(mDataDir, Mirror.MIRROR_LIST_FILE_NAME);
    try { 
      return Mirror.readMirrorListFromFile(file);
    }
    catch (Exception exc) {
      throw new TvBrowserException(getClass(), "error.2",
        "Loading mirror list failed", exc);
    }
  }



  private void updateMetaFile(String serverUrl, String metaFileName) throws TvBrowserException {
    File file = new File(mDataDir, metaFileName);
    
    // Download a new channellist if needed
    if (needsUpdate(file)) {
      String url = serverUrl + metaFileName;
      try {
        IOUtilities.download(new URL(url), file);
        
        mDirectlyLoadedBytes += (int) file.length();
      }
      catch (IOException exc) {
        throw new TvBrowserException(getClass(), "error.1",
          "Downloading file from '{0}' to '{1}' failed",
          url, file.getAbsolutePath(), exc);
      }
    }
  }



  private boolean needsUpdate(File file) {
    if (! file.exists()) {
      return true;
    } else {
      long minLastModified = System.currentTimeMillis()
        - ((long)MAX_META_DATA_AGE * 24L * 60L * 60L * 1000L);
      return file.lastModified() < minLastModified;
    }
  }



  private Mirror chooseUpToDateMirror(Mirror[] mirrorArr)
    throws TvBrowserException
  {
    // Choose a random Mirror
    Mirror mirror = chooseMirror(mirrorArr);
      
    // Check whether the mirror is up to date and available
    for (int i = 0; i < MAX_UP_TO_DATE_CHECKS; i++) {
      try {
        if (mirrorIsUpToDate(mirror)) {
          break;
        } else {
          // This one is not up to date -> choose another one
          Mirror oldMirror = mirror;
          mirror = chooseMirror(mirrorArr);
          mLog.info("Mirror " + oldMirror.getUrl() + " is out of date. Choosing "
            + mirror.getUrl() + " instead.");
        }
      }
      catch (TvBrowserException exc) {
        // This one is not available -> choose another one
        Mirror oldMirror = mirror;
        mirror = chooseMirror(mirrorArr);
        mLog.log(Level.INFO, "Mirror " + oldMirror.getUrl()
          + " is not available. Choosing " + mirror.getUrl() + " instead.", exc);
      }
    }
    
    // Return the mirror
    return mirror;
  }



  private Mirror chooseMirror(Mirror[] mirrorArr)
    throws TvBrowserException
  {
    // Get the total weight
    int totalWeight = 0;
    for (int i = 0; i < mirrorArr.length; i++) {
      totalWeight += mirrorArr[i].getWeight();
    }
    
    // Choose a weight
    int chosenWeight = (int) (Math.random() * totalWeight);
    
    // Find the chosen mirror
    int currWeight = 0;
    for (int i = 0; i < mirrorArr.length; i++) {
      currWeight += mirrorArr[i].getWeight();
      if (currWeight > chosenWeight) {
        return mirrorArr[i];
      }
    }
    
    // We didn't find a mirror? This should not happen -> throw exception
    throw new TvBrowserException(getClass(), "error.3",
      "No mirror found (chosen weight={0}, total weight={1})",
      new Integer(chosenWeight), new Integer(totalWeight));
  }



  private boolean mirrorIsUpToDate(Mirror mirror)
    throws TvBrowserException
  {
    // Load the lastupdate file and parse it
    String url = mirror.getUrl() + "/lastupdate";
    Date lastupdated;
    try {
      byte[] data = IOUtilities.loadFileFromHttpServer(new URL(url));
      mDirectlyLoadedBytes += data.length;
      
      // Parse is. E.g.: '2003-10-09 11:48:45'
      String asString = new String(data);
      int year = Integer.parseInt(asString.substring(0, 4));
      int month = Integer.parseInt(asString.substring(5, 7));
      int day = Integer.parseInt(asString.substring(8, 10));
      lastupdated = new Date(year, month, day);
    }
    catch (Exception exc) {
      throw new TvBrowserException(getClass(), "error.4",
        "Loading lastupdate file failed: {0}",
        url, exc);
    }
    
    return lastupdated.compareTo(new Date().addDays(- MAX_LAST_UPDATE_DAYS)) >= 0;
  }



  private void updateChannelList(Mirror mirror) throws TvBrowserException {
    File file = new File(mDataDir, ChannelList.FILE_NAME);
    if (needsUpdate(file)) {
      String url = mirror.getUrl() + "/" + ChannelList.FILE_NAME;
      try {
        IOUtilities.download(new URL(url), file);
      }
      catch (Exception exc) {
        throw new TvBrowserException(getClass(), "error.6",
          "Server has no channel list: {0}", mirror.getUrl(), exc);
      }
      
      // Invalidate the channel list
      mAvailableChannelArr = null;
    }
  }



  private void addDownloadJob(TvDataBase dataBase, DownloadManager manager,
    Date date, String level, String channelName, String country,
    DayProgramReceiveDH receiveDH, DayProgramUpdateDH updateDH)
    throws TvBrowserException
  {
    String completeFileName = DayProgramFile.getProgramFileName(date,
      country, channelName, level);
    File completeFile = new File(mDataDir, completeFileName);
            
    // Check whether we already have data for this day
    if (completeFile.exists()) {
      // We have data -> Add an update job
              
      // Get the version of the file
      int version;
      try {
        version = DayProgramFile.readVersionFromFile(completeFile);
      }
      catch (Exception exc) {
        throw new TvBrowserException(getClass(), "error.5",
          "Reading version of TV data file failed: {0}",
          completeFile.getAbsolutePath(), exc);
      }
  
      String updateFileName = DayProgramFile.getProgramFileName(date,
        country, channelName, level, version);
              
      manager.addDownloadJob(updateFileName, updateDH);
    } else {
      // We have no data -> add a receive job
      manager.addDownloadJob(completeFileName, receiveDH);
    }
  }
  
  
  
  File getDataDir() {
    return mDataDir;
  }



  Channel getChannel(String country, String channelName) {
    Channel[] channelArr = getAvailableChannels();
    
    for (int i = 0; i < channelArr.length; i++) {
      Channel channel = channelArr[i];
      if (channel.getCountry().equals(country)
        && channel.getId().equals(channelName))
      {
        return channel;
      }
    }
    
    return null;
  }



  /**
   * Called by the host-application during start-up. Implements this method to
   * load your dataservices settings from the file system.
   */
  public void loadSettings(Properties settings) {
    mSettings = settings;
  }



  /**
   * Called by the host-application during shut-down. Implements this method to
   * store your dataservices settings to the file system.
   */
  public Properties storeSettings() {
    return mSettings;
  }



  public boolean hasSettingsPanel() {
    return false;
  }



  public SettingsPanel getSettingsPanel() {
    return null;
  }



  /**
   * Gets the list of the channels that are available by this data service.
   */
  public Channel[] getAvailableChannels() {
    if (mAvailableChannelArr == null) {
      File channelFile = new File(mDataDir, ChannelList.FILE_NAME);
      if (channelFile.exists()) {
        try {
          ChannelList channelList = new ChannelList();
          channelList.readFromFile(channelFile, this);
          mAvailableChannelArr = channelList.createChannelArray();
        }
        catch (Exception exc) {
          mLog.log(Level.WARNING, "Loading channellist failed: "
            + channelFile.getAbsolutePath(), exc);
        }
      }
      
      if (mAvailableChannelArr == null) {
        // There is no channel file or loading failed
        // -> create a list with some channels
        mAvailableChannelArr = createDefaultChannels();
      }
    }
    
    return mAvailableChannelArr;
  }



  private Channel[] createDefaultChannels() {
    TimeZone zone = TimeZone.getTimeZone("MET");
    return new Channel[] {
      new Channel(this, "Premiere 1", "premiere-1", zone, "de"),
      new Channel(this, "Premiere 2", "premiere-2", zone, "de"),
      new Channel(this, "Premiere 3", "premiere-3", zone, "de"),
    };
  }



  /**
   * Gets information about this TvDataService
   */
  public PluginInfo getInfo() {
    return new devplugin.PluginInfo(
      "TV-Browser",
      "Die eigenen TV-Daten des TV-Browser-Projektes",
      "Til Schneider, www.murfman.de",
      new Version(0, 1));
  }

}
