package movieawardplugin;

import devplugin.SettingsTab;

import javax.swing.JPanel;
import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import util.ui.Localizer;
import util.ui.UiUtilities;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.factories.Borders;

import java.awt.Color;

public class MovieAwardSettingsTab implements SettingsTab {
  /**
   * Translator
   */
  private static final Localizer mLocalizer = Localizer.getLocalizerFor(MovieAwardSettingsTab.class);

  private MovieAwardPlugin mPlugin;

  public MovieAwardSettingsTab(MovieAwardPlugin movieAwardPlugin) {
    mPlugin = movieAwardPlugin;
  }

  public JPanel createSettingsPanel() {
    final JPanel panel = new JPanel(new FormLayout("fill:min:grow", "fill:min:grow"));
    panel.setBorder(Borders.DLU4_BORDER);
    CellConstraints cc = new CellConstraints();

    StringBuilder builder = new StringBuilder();

    builder.append("<html><body>");

    builder.append(mLocalizer.msg("description", "Ths Plugin contains a list of winners for this awards :")).append("<br>");

    builder.append("<ul>");

    for (MovieAward award : mPlugin.getMovieAwards()) {
      builder.append("<li>");

      if (award.getUrl() != null) {
        builder.append("<a href=\"").append(award.getUrl()).append("\">");
      }
      builder.append(award.getName());
      if (award.getUrl() != null) {
        builder.append("</a>");
      }

      if (award.getProviderName() != null) {
        builder.append(" ").append(mLocalizer.msg("provided", "provided by")).append(" ");
        if (award.getProviderUrl() != null) {
          builder.append("<a href=\"").append(award.getProviderUrl()).append("\">");
        }
        builder.append(award.getProviderName());
        if (award.getProviderUrl() != null) {
          builder.append("</a>");
        }
      }

      builder.append("</li>");
    }

    builder.append("</ul>");

    builder.append("</body></html>");

    System.out.println(builder);

    JEditorPane editor = UiUtilities.createHtmlHelpTextArea(builder.toString(), Color.WHITE);
    editor.setBackground(Color.WHITE);
    editor.setOpaque(true);

    
    panel.add(new JScrollPane(editor), cc.xy(1,1));

    return panel;
  }

  public void saveSettings() {
  }

  public Icon getIcon() {
    return mPlugin.getPluginIcon();
  }

  public String getTitle() {
    return mLocalizer.msg("title", "Movie Awards");
  }

}