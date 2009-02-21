/*
 * SpeechPlugin for TV-Browser
 * Copyright Michael Keppler
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
 * VCS information:
 *     $Date$
 *   $Author$
 * $Revision$
 */
package speechplugin.engine;

import java.util.List;

public abstract class AbstractSpeechEngine {
  public void initialize() {
    // empty initialization
  }

  public abstract List<String> getVoices();

  public abstract void setVoice(String voiceName);

  public abstract void speak(String text);

  public abstract void stopSpeaking();

  public abstract boolean isSpeaking();

  public void shutdown() {
    // empty shutdown
  }
}
