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
 *     $Date$
 *   $Author$
 * $Revision$
 */
package util.program;

import devplugin.Program;
import devplugin.ProgramFieldType;

/**
 * The program field type for special compounded types.
 * 
 * @author Ren� Mach
 */
public class CompoundedProgramFieldType {
  /** The compounded field type for the episode formating */
  public static CompoundedProgramFieldType EPISODE_COMPOSITION =
    new CompoundedProgramFieldType(-1, new ProgramFieldType[] {ProgramFieldType.EPISODE_NUMBER_TYPE,ProgramFieldType.EPISODE_TOTAL_NUMBER_TYPE,ProgramFieldType.SEASON_NUMBER_TYPE,ProgramFieldType.EPISODE_TYPE}, ProgramFieldType.EPISODE_TYPE.getLocalizedName(), "{0}/{1} - {2}, {3}");
  
  private int mId;
  private ProgramFieldType[] mFieldTypes;
  private String mName;
  private String mFormatString;
  
  /**
   * Creates an instance of this extra type.
   *  
   * @param id The id of the compounded field type. (has to be a negative value!)
   * @param fieldTypes The field type that this type contains.
   * @param name The name for this field type, or <code>null</code> if
   * the name of the first entry in the field type array should be used. 
   * @param formatString The String that contains the formating for the display
   * use {n} as replacement for the values of the n-th field type.
   */
  private CompoundedProgramFieldType(int id, ProgramFieldType[] fieldTypes, String name, String formatString) {
    mId = id;
    mFieldTypes = fieldTypes;
    mName = name;
    mFormatString = formatString;
  }
  
  /**
   * Gets the formated value for the given program for this
   * compounded field type.
   * 
   * @param prog The Program to get the formated value for.
   * @return The formated value.
   */
  public String getFormatedValueForProgram(Program prog) {
    String value = mFormatString;
    boolean found = false;
    
    for(int i = 0; i < mFieldTypes.length; i++) {
      String formatedValue = "";
      
      if(mFieldTypes[i].getFormat() == ProgramFieldType.TEXT_FORMAT) {
        formatedValue = prog.getTextField(mFieldTypes[i]);
      }
      else if(mFieldTypes[i].getFormat() == ProgramFieldType.INT_FORMAT) {
        formatedValue = prog.getIntFieldAsString(mFieldTypes[i]);
      }
      else if(mFieldTypes[i].getFormat() == ProgramFieldType.TIME_FORMAT) {
        formatedValue = prog.getTimeFieldAsString(mFieldTypes[i]);
      }
      
      if(formatedValue == null) {
        formatedValue = "?";
      }
      else {
        found = true;
      }
      
      value = value.replace("{"+i+"}",formatedValue);
    }
    
    return found ? value : null;
  }
  
  /**
   * Gets the name of this compounded field type.
   * 
   * @return The name of this compounded field type.
   */
  public String getName() {
    if(mName != null) {
      return mName;
    }
    else {
      return mFieldTypes[0].getLocalizedName();
    }
  }
  
  public String toString() {
    return getName();
  }
  
  /**
   * Gets the id of this compounded field type.
   * 
   * @return The id of this compounded field type.
   */
  public int getId() {
    return mId;
  }
  
  /**
   * Gets the compounded program field type for the given id.
   * 
   * @param id The id to get the compunded field type for.
   * @return The compounded field type for the given id or
   * <code>null</code> if the compounded field type of the id was not found.
   */
  public static CompoundedProgramFieldType getCompoundedProgramFieldTypeForId(int id) {
    if(id == EPISODE_COMPOSITION.getId()) {
      return EPISODE_COMPOSITION;
    }
    
    return null;
  }
}