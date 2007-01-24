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
 *  $RCSfile$
 *   $Source$
 *     $Date$
 *   $Author$
 * $Revision$
 */
package devplugin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;

import tvbrowser.core.TvDataUpdateListener;
import tvbrowser.core.TvDataUpdater;
import tvbrowser.ui.pluginview.Node;
import tvbrowser.ui.pluginview.PluginTreeModel;


/**
 * The PluginTreeNode class represents a single node of the plugin tree view.
 *
 */
public class PluginTreeNode {

  private static final util.ui.Localizer mLocalizer =
      util.ui.Localizer.getLocalizerFor(PluginTreeNode.class);

  private int mNodeType;
  private ArrayList<PluginTreeNode> mChildNodes;
  private Object mObject;
  private ArrayList<PluginTreeListener> mNodeListeners;
  private Marker mMarker;
  private Node mDefaultNode;
  private boolean mGroupingByDate;

  private PluginTreeNode(int type, Object o) {
    mChildNodes = new ArrayList<PluginTreeNode>();
    mNodeType = type;
    mObject = o;
    mDefaultNode = new Node(type, mObject);
    mNodeListeners = new ArrayList<PluginTreeListener>();
    mGroupingByDate = true;
  }

  /**
   * Creates a new PluginTreeNode object with a specified title
   * @param title
   */
  public PluginTreeNode(String title) {
    this(Node.CUSTOM_NODE, title);
  }

  /**
   * Creates a new root PluginTreeNode
   * On tv listings updates, the {@link PluginTreeListener} gets informed.
   * @param marker
   */
  public PluginTreeNode(Marker marker) {
    this(marker, true);
  }

  /**
   * Creates a new root PluginTreeNode
   * @param marker
   * @param handleTvDataUpdate specifies, if the {@link PluginTreeListener}
   * should get called on tv listings updates
   */
  public PluginTreeNode(Marker marker, boolean handleTvDataUpdate) {
    this(Node.PLUGIN_ROOT, marker);
    mMarker = marker;

    if (handleTvDataUpdate) {
      final RemovedProgramsHandler removedProgramsHandler = new RemovedProgramsHandler();

      TvDataUpdater.getInstance().addTvDataUpdateListener(new TvDataUpdateListener() {
        public void tvDataUpdateStarted() {
          removedProgramsHandler.clear();
        }

        public void tvDataUpdateFinished() {
          refreshAllPrograms(removedProgramsHandler);
          update();
          Program[] removedPrograms = removedProgramsHandler.getRemovedPrograms();
          fireProgramsRemoved(removedPrograms);
        }
      });
    }

  }

  /**
   * Creates a new Node containing a program item.
   * @param item
   */
  public PluginTreeNode(ProgramItem item) {
    this(Node.PROGRAM, item);
    mDefaultNode.setAllowsChildren(false);
  }


  public void addNodeListener(PluginTreeListener listener) {
    mNodeListeners.add(listener);
  }

  public boolean removeNodeListener(PluginTreeListener listener) {
    return mNodeListeners.remove(listener);
  }

  public void removeAllNodeListeners() {
    mNodeListeners.clear();
  }

  /**
   * Remove all programs from this node which are not available any more
   */
  private void refreshAllPrograms(RemovedProgramsHandler handler) {

    for (int i=mChildNodes.size()-1; i>=0; i--) {
      PluginTreeNode node = mChildNodes.get(i);
      node.mMarker = mMarker;
      
      if (node.isLeaf()) {
        ProgramItem progItemInTree = (ProgramItem)node.getUserObject();
        Program progInTree = progItemInTree.getProgram();
        
        if(progInTree.getProgramState() == Program.WAS_DELETED_STATE) {
          removeProgram(progInTree);
          handler.addRemovedProgram(progInTree);
        }
        else if(progInTree.getProgramState() == Program.WAS_UPDATED_STATE) {
          Program updatedProg = Plugin.getPluginManager().getProgram(progInTree.getDate(), progInTree.getID());
          progItemInTree.setProgram(updatedProg);
        }
      }
      else {
        node.refreshAllPrograms(handler);
      }
    }
  }

