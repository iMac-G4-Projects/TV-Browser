/*
* TV-Browser
* Copyright (C) 04-2003 Martin Oberhauser (martin_oat@yahoo.de)
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


 /**
  * TV-Browser
  * @author Martin Oberhauser
  */

package tvdataloader;

/**
 * To write your own tv-data-loader implement this interface.
 */
public interface TVDataServiceInterface {

  /**
   * Called by the host-application before starting to download.
   */
  public void connect() throws java.io.IOException;

  /**
   * Returns the whole program of the channel on the specified date.
   */
  public AbstractChannelDayProgram downloadDayProgram(devplugin.Date date, devplugin.Channel channel) throws java.io.IOException;

  /**
   * After the download is done, this method is called. Use this method for clean-up.
   */
  public void disconnect() throws java.io.IOException;

  /**
   * Called by the host-application to read the day-program of a channel from the file system.
   * Enter code like "return (AbstractChannelDayProgram)in.readObject();" here.
   */
  public AbstractChannelDayProgram readChannelDayProgram(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException;

}