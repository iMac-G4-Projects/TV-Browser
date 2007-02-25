package util.ui.textcomponentpopup;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import util.ui.Localizer;

/**
 * This TextComponentPopupEventQueue is based on the Implementation of Santhosh
 * 
 * For Details look here:
 * http://jroller.com/page/santhosh?entry=context_menu_for_textcomponents
 * 
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 */
public class TextComponentPopupEventQueue extends EventQueue {

  private static final util.ui.Localizer mLocalizer = util.ui.Localizer
      .getLocalizerFor(TextComponentPopupEventQueue.class);

  protected void dispatchEvent(AWTEvent event) {
    try {
      super.dispatchEvent(event);
    }catch(Throwable e) {return;}

    // interested only in mouseevents
    if (!(event instanceof MouseEvent))
      return;

    MouseEvent me = (MouseEvent) event;

    // interested only in popuptriggers
    if (!me.isPopupTrigger() || me.getComponent() == null)
      return;

    // me.getComponent(...) retunrs the heavy weight component on which event
    // occured
    Component comp = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY());

    // interested only in textcomponents
    if (!(comp instanceof JTextComponent))
      return;

    // no popup shown by user code
    if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0)
      return;

    // create popup menu and show
    JTextComponent tc = (JTextComponent) comp;
    JPopupMenu menu = new JPopupMenu();
    menu.add(new CutAction(tc));
    menu.add(new CopyAction(tc));
    menu.add(new PasteAction(tc));
    menu.add(new DeleteAction(tc));
    menu.addSeparator();
    menu.add(new SelectAllAction(tc));

    Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), tc);
    menu.show(tc, pt.x, pt.y);
  }

  class CutAction extends AbstractAction {
    JTextComponent comp;

    public CutAction(JTextComponent comp) {
      super(mLocalizer.msg("cut", "Cut"));
      this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {
      comp.cut();
    }

    public boolean isEnabled() {
      return comp.isEditable() && comp.isEnabled() && comp.getSelectedText() != null;
    }
  }

  class PasteAction extends AbstractAction {
    JTextComponent comp;

    public PasteAction(JTextComponent comp) {
      super(mLocalizer.msg("paste", "Paste"));
      this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {
      comp.paste();
    }

    public boolean isEnabled() {
      if (comp.isEditable() && comp.isEnabled()) {
        Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
        return contents.isDataFlavorSupported(DataFlavor.stringFlavor);
      } else
        return false;
    }
  }

  class DeleteAction extends AbstractAction {
    JTextComponent comp;

    public DeleteAction(JTextComponent comp) {
      super(Localizer.getLocalization(Localizer.I18N_DELETE));
      this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {
      comp.replaceSelection(null);
    }

    public boolean isEnabled() {
      return comp.isEditable() && comp.isEnabled() && comp.getSelectedText() != null;
    }
  }

  class CopyAction extends AbstractAction {
    JTextComponent comp;

    public CopyAction(JTextComponent comp) {
      super(mLocalizer.msg("copy", "Copy"));
      this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {
      comp.copy();
    }

    public boolean isEnabled() {
      return comp.isEnabled() && comp.getSelectedText() != null && !(comp instanceof JPasswordField);
    }
  }

  class SelectAllAction extends AbstractAction {
    JTextComponent comp;

    public SelectAllAction(JTextComponent comp) {
      super(mLocalizer.msg("selectAll", "Select All"));
      this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {
      comp.selectAll();
    }

    public boolean isEnabled() {
      return comp.isEnabled() && comp.getText().length() > 0;
    }
  }
}