  private void fireProgramsRemoved(Program[] progArr) {
    for (int i=0; i<mNodeListeners.size(); i++) {
      PluginTreeListener listener = mNodeListeners.get(i);
      listener.programsRemoved(progArr);
    }
  }

  public Node getMutableTreeNode() {
    return mDefaultNode;
  }

  /**
   * Adds a an action menu to this node
   * @param menu
   */
  public void addActionMenu(ActionMenu menu) {
    mDefaultNode.addActionMenu(menu);
  }


  public void removeAllActions() {
    mDefaultNode.removeAllActionMenus();
  }

  public void addAction(Action action) {
    addActionMenu(new ActionMenu(action));
  }

  public ActionMenu[] getActionMenus() {
    return mDefaultNode.getActionMenus();
  }

  /**
   * Sets the formatter for this node and all of the child nodes.
   * @param formatter the formatter
   */
  public void setNodeFormatter(NodeFormatter formatter) {
    mDefaultNode.setNodeFormatter(formatter);
  }

  /**
   * Enables/Disabled the 'grouping by date'-feature.
   * Default is 'enabled'
   *
   * @param enable
   */
  public void setGroupingByDateEnabled(boolean enable) {
    mGroupingByDate = enable;
  }

  private NodeFormatter getNodeFormatter() {
    return mDefaultNode.getNodeFormatter();
  }

  private void createDefaultNodes() {
    mDefaultNode.removeAllChildren();

    PluginTreeNode[] items = mChildNodes.toArray(new PluginTreeNode[mChildNodes.size()]);
    Arrays.sort(items, sPluginTreeNodeComparator);
    for (int i=0; i<items.length; i++) {
      PluginTreeNode n = items[i];
      if (!n.isLeaf()) {
        if (n.mGroupingByDate) {
          n.createDateNodes();
        }
        else {
          n.createDefaultNodes();
        }
        mDefaultNode.add(n.getMutableTreeNode());
      }
      else {
      	ProgramItem progItem = (ProgramItem)n.getUserObject();
        Node node = new Node(progItem);
        node.setNodeFormatter(n.getNodeFormatter());
        mDefaultNode.add(node);
      }
    }
  }

  private void createDateNodes() {
    /* We create new folders for each day and assign the program items
       to the appropriate folder */

    Map<Date, ArrayList<PluginTreeNode>> dateMap = new HashMap<Date, ArrayList<PluginTreeNode>>();  // key: date; value: ArrayList of ProgramItem objects
    mDefaultNode.removeAllChildren();

    Iterator<PluginTreeNode> it = mChildNodes.iterator();
    while (it.hasNext()) {
      PluginTreeNode n = it.next();
      if (!n.isLeaf()) {
        if (n.mGroupingByDate) {
          n.createDateNodes();
        }
        else {
          n.createDefaultNodes();
        }
        mDefaultNode.add(n.getMutableTreeNode());
      }
      else {
      	Date date = ((ProgramItem)n.getUserObject()).getProgram().getDate();
        ArrayList<PluginTreeNode> list = dateMap.get(date);
        if (list == null) {
          list = new ArrayList<PluginTreeNode>();
          dateMap.put(date, list);
        }
        list.add(n);
      }
    }

    // Create the new nodes
    Set<Date> keySet = dateMap.keySet();
    Date[] dates = new Date[keySet.size()];
    keySet.toArray(dates);
    Arrays.sort(dates);
    Date today = Date.getCurrentDate();
    Date nextDay = today.addDays(1);
    for (int i=0; i<dates.length; i++) {
      String dateStr;
      if (today.equals(dates[i])) {
        dateStr = mLocalizer.msg("today","today");
      }
      else if (nextDay.equals(dates[i])) {
        dateStr = mLocalizer.msg("tomorrow","tomorrow");
      }
      else {
        dateStr = dates[i].toString();
      }

      Node node = new Node(Node.STRUCTURE_NODE, dateStr);
      mDefaultNode.add(node);
      List<PluginTreeNode> list = dateMap.get(dates[i]);
      PluginTreeNode[] nodeArr = new PluginTreeNode[list.size()];
      list.toArray(nodeArr);
      Arrays.sort(nodeArr, sPluginTreeNodeComparator);
      for (int k=0; k<nodeArr.length; k++) {
      	Node newNode = new Node((ProgramItem)nodeArr[k].getUserObject());
        newNode.setNodeFormatter(nodeArr[k].getNodeFormatter());
        node.add(newNode);
      }
    }
  }

