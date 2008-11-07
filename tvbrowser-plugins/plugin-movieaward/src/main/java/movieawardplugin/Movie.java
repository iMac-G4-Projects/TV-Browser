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
 *     $Date: 2007-10-02 10:19:08 +0200 (Di, 02 Okt 2007) $
 *   $Author: Bananeweizen $
 * $Revision: 3966 $
 */
package movieawardplugin;

import java.util.HashMap;

public class Movie {
  private String mId;
  private int mYear;
  private String mDirector;
  private HashMap<String, String> mTitle = new HashMap<String, String>();

  public Movie(String id) {
    mId = id;
  }

  public void setProductionYear(int year) {
    mYear = year;
  }

  public void setDirector(String director) {
    mDirector = director;
  }

  public String getId() {
    return mId;
  }

  public void addTitle(String lang, String title) {
    mTitle.put(lang, title);
  }
}