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
package devplugin;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contains all the field types of a program.
 * 
 * @see Program#getBinaryField(ProgramFieldType)
 * @see Program#getTextField(ProgramFieldType)
 * @see Program#getIntField(ProgramFieldType)
 * @see Program#getTimeField(ProgramFieldType)
 * @author Til Schneider, www.murfman.de
 */
public class ProgramFieldType {

  private static final util.ui.Localizer mLocalizer
    = util.ui.Localizer.getLocalizerFor(ProgramFieldType.class);

  private static final ArrayList mKnownTypeList = new ArrayList();
  private static ProgramFieldType[] mKnownTypeArray;
  
  public static final int UNKOWN_FORMAT = 1;
  public static final int BINARY_FORMAT = 2;
  public static final int TEXT_FORMAT = 3;
  public static final int INT_FORMAT = 4;
  public static final int TIME_FORMAT = 5;

  public static final ProgramFieldType START_TIME_TYPE
    = new ProgramFieldType(1, TIME_FORMAT, true, "start time",
                           "startTime", "Start time");
                           
  public static final ProgramFieldType END_TIME_TYPE
    = new ProgramFieldType(2, TIME_FORMAT, true, "end time",
                           "endTime", "End time");
                           
  public static final ProgramFieldType TITLE_TYPE
    = new ProgramFieldType(3, TEXT_FORMAT, true, "title",
                           "title", "Title");
    
  public static final ProgramFieldType ORIGINAL_TITLE_TYPE
    = new ProgramFieldType(4, TEXT_FORMAT, true, "original title",
                           "originalTitle", "Original title");
    
  public static final ProgramFieldType EPISODE_TYPE
    = new ProgramFieldType(5, TEXT_FORMAT, true, "episode",
                           "episode", "Episode");
    
  public static final ProgramFieldType ORIGINAL_EPISODE_TYPE
    = new ProgramFieldType(6, TEXT_FORMAT, true, "original episode",
                           "originalEpisode", "Original episode");
    
  public static final ProgramFieldType SHORT_DESCRIPTION_TYPE
    = new ProgramFieldType(7, TEXT_FORMAT, true, "short description",
                           "shortDescription", "Short description");

  public static final ProgramFieldType DESCRIPTION_TYPE
    = new ProgramFieldType(8, TEXT_FORMAT, true, "description",
                           "description", "Description");

  public static final ProgramFieldType IMAGE_TYPE
    = new ProgramFieldType(9, BINARY_FORMAT, true, "image",
                           "image", "Image");

  public static final ProgramFieldType ACTOR_LIST_TYPE
    = new ProgramFieldType(10, TEXT_FORMAT, true, "actor list",
                           "actors", "Actors");

  public static final ProgramFieldType DIRECTOR_TYPE
    = new ProgramFieldType(11, TEXT_FORMAT, true, "director",
                           "director", "Director");

  public static final ProgramFieldType SHOWVIEW_NR_TYPE
    = new ProgramFieldType(12, TEXT_FORMAT, true, "showview number",
                           "showview", "Showview");

  public static final ProgramFieldType INFO_TYPE
    = new ProgramFieldType(13, INT_FORMAT, true, "info bits",
                           "formatInfo", "Format information");

  public static final ProgramFieldType AGE_LIMIT_TYPE
    = new ProgramFieldType(14, INT_FORMAT, true, "age limit",
                           "ageLimit", "Age limit");

  public static final ProgramFieldType URL_TYPE
    = new ProgramFieldType(15, TEXT_FORMAT, true, "film url",
                           "filmUrl", "Website");
                           
  public static final ProgramFieldType GENRE_TYPE
    = new ProgramFieldType(16, TEXT_FORMAT, true, "genre",
                           "genre", "Genre");                         

  public static final ProgramFieldType ORIGIN_TYPE
    = new ProgramFieldType(17, TEXT_FORMAT, true, "origin",
                           "origin", "Origin");

  public static final ProgramFieldType NET_PLAYING_TIME_TYPE
    = new ProgramFieldType(18, INT_FORMAT, true, "net playing time",
                           "netPlayingTime", "Net playing time");

