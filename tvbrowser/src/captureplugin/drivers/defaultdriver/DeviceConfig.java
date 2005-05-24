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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import util.ui.Localizer;
import captureplugin.utils.ChannelComperator;
import devplugin.Channel;


/**
 * The Configuration for this Device
 */
public class DeviceConfig {
    /** Translator */
    private static final Localizer mLocalizer = Localizer.getLocalizerFor(DeviceConfig.class);  
  
    /** Device-Name */
    private String mName = "";
    
    /** Channels */
    private TreeMap mChannels = new TreeMap(new ChannelComperator());

    /** Programs that are marked */
    private ProgramTimeList mMarkedPrograms = new ProgramTimeList();

    /** Path to Program */
    private String mProgramPath = "";

    /** URL to WebFrontend */
    private String mWebURL = "";
    
    /** Use the WebFrontend */
    private boolean mUseWeb = false;
    
    /** Parameters for Add */
    private String mParameterFormatAdd = "";

    /** Parameters for Delete */
    private String mParameterFormatRem = "";

    /** Time to start earlier */
    private int mPreTime = 0;

    /** Time to stop later */
    private int mPostTime = 0;

    /** Username */
    private String mUserName = "";
    
    /** Password */
    private String mPassword = "";    

    /** Use the Return-Value ? */
    private boolean mUseReturnValue = true;

    /** Maximum simultanious recordings */
    private int mMaxSimultanious = 1;
    
    /** Dialog only on Error */
    private boolean mResultDialogOnlyOnError = false;
    
    /** Only Programs in the Future */
    private boolean mOnlyFuture = true;
    
    /** MaximTime to Timeout */
    private int mMaxTimeout = 5;
    
    private ArrayList mParamEntries = new ArrayList();
    
    /**
     * Create a empty Config
     */
    public DeviceConfig() {
        
    }
    
    /**
     * Copy Config
     * @param config
     */
    public DeviceConfig(DeviceConfig data) {
        setChannels((TreeMap)data.getChannels().clone());
        setMarkedPrograms((ProgramTimeList)data.getMarkedPrograms().clone());
        setParameterFormatAdd(data.getParameterFormatAdd());
        setParameterFormatRem(data.getParameterFormatRem());
        setPostTime(data.getPostTime());
        setPreTime(data.getPreTime());
        setProgramPath(data.getProgramPath());
        setWebUrl(data.getWebUrl());
        setUseWebUrl(data.getUseWebUrl());
        setPassword(data.getPassword());
        setUserName(data.getUserName());
        setName(data.getName());
        setUseReturnValue(data.useReturnValue());
        setMaxSimultanious(data.getMaxSimultanious());
        setDialogOnlyOnError(data.getDialogOnlyOnError());
        setOnlyFuturePrograms(data.getOnlyFuturePrograms());
        setParamList(data.getParamList());
        setTimeOut(data.getTimeOut());
    }


    /**
     * @param paramList
     */
    public void setParamList(Collection paramList) {
       mParamEntries = new ArrayList(paramList);
    }

    /**
     * @return
     */
    public Collection getParamList() {
        return mParamEntries;
    }

    /**
     * Get a Collection of all enabled Params
     * @return all enabled Params
     */
    public Collection getEnabledParamList() {
      ArrayList params = new ArrayList();
      
      for (int i=0;i< mParamEntries.size();i++) {
        ParamEntry entry = (ParamEntry)mParamEntries.get(i);
        if (entry.isEnabled()) {
          params.add(entry);
        }
      }
      
      return params;
    }    
    
    /**
     * @param onlyFuturePrograms
     */
    public void setOnlyFuturePrograms(boolean onlyFuturePrograms) {
        mOnlyFuture = onlyFuturePrograms;        
    }

    /**
     * @return
     */
    public boolean getOnlyFuturePrograms() {
        return mOnlyFuture;
    }

