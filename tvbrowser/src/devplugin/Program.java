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

package devplugin;

import java.util.Iterator;

import javax.swing.event.ChangeListener;

/**
 * This interface provides a view of the program object in the host-application.
 *
 * @author Martin Oberhauser
 */
public interface Program {

  public static final int INFO_VISION_BLACK_AND_WHITE  = 1 << 1;
  public static final int INFO_VISION_4_TO_3           = 1 << 2;
  public static final int INFO_VISION_16_TO_9          = 1 << 3;
  public static final int INFO_AUDIO_MONO              = 1 << 4;
  public static final int INFO_AUDIO_STEREO            = 1 << 5;
  public static final int INFO_AUDIO_DOLBY_SURROUND    = 1 << 6;
  public static final int INFO_AUDIO_DOLBY_DIGITAL_5_1 = 1 << 7;
  public static final int INFO_AUDIO_TWO_CHANNEL_TONE  = 1 << 8;
  public static final int INFO_SUBTITLE                = 1 << 9;
  public static final int INFO_LIVE                    = 1 << 10;

  
  
  /**
   * Adds a ChangeListener to the program.
   *
   * @param listener the ChangeListener to add
   * @see #removeChangeListener
   */
  public void addChangeListener(ChangeListener listener);
  
  /**
   * Removes a ChangeListener from the program.
   *
   * @param listener the ChangeListener to remove
   * @see #addChangeListener
   */
  public void removeChangeListener(ChangeListener listener);  
  
  /**
   * Gets the ID of this program. This ID is unique for a certain date.
   *
   * @return The ID of this program.
   */
  public String getID();

  public String getTitle();
  public String getShortInfo();
  public String getDescription();
  public String getActors();
  public String getURL();
  public int getMinutes();
  public int getHours();
  
  /**
   * Gets the length of this program in minutes.
   *
   * @return the length.
   */
  public int getLength();
  
  public int getInfo();
  public String getTimeString();
  public String getDateString();

  public Channel getChannel();
  public Date getDate();
  public byte[] getPicture();

  public void mark(Plugin plugin);

  public void unmark(Plugin plugin);

  /**
   * Sets whether this program is marked as "on air".
   */
  public void markAsOnAir(boolean onAir);
  
  /**
   * Gets whether this program is marked as "on air".
   */
  public boolean isOnAir();

  /**
   * Gets all {@link devplugin.Plugin}s that have marked this program.
   */
  public Plugin[] getMarkedByPlugins();

  /**
   * Gets whether this program is expired.
   */
  public boolean isExpired();  
  
}