/*
 * Created on 08.04.2004
 */
package tvbrowser.core.filters.filtercomponents;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tvbrowser.core.filters.FilterComponent;
import util.ui.ImageUtilities;
import devplugin.Program;
import devplugin.ProgramInfoHelper;

/**
 * Filtert nach bestimmten Programm-Informationen (zum Beispiel Untertitel)
 * 
 * @author bodo
 */
public class ProgramInfoFilterComponent implements FilterComponent {

	/**
	 * Erzeugt einen leeren Filter
     */
    public ProgramInfoFilterComponent() {
        this("", "");
    }

    /**
     * Erzeugt einen Filter
     * @param name Name 
     * @param description Beschreibung
     */
    public ProgramInfoFilterComponent(String name, String description) {
        _name = name;
        _desc = description;
    }

    /** 
     * Gibt die Version zur�ck
     * @see tvbrowser.core.filters.FilterComponent#getVersion()
     */
    public int getVersion() {
        return 1;
    }

    /**
     * Wird dieses Programm akzeptiert von diesem Filter ?
     * @see tvbrowser.core.filters.FilterComponent#accept(devplugin.Program)
     */
    public boolean accept(Program program) {
        int info = program.getInfo();
        if (info < 1) { return false; }

        return bitSet(info, selectedBits);
    }

    /**
     * Liest die Einstellungen f�r dieses Plugin aus dem Stream
     * @see tvbrowser.core.filters.FilterComponent#read(java.io.ObjectInputStream,
     *      int)
     */
    public void read(ObjectInputStream in, int version) throws IOException,
            ClassNotFoundException {
		selectedBits = in.readInt();
    }

    /**
     * Schreibt die Einstellungen dieses Plugins in den Stream
     * @see tvbrowser.core.filters.FilterComponent#write(java.io.ObjectOutputStream)
     */
    public void write(ObjectOutputStream out) throws IOException {
		out.writeInt(selectedBits);
    }

    /**
     * Gibt einen Panel zur�ck, der es erm�glicht, 
     * den Filter einzustellen
     * @see tvbrowser.core.filters.FilterComponent#getPanel()
     */
    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;

        GridBagConstraints b = new GridBagConstraints();
        b.fill = GridBagConstraints.NONE;
        
        _checkBox = new JCheckBox[ProgramInfoHelper.mInfoMsgArr.length];
        
        for (int i = 0; i < ProgramInfoHelper.mInfoMsgArr.length; i++) {
        
        	final JCheckBox box = new JCheckBox();
        	_checkBox[i] = box;
        	JLabel label = new JLabel(ProgramInfoHelper.mInfoMsgArr[i], ProgramInfoHelper.mInfoIconArr[i], JLabel.LEFT);
        	
        	if (bitSet(selectedBits, ProgramInfoHelper.mInfoBitArr[i])) {
        		box.setSelected(true);
        	}
        	
        	label.addMouseListener(new MouseAdapter() {
        		 public void mouseClicked(MouseEvent e) {
        		 	box.setSelected(!box.isSelected());
        		 }
        	});
        	 
            panel.add(box,b);
            panel.add(label,c);
            System.out.println(ProgramInfoHelper.mInfoMsgArr[i]);
        }
        
        return panel;
    }

    /**
     * Im Dialog wurde OK gedr�ckt, alle Einstellungen k�nnen
     * nun �bernommen werden
     * @see tvbrowser.core.filters.FilterComponent#ok()
     */
    public void ok() {
    	int selectedBits = 0;
    
    	for (int i = 0; i < _checkBox.length; i++) {
    		if (_checkBox[i].isSelected()) {
    			System.out.println(ProgramInfoHelper.mInfoMsgArr[i]);
    			selectedBits = selectedBits | ProgramInfoHelper.mInfoBitArr[i];
    		}
        }
        this.selectedBits = selectedBits;
    }

    /**
     * Gibt den momentanen Namen des Filters zur�ck
     * @see tvbrowser.core.filters.FilterComponent#getName()
     */
    public String getName() {
        return _name;
    }

    /**
     * Gibt die momentane Beschreibung des Filters zur�ck
     * @see tvbrowser.core.filters.FilterComponent#getDescription()
     */
    public String getDescription() {
        return _desc;
    }

    /**
     * Setzt den Namen des Filters
     * @see tvbrowser.core.filters.FilterComponent#setName(java.lang.String)
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Setzt die Beschreibung des Filters
     * @see tvbrowser.core.filters.FilterComponent#setDescription(java.lang.String)
     */
    public void setDescription(String desc) {
        _desc = desc;
    }

    /**
     * Gibt den Namen des Filters zur�ck 
     */
    public String toString() {
        return mLocalizer.msg("ProgrammInfo", "Program-Info");
    }

    /**
     * �berpr�ft, ob Bits gesetzt sind
     * @param num hier pr�fen
     * @param pattern diese pattern pr�fen
     * @return Pattern gesetzt?
     */
    private boolean bitSet(int num, int pattern) {
        return (num & pattern) == pattern;
    }

    /**
     * Erzeugt ein Icon
     * @param fileName Name der Datei
     * @return Icon
     */
    private Icon createIcon(String fileName) {
        return ImageUtilities.createImageIconFromJar("imgs/" + fileName, getClass());
    }

    /**
     * Die gesetzten Bits
     */
    private int selectedBits = 0;
    
    /**
     * Die CheckBoxen f�r den Panel
     */
    private JCheckBox[] _checkBox;

    /**
     * Der Lokalizer
     */
    private static final util.ui.Localizer mLocalizer = util.ui.Localizer.getLocalizerFor(ProgramInfoFilterComponent.class);    
    
    /**
     * Name des Filters
     */
    private String _name;

    /**
     * Beschreibung des Filters
     */
    private String _desc;
}