    /**
     * @param dialogOnlyOnError
     */
    public void setDialogOnlyOnError(boolean dialogOnlyOnError) {
        mResultDialogOnlyOnError = dialogOnlyOnError;        
    }

    /**
     * @return
     */
    public boolean getDialogOnlyOnError() {
        return mResultDialogOnlyOnError;
    }

    /**
     * @return
     */
    public int getPreTime() {
        return mPreTime;
    }


    /**
     * @param i
     */
    public void setPreTime(int i) {
        mPreTime = i;
    }

    /**
     * @return
     */
    public int getPostTime() {
        return mPostTime;
    }


    /**
     * @param i
     */
    public void setPostTime(int i) {
        mPostTime = i;
    }


    /**
     * @return
     */
    public String getProgramPath() {
        return mProgramPath;
    }

    /**
     * @param string
     */
    public void setProgramPath(String string) {
        mProgramPath = string;
    }

    /**
     * @return
     */
    public boolean getUseWebUrl() {
        return mUseWeb;
    }

    /**
     * @param urlmode
     */
    public void setUseWebUrl(boolean urlmode) {
        mUseWeb = urlmode;
    }


    /**
     * @return
     */
    public String getWebUrl() {
        return mWebURL;
    }

    /**
     * @param url
     */
    public void setWebUrl(String url) {
       mWebURL = url;
        
    }


    /**
     * @param markedPrograms The markedPrograms to set.
     */
    public void setMarkedPrograms(ProgramTimeList markedPrograms) {
        this.mMarkedPrograms = markedPrograms;
    }    
    
    /**
     * @return
     */
    public ProgramTimeList getMarkedPrograms() {
        return mMarkedPrograms;
    }

    /**
     * @return
     */
    public String getPassword() {
        return mPassword;
    }


    /**
     * @param password
     */
    public void setPassword(String password) {
        mPassword = password;
    }

    /**
     * @return
     */
    public String getUserName() {
        return mUserName;
    }


    /**
     * @param user
     */
    public void setUserName(String user) {
        mUserName = user;
    }

    /**
     * @return
     */
    public String getParameterFormatAdd() {
        return mParameterFormatAdd;
    }

    /**
     * @param add
     */
    public void setParameterFormatAdd(String add) {
        mParameterFormatAdd = add;
    }

    /**
     * @return
     */
    public String getParameterFormatRem() {
        return mParameterFormatRem;
    }

    /**
     * @param rem
     */
    public void setParameterFormatRem(String rem) {
        mParameterFormatRem = rem;
    }

    /**
    * @param channels The channels to set.
    */
   public void setChannels(TreeMap channels) {
       this.mChannels = channels;
   }    
    
    /**
     * @return
     */
    public TreeMap getChannels() {
        return mChannels;
    }


    public Object clone() {
        return new DeviceConfig(this);
    }

    /**
     * @param name
     */
    public void setName(String name) {
        mName = name;
    }
    

    /**
     * return
     */
    public String getName() {
        return mName;
    }
    
    /**
     * Use the Return-Value ?
     * @return use Return Value?
     */
    public boolean useReturnValue() {
        return mUseReturnValue;
    }
    
    /**
     * Use the Return Value?
     * @param retvalue Use Return Value ?
     */
    public void setUseReturnValue(boolean retvalue) {
        mUseReturnValue = retvalue;
    }


    /**
     * Gets the maximum simultanious recordings for this device
     * @return max simultanious recordings
     */
    public int getMaxSimultanious() {
        return mMaxSimultanious;
    }
    

    /**
     * Sets the maximum simultanious recordings for this device
     * @param max simultanious recordings
     */
    public void setMaxSimultanious(int max) {
        mMaxSimultanious = max;
    }
    
    /**
     * Set the sec to timeout
     * @param sec Secs
     */
    public void setTimeOut(int sec) {
        mMaxTimeout = sec;
    }
    
    /**
     * Get the Timeout in secs
     * @return Timeout in secs
     */
    public int getTimeOut() {
        return mMaxTimeout;
    }
    
