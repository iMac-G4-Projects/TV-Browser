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
 */

package tvbrowser.core;

import java.util.*;
import java.io.*;

import devplugin.Channel;
import tvdataservice.TvDataService;

/**
 * ChannelList contains a list of all available mAvailableChannels in the system.
 * Use this class to subscribe mAvailableChannels.
 * The available mAvailableChannels are listed in the file CHANNEL_FILE.
 *
 * @author Martin Oberhauser
 */
public class ChannelList {

  private static ArrayList mAvailableChannels = new ArrayList();

  private static ArrayList mSubscribedChannels = new ArrayList();

  public static void loadDayLightSavingTimeCorrections() {
    File f=new File(Settings.getUserDirectoryName(),"daylight_correction.txt");
    if (!f.exists()) {
      return; 
    }
    
    FileReader fr;
    BufferedReader reader=null; 
    try {
      fr=new FileReader(f);
      reader=new BufferedReader(fr);
      String line;
      for (;;){
        line=reader.readLine();
        if (line==null) {
          break;
        }
        int pos=line.indexOf('=');
        try {
          String key=line.substring(0,pos);
          String val=line.substring(pos+1);
          if (val!=null) {
            int corr=Integer.parseInt(val);            
            
            pos = key.indexOf(':');
            String dataServiceClassName = key.substring(0,pos);
            String id = key.substring(pos + 1);

            TvDataService dataService
              = TvDataServiceManager.getInstance().getDataService(dataServiceClassName);

            Channel ch=ChannelList.getChannel(dataService,id);
            if (ch!=null) {
              ch.setDayLightSavingTimeCorrection(corr);
            }
            
          }
          
          
          
          
        }catch(IndexOutOfBoundsException e) {
          // ignore
        }
      }
      
    }catch(IOException e) {
      // ignore
    }
    if (reader!=null) {
      try { reader.close(); }catch(IOException exc){}
    }
  }
  
  public static void storeDayLightSavingTimeCorrections() {
    File f=new File(Settings.getUserDirectoryName(),"daylight_correction.txt");
    
    FileWriter fw;
    PrintWriter out=null;
    try {
      fw=new FileWriter(f);
      out=new PrintWriter(fw);
      Channel[] channels=getSubscribedChannels();
        for (int i=0;i<channels.length;i++) {
          int corr=channels[i].getDayLightSavingTimeCorrection();
          if (corr!=0) {
            out.println(channels[i].getDataService().getClass().getName()+":"+channels[i].getId()+"="+corr);  
          }
        }
    }catch(IOException e) {
      // ignore  
    }
    if (out!=null) {
      out.close();
    }
    
  }

  public static void addDataServiceChannels(TvDataService dataService) {
    Channel[] channelArr = dataService.getAvailableChannels();

    for (int i = 0; i < channelArr.length; i++) {
      mAvailableChannels.add(channelArr[i]);
    }
  }



  /**
   * Subscribes a channel
   * @param id the channel's ID
   */
  public static void subscribeChannel(TvDataService dataService, String id) {
	Channel ch = getChannel(dataService, id);
	if (ch!=null) {
		mSubscribedChannels.add(ch);
	}	
  }




  /**
   * Marks the specified mAvailableChannels as 'subscribed'. All other mAvailableChannels become
   * 'unsubscribed'
   *
   * @param mAvailableChannels the subscribed mAvailableChannels (array of String)
   */
  public static void setSubscribeChannels(Channel[] channelArr) {
	mSubscribedChannels = new ArrayList(channelArr.length);
	for (int i = 0; i < channelArr.length; i++) {
      if (channelArr[i] == null) {
        throw new NullPointerException("channel #" + i + " is null!");
      }

      mSubscribedChannels.add(channelArr[i]);
	}
  }


  /**
   * Returns a new Channel object with the specified ID or null, if the
   * given ID does not exist.
   */
  public static Channel getChannel(TvDataService dataService, String id) {
	Iterator iter = mAvailableChannels.iterator();
	while (iter.hasNext()) {
	  Channel channel = (Channel) iter.next();
	  if (channel.getDataService().equals(dataService) && channel.getId().equals(id)) {
		return channel;
	  }
	}
	return null;
  }


  public static int getPos(Channel channel) {
    for (int i = 0; i < mSubscribedChannels.size(); i++) {
      Channel ch = (Channel) mSubscribedChannels.get(i);
      if (ch.equals(channel)) {
        return i;
      }
    }
    return -1;
  }



  /**
   * Returns an Enumeration of all available Channel objects.
   */
  public static Iterator getChannels() {
	return mAvailableChannels.iterator();
  }



  /**
   * Returns true, if the specified channel is currently subscribed.
   */
  public static boolean isSubscribedChannel(Channel channel) {
	if (channel==null) return false;
	for (int i=0;i<mSubscribedChannels.size();i++) {
	  Channel ch=(Channel)mSubscribedChannels.get(i);
	  if (ch!=null && ch.getId()==channel.getId() && ch.getDataService().equals(channel.getDataService())) {
		return true;
	  }
	}
	return false;
  }



  /**
   * Returns the number of subscribed mAvailableChannels.
   */
  public static int getNumberOfSubscribedChannels() {
	return mSubscribedChannels.size();
  }



  /**
   * Returns all subscribed mAvailableChannels.
   */
  public static Channel[] getSubscribedChannels() {
	Channel[] result=new Channel[mSubscribedChannels.size()];
	for (int i=0;i<mSubscribedChannels.size();i++) {
	  result[i]=(Channel)mSubscribedChannels.get(i);
	}
	return result;
  }


}