  private static Comparator<PluginTreeNode> sPluginTreeNodeComparator = new Comparator<PluginTreeNode>() {
    public int compare(PluginTreeNode n1, PluginTreeNode n2) {
        Object u1 = n1.getUserObject();
        Object u2 = n2.getUserObject();
        if (u1 instanceof ProgramItem && u2 instanceof ProgramItem) {
          return sProgramItemComparator.compare((ProgramItem)u1, (ProgramItem)u2);
        }
        if (u1 instanceof String && u2 instanceof String) {
          return ((String)u1).compareTo((String)u2);
        }
        if (u1 instanceof String) {
          return 1;
        }
        return -1;
    }
  };

  private static Comparator<ProgramItem> sProgramItemComparator = new Comparator<ProgramItem>(){
    public int compare(ProgramItem pi1, ProgramItem pi2) {
        Program p1 = pi1.getProgram();
        Program p2 = pi2.getProgram();
        int result = p1.getDate().compareTo(p2.getDate());
        if (result != 0) {
          return result;
        }
        int t1 = p1.getStartTime();
        int t2 = p2.getStartTime();
        if (t1 < t2) {
          return -1;
        }
        else if (t1 > t2) {
          return 1;
        }
		return 0;
    }
  };


  public Object getUserObject() {
    return mObject;
  }

  public void removeAllChildren() {
    if (mMarker != null) {
      Program[] programs = getPrograms();
      for (int i=0; i<programs.length; i++) {
        programs[i].unmark(mMarker);
      }
    }
    mChildNodes.clear();
    mDefaultNode.removeAllChildren();
  }


  public void add(PluginTreeNode node) {
    mChildNodes.add(node);    
    node.mMarker = mMarker;
  }

  public boolean contains(Program prog, boolean recursive) {
    PluginTreeNode node = findProgramTreeNode(prog, recursive);
    return node != null;
  }

  public boolean contains(Program prog) {
    return contains(prog, false);
  }

  /**
   * Refreshs the tree. Call this method after you have added/removed/changed nodes
   * of the (sub-)tree
   */
  public void update() {

    if (mGroupingByDate) {
      createDateNodes();
    }
    else {
      createDefaultNodes();
    }

    PluginTreeModel.getInstance().reload(mDefaultNode);

  }


  public PluginTreeNode addProgram(Program program) {
    return addProgram(new ProgramItem(program));
  }

  public PluginTreeNode addProgram(ProgramItem item) {

    if (contains(item.getProgram(), false)) {
      return findProgramTreeNode(item.getProgram(), false);
    }

    if (mMarker != null) {
      item.getProgram().mark(mMarker);
    }
    PluginTreeNode node = new PluginTreeNode(item);
    add(node);
    return node;
  }


  private PluginTreeNode findProgramTreeNode(PluginTreeNode root, Program prog, boolean recursive) {
    Iterator it = root.mChildNodes.iterator();
    while (it.hasNext()) {
      PluginTreeNode node = (PluginTreeNode)it.next();
      if (!node.isLeaf()) {
        if (recursive) {
          PluginTreeNode n = findProgramTreeNode(node, prog, recursive);
          if (n!=null) {
            return n;
          }
        }
      }
      else {
        ProgramItem item = (ProgramItem)node.getUserObject();
        if (item.getProgram().equals(prog)) {
          return node;
        }
      }
    }
    return null;
  }

