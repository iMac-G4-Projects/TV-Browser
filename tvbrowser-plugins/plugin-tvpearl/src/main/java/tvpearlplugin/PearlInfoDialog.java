/*
 * TV-Pearl by Reinhard Lehrbaum
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
 */
package tvpearlplugin;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import devplugin.*;
import util.browserlauncher.*;
import util.ui.*;
import util.ui.html.*;
import java.text.*;

public class PearlInfoDialog extends JDialog implements WindowClosingIf
{
    protected static final util.ui.Localizer mLocalizer = util.ui.Localizer.getLocalizerFor(PearlInfoDialog.class);

    private static final long serialVersionUID = 1L;

    private JScrollPane mScrollPane;
    private JEditorPane mInfoPane;
    private JButton mCloseBn;
    private TVPProgram mProgram;

    public PearlInfoDialog(Frame parent, TVPProgram program)
    {
        super(parent, true);

        mProgram = program;
        createGUI();
    }

    public PearlInfoDialog(Dialog parent, TVPProgram program)
    {
        super(parent, true);

        mProgram = program;
        createGUI();
    }

    private void createGUI()
    {
        setTitle(mLocalizer.msg("title", "TV Pearl Info"));
        UiUtilities.registerForClosing(this);

        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(UiUtilities.DIALOG_BORDER);
        main.setPreferredSize(new Dimension(500, 350));
        setContentPane(main);

        mInfoPane = new JEditorPane();
        mInfoPane.setEditorKit(new ExtendedHTMLEditorKit());
        mInfoPane.setEditable(false);
        mInfoPane.addHyperlinkListener(new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent evt)
            {
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                {
                    URL url = evt.getURL();
                    if (url != null)
                    {
                        Launch.openURL(url.toString());
                    }
                }
            }
        });
        mScrollPane = new JScrollPane(mInfoPane);
        main.add(mScrollPane, BorderLayout.CENTER);

        JPanel buttonPn = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        main.add(buttonPn, BorderLayout.SOUTH);

        if (mProgram != null)
        {
            JButton GotoBn = new JButton(mLocalizer.msg("goto", "Goto"));
            GotoBn.setVisible(mProgram.getProgramID().length() != 0);

            GotoBn.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    Program p = Plugin.getPluginManager().getProgram(new devplugin.Date(mProgram.getStart()),
                            mProgram.getProgramID());
                    Plugin.getPluginManager().scrollToProgram(p);
                }
            });
            buttonPn.add(GotoBn);
        }

        mCloseBn = new JButton(Localizer.getLocalization(Localizer.I18N_CLOSE));
        mCloseBn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                dispose();
            }
        });
        buttonPn.add(mCloseBn);
        getRootPane().setDefaultButton(mCloseBn);

        pack();

        if (mProgram != null)
        {
            ExtendedHTMLDocument doc = (ExtendedHTMLDocument) mInfoPane.getDocument();
            mInfoPane.setText(createHtmlText(doc, mProgram));
            mScrollPane.getVerticalScrollBar().setValue(0);
        }
        toFront();
    }

    public void close()
    {
        dispose();
    }

    private String createHtmlText(ExtendedHTMLDocument doc, TVPProgram program)
    {
        Font tFont = new Font("Verdana", Font.BOLD, 18);
        Font bFont = new Font("Verdana", Font.PLAIN, 11);
        String titleFont = tFont.getFamily();
        String titleSize = bFont.getFamily();
        String bodyFont = String.valueOf(tFont.getSize());
        String bodyFontSize = String.valueOf(bFont.getSize());

        StringBuffer buffer = new StringBuffer();
        buffer.append("<html><body>");
        buffer.append("<table width=\"100%\" style=\"font-family:");
        buffer.append(bodyFont);
        buffer.append(";\"><tr>");
        buffer.append("<td width=\"60\">");
        buffer.append("<p \"align=center\">");
        buffer.append("</p></td><td>");
        buffer.append("<div style=\"color:#ff0000; font-size:");
        buffer.append(bodyFontSize).append(";\"><b>");
        buffer.append(TVPearlPlugin.getInstance().getDayName(program.getStart(), false)).append(", ");
        buffer.append(DateFormat.getDateInstance().format(program.getStart().getTime()));
        buffer.append(" � ");
        buffer.append(DateFormat.getTimeInstance(DateFormat.SHORT).format(program.getStart().getTime()));
        buffer.append(" � ");
        buffer.append(program.getChannel());
        buffer.append("</b></div><div style=\"color:#003366; font-size:");
        buffer.append(titleSize);
        buffer.append("; line-height:2.5em; font-family:");
        buffer.append(titleFont).append("\"><b>");
        buffer.append(program.getTitle());
        buffer.append("</b></div>");
        buffer.append("</td></tr>");

        addSeperator(doc, buffer);

        buffer.append("<tr><td valign=\"top\" style=\"color:#808080; font-size:");
        buffer.append(bodyFontSize).append("\"><b>");
        buffer.append(mLocalizer.msg("author", "Author"));
        buffer.append("</b></td><td valign=\"middle\" style=\"font-size:font-size:");
        buffer.append(bodyFontSize).append("\">");
        buffer.append(program.getAuthor());
        buffer.append("</td></tr>");

        addSeperator(doc, buffer);

        buffer.append("<tr><td valign=\"top\" style=\"color:#808080; font-size:");
        buffer.append(bodyFontSize).append("\"><b>");
        buffer.append(mLocalizer.msg("info", "Info"));
        buffer.append("</b></td><td valign=\"middle\" style=\"font-size:font-size:");
        buffer.append(bodyFontSize).append("\">");
        buffer.append(program.getInfo().replaceAll("\n", "<br>"));
        buffer.append("</td></tr>");

        addSeperator(doc, buffer);

        buffer.append("<tr><td colspan=\"2\" valign=\"top\" align=\"center\" style=\"color:#808080; font-size:");
        buffer.append(bodyFontSize).append("\">");
        buffer.append("<a href=\"");
        buffer.append(program.getContentUrl()).append("\">");
        buffer.append(mLocalizer.msg("forum", "Forum entry"));
        buffer.append("</a>");
        buffer.append("</td></tr>");

        buffer.append("</table>");

        buffer.append("</body></html>");

        return buffer.toString();
    }

    private void addSeperator(ExtendedHTMLDocument doc, StringBuffer buffer)
    {
        buffer.append("<tr><td colspan=\"2\">");
        buffer.append("<div style=\"font-size:0;\">").append(doc.createCompTag(new HorizontalLine())).append(
                "</div></td></tr>");
    }
}