    /**
     * Write the Config into a Stream
     * @param stream
     */
    public void writeData(ObjectOutputStream stream) throws IOException {

        stream.writeInt(4);
        
        stream.writeObject(getName());
        
        writeChannelMappings(stream);
        
        stream.writeObject(getUserName());
        stream.writeObject(getPassword());
        stream.writeBoolean(getUseWebUrl());
        stream.writeObject(getProgramPath());
        stream.writeObject(getWebUrl());
        stream.writeInt(getPreTime());
        stream.writeInt(getPostTime());
        
        stream.writeObject(getParameterFormatAdd());
        stream.writeObject(getParameterFormatRem());
        
        getMarkedPrograms().writeData(stream);
        
        stream.writeBoolean(mUseReturnValue);
        stream.writeInt(mMaxSimultanious);
        stream.writeBoolean(mResultDialogOnlyOnError);
        stream.writeBoolean(mOnlyFuture);
        
        stream.writeInt(mParamEntries.size());
        
        for (int i = 0; i < mParamEntries.size(); i++) {
            ((ParamEntry)mParamEntries.get(i)).writeData(stream);
        }
        
        stream.writeInt(mMaxTimeout);

    }

    /**
     * Read the Config from a Stream
     * @param stream
     */
    public void readData(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int version = stream.readInt();
        
        if (version < 4) {
          throw new IOException(
              mLocalizer.msg("NoOlderVersionSupported",
          "Sorry, older Versions of the CapturePlugin are not longer supported. Please create a new Device!"));
        }
        
        setName((String) stream.readObject());     
        readChannelsMappings(stream);
        setUserName((String) stream.readObject());
        setPassword((String) stream.readObject());
        setUseWebUrl(stream.readBoolean());
        setProgramPath((String) stream.readObject());
        setWebUrl((String) stream.readObject());
        setPreTime(stream.readInt());
        setPostTime(stream.readInt());

        setParameterFormatAdd((String) stream.readObject());
        setParameterFormatRem((String) stream.readObject());
        
        ProgramTimeList list = new ProgramTimeList();
        list.readData(stream);
        
        setMarkedPrograms(list);
        
        if (version == 1) {
            return;
        }
        
        mUseReturnValue = stream.readBoolean();
        mMaxSimultanious = stream.readInt();
        mResultDialogOnlyOnError = stream.readBoolean();
        mOnlyFuture = stream.readBoolean();
        
        int size = stream.readInt();
        
        mParamEntries = new ArrayList();
        
        for (int i = 0; i < size; i++) {
            ParamEntry entry = new ParamEntry();
            entry.readData(stream);
            mParamEntries.add(entry);
        }
        
        
        if (version > 2) {
            mMaxTimeout = stream.readInt();
        }
    }

    /**
     * Write the Channel-Mappings to a Stream
     * @param stream Write to this Stream
     * @throws IOException
     */
    private void writeChannelMappings(ObjectOutputStream stream) throws IOException {
      stream.writeInt(mChannels.keySet().size());
      Iterator it = mChannels.keySet().iterator();

      int i =0;
      while (it.hasNext()) {
        i++;
        Channel channel = (Channel) it.next();
        stream.writeObject(channel.getDataService().getClass().getName());
        stream.writeObject(channel.getId());
        stream.writeObject(mChannels.get(channel));
      }
    }
    
    /**
     * Read the ChannelMappings from a Stream
     * @param stream read from this Stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readChannelsMappings(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      int channelCnt = stream.readInt();
      mChannels.clear();
      for (int i=0; i<channelCnt; i++) {
        String dataServiceClassName = (String)stream.readObject();
        String channelId = (String)stream.readObject();
        String value = (String)stream.readObject();
        Channel ch = Channel.getChannel(dataServiceClassName, channelId); 
        if (ch!=null) {
          mChannels.put(ch, value);
        }
      }
    }


}