  private PluginTreeNode findProgramTreeNode(Program prog, boolean recursive) {
    return findProgramTreeNode(this, prog, recursive);
  }


  public void removeProgram(ProgramItem item) {
    removeProgram(item.getProgram());
  }

  public void removeProgram(Program program) {
    PluginTreeNode node = findProgramTreeNode(program, false);
    if (node != null) {
      mChildNodes.remove(node);
      if (mMarker != null) {
        program.unmark(mMarker);
    }
    }
  }

  public PluginTreeNode addNode(String title) {
    PluginTreeNode node = new PluginTreeNode(title);
    add(node);
    return node;
  }

  public ProgramItem[] getProgramItems() {
    ArrayList<Object> list = new ArrayList<Object>();
    Iterator<PluginTreeNode> it = mChildNodes.iterator();
    while (it.hasNext()) {
      PluginTreeNode n = it.next();
      if (n.isLeaf()) {
        list.add(n.getUserObject());
      }
    }

    ProgramItem[] result = new ProgramItem[list.size()];
    list.toArray(result);
    return result;
  }

  public Program[] getPrograms() {
    ArrayList<Program> list = new ArrayList<Program>();
    Iterator<PluginTreeNode> it = mChildNodes.iterator();
    while (it.hasNext()) {
      PluginTreeNode n = it.next();
      if (n.isLeaf()) {
        ProgramItem item = (ProgramItem)n.getUserObject();
        list.add(item.getProgram());
      }
    }

    Program[] result = new Program[list.size()];
    list.toArray(result);
    return result;
  }


  public void store(ObjectOutputStream out) throws IOException {
    int childrenCnt = mChildNodes.size();
    out.writeInt(childrenCnt);

    for (int i=0; i<childrenCnt; i++) {
      PluginTreeNode n = mChildNodes.get(i);
      out.writeInt(n.mNodeType);
      if (!n.isLeaf()) {
        String title = (String)n.getUserObject();
        out.writeObject(title);
      }
      else {
        ProgramItem item = (ProgramItem)n.getUserObject();
        item.write(out);
      }
      n.store(out);
    }
  }

  public void load(ObjectInputStream in) throws IOException, ClassNotFoundException {
    int cnt = in.readInt();
    for (int i=0; i<cnt; i++) {
      int type = in.readInt();
      PluginTreeNode n;
      if (type == Node.PROGRAM) {
        ProgramItem item = new ProgramItem();
        item.read(in);

        if (!contains(item.getProgram(), true)) {
          n = new PluginTreeNode(item);
          if (item.getProgram() != null) {
            add(n);
            if (mMarker != null) {
              item.getProgram().mark(mMarker);
            }
          }
        } else {
          n = findProgramTreeNode(item.getProgram(), false);
        }

      }
      else {
        String title = (String)in.readObject();
        n = new PluginTreeNode(title);
        add(n);
      }
      n.load(in);

    }
  }


  public int size() {
    return mChildNodes.size();
  }

  public void clear() {
    mChildNodes.clear();
  }

  public boolean isEmpty() {
    return mChildNodes.isEmpty();
  }

  public boolean isLeaf() {
    return (mDefaultNode.getType() == Node.PROGRAM);
  }


  class RemovedProgramsHandler {
    private ArrayList<Program> mProgArr;
    public RemovedProgramsHandler() {
      mProgArr = new ArrayList<Program>();
    }
    public void clear() {
      mProgArr.clear();
    }

    public void addRemovedProgram(Program prog) {
      mProgArr.add(prog);
    }

    public Program[] getRemovedPrograms() {
      Program[] progArr = new Program[mProgArr.size()];
      mProgArr.toArray(progArr);
      return progArr;
    }
  }

}