  public static final ProgramFieldType VPS_TYPE
    = new ProgramFieldType(19, TIME_FORMAT, true, "vps",
                           "vps", "VPS");

  public static final ProgramFieldType SCRIPT_TYPE
    = new ProgramFieldType(20, TEXT_FORMAT, true, "script",
                           "script", "Script");

  public static final ProgramFieldType REPETITION_OF_TYPE
    = new ProgramFieldType(21, TEXT_FORMAT, true, "repetition of",
                           "repetitionOf", "Repetition of");
   
  public static final ProgramFieldType MUSIC_TYPE
    = new ProgramFieldType(22, TEXT_FORMAT, true, "music",
                           "music", "Music");
    
  public static final ProgramFieldType MODERATION_TYPE
    = new ProgramFieldType(23, TEXT_FORMAT, true, "moderation",
                           "moderation", "Moderation");
    
  public static final ProgramFieldType PRODUCTION_YEAR_TYPE
    = new ProgramFieldType(24, INT_FORMAT, true, "production year",
                           "productionYear", "Production year");

  public static final ProgramFieldType REPETITION_ON_TYPE
      = new ProgramFieldType(25, TEXT_FORMAT, true, "repetition on",
                           "repetitionOn", "Repetition on");

  private int mTypeId;

  private String mName;

  private String mLocalizedName;
  
  private String mLocalizerKey, mLocalizerDefaultMsg;
  
  private int mFormat;



  /**
   * @param typeId
   * @param name
   */
  private ProgramFieldType(int typeId, int format, boolean isKnownType,
    String name, String localizerKey, String localizerDefaultMsg)
  {
    mTypeId = typeId;
    mFormat = format;
    mName = name;
    mLocalizedName = null;
    mLocalizerKey = localizerKey;
    mLocalizerDefaultMsg = localizerDefaultMsg;
    
    if (isKnownType) {
      mKnownTypeList.add(this);
      int maxTypeId = 0;
      for (int i=0;i<mKnownTypeList.size();i++) {
      	maxTypeId = Math.max(maxTypeId, ((ProgramFieldType) mKnownTypeList.get(i)).getTypeId());
      }
      mKnownTypeArray=new ProgramFieldType[maxTypeId+1];
      for (int i=0;i<mKnownTypeList.size();i++) {
        ProgramFieldType type=(ProgramFieldType) mKnownTypeList.get(i);
        mKnownTypeArray[type.getTypeId()] = type;
      }
    }
  }
  
  
  
  public static ProgramFieldType getTypeForId(int typeId) {
    if (typeId< mKnownTypeArray.length) {
      return mKnownTypeArray[typeId];
    }
    
    return new ProgramFieldType(typeId, UNKOWN_FORMAT, false,
                                "unknown (" + typeId + ")","unknown", "Unknown");
  }
  
  
  public static String getFormatName(int format) {
    switch (format) {
      case BINARY_FORMAT: return "binary format";
      case TEXT_FORMAT: return "text format";
      case INT_FORMAT: return "int format";
      case TIME_FORMAT: return "time format";
      default: return "unknown format";
    }
  }
  
  
  public static Iterator getTypeIterator() {
    return mKnownTypeList.iterator();
  }


 
  public int getTypeId() {
    return mTypeId;
  }


 
  public String getName() {
    return mName;
  }
  

  public String getLocalizedName() {
    if(mLocalizedName == null)
      mLocalizedName = mLocalizer.msg(mLocalizerKey, mLocalizerDefaultMsg);
    
    return mLocalizedName; 
  }


  public int getFormat() {
    return mFormat;
  }
  
  
  public boolean isRightFormat(int format) {
    return (mFormat == format)
      || (format == UNKOWN_FORMAT) || (mFormat == UNKOWN_FORMAT);
  }
  
  
  public String toString() {
    // We return the localized name here. This way ProgramFieldType objects
    // can be used directly in GUI components like JLists etc.
    return getLocalizedName();
  }
  
}
