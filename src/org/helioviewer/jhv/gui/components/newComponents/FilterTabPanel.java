package org.helioviewer.jhv.gui.components.newComponents;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.helioviewer.jhv.gui.GuiState3DWCS;
import org.helioviewer.jhv.gui.IconBank;
import org.helioviewer.jhv.gui.IconBank.JHVIcon;
import org.helioviewer.jhv.gui.components.WheelSupport;
import org.helioviewer.jhv.layers.Layer;
import org.helioviewer.jhv.layers.NewLayerListener;
import org.helioviewer.jhv.layers.filter.LUT;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FilterTabPanel extends JPanel implements NewLayerListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6032053412521027833L;
	private JSlider opacitySlider;
	private JSlider sharpenSlider;
	private JSlider gammaSlider;
	private JSlider contrastSlider;
	private JComboBox<String> comboBoxColorTable;
	private JCheckBox chckbxRed;
	private JCheckBox chckbxGreen;
	private JCheckBox chckbxBlue;
	private JToggleButton btnInverseColorTable;
	private JLabel lblOpacity, lblSharpen, lblGamma, lblContrast;
	private Layer activeLayer;
	
	private static final double GAMMA_FACTOR = 0.01 * Math.log(10);
	
    private static final Icon ICON_INVERT = IconBank.getIcon(JHVIcon.INVERT);

	public FilterTabPanel() {
		initGui();
		GuiState3DWCS.layers.addNewLayerListener(this);
	}
	
	private void initGui(){
		FormLayout formLayout = new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("right:default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("right:default"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,});
		formLayout.setColumnGroups(new int[][]{new int[]{8, 4, 6}});
		setLayout(formLayout);
		
		JLabel lblOpacityTitle = new JLabel("Opacity:");
		add(lblOpacityTitle, "2, 2");
		
		opacitySlider = new JSlider();
		opacitySlider.setMinorTickSpacing(20);
		opacitySlider.setPaintTicks(true);
		add(opacitySlider, "4,2,5,1");
		WheelSupport.installMouseWheelSupport(opacitySlider);

		lblOpacity = new JLabel("%");
		add(lblOpacity, "10, 2");

		
		opacitySlider.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				lblOpacity.setText(opacitySlider.getValue() + "%");
				if (activeLayer != null && activeLayer.opacity != opacitySlider.getValue() / 100.0){
					activeLayer.opacity = opacitySlider.getValue() / 100.0;
					repaintComponent();
				}
			}
		});
		
		JLabel lblSharpenTitle = new JLabel("Sharpen");
		add(lblSharpenTitle, "2, 4");

		sharpenSlider = new JSlider();
		sharpenSlider.setMinorTickSpacing(20);
		sharpenSlider.setPaintTicks(true);
		add(sharpenSlider, "4, 4, 5, 1");
		WheelSupport.installMouseWheelSupport(sharpenSlider);
		lblSharpen = new JLabel("%");
		add(lblSharpen, "10, 4");
		sharpenSlider.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				lblSharpen.setText(sharpenSlider.getValue() + "%");
				if (activeLayer != null && activeLayer.sharpen != sharpenSlider.getValue()/100.0){
					activeLayer.sharpen = sharpenSlider.getValue()/100.0;
					repaintComponent();
				}
			}
		});
		
		JLabel lblGammaTitle = new JLabel("Gamma");
		add(lblGammaTitle, "2, 6");
		
		gammaSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
		gammaSlider.setMinorTickSpacing(20);
		gammaSlider.setPaintTicks(true);
		gammaSlider.setValue(10);
		add(gammaSlider, "4, 6, 5, 1");
		
		lblGamma = new JLabel("1.0");
		add(lblGamma, "10, 6");
		WheelSupport.installMouseWheelSupport(gammaSlider);
		gammaSlider.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				double gammaValue = Math.exp(gammaSlider.getValue() * GAMMA_FACTOR);
		        String label = Double.toString(Math.round(gammaValue * 10.0) * 0.1);
		        if (gammaSlider.getValue() == 100) {
		            label = label.substring(0, 4);
		        } else {
		            label = label.substring(0, 3);
		        }
				lblGamma.setText(label);
				if (activeLayer != null && activeLayer.gamma != gammaValue){
					activeLayer.gamma = gammaValue;
					repaintComponent();
				}
			}
		});
		
		JLabel lblContrastTitle = new JLabel("Contrast");
		add(lblContrastTitle, "2, 8");
		
		contrastSlider = new JSlider();
		contrastSlider.setMinorTickSpacing(20);
		contrastSlider.setPaintTicks(true);
		contrastSlider.setMaximum(100);
		contrastSlider.setMinimum(-100);
		contrastSlider.setValue(0);
		add(contrastSlider, "4, 8, 5, 1");
		WheelSupport.installMouseWheelSupport(contrastSlider);
		
		lblContrast = new JLabel("0");
		add(lblContrast, "10,8");

		contrastSlider.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				lblContrast.setText(contrastSlider.getValue() + "");
				if (activeLayer != null &&  activeLayer.contrast != contrastSlider.getValue()/10.0){
					activeLayer.contrast = contrastSlider.getValue() / 10.0;
					repaintComponent();
				}
			}
		});
		
		JLabel lblColorTitle = new JLabel("Color:");
		add(lblColorTitle, "2, 10, right, default");
		
        /*Map<String, LUT> lutMap = LUT.getStandardList();
        lutMap.put("<Load new GIMP gradient file>", null);*/
        comboBoxColorTable = new JComboBox<String>();
        comboBoxColorTable = new JComboBox<String>(LUT.getLut().getNames());
        comboBoxColorTable.setSelectedItem("gray");
		add(comboBoxColorTable, "4, 10, 5, 1, fill, default");
		comboBoxColorTable.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (activeLayer != null && activeLayer.lut.idx != comboBoxColorTable.getSelectedIndex()){
					activeLayer.lut.name = (String)comboBoxColorTable.getSelectedItem();
					activeLayer.lut.idx = LUT.getLutPosition(activeLayer.lut.name);
					repaintComponent();
				}
			}
		});
		
		btnInverseColorTable = new JToggleButton(ICON_INVERT);
		add(btnInverseColorTable, "10, 10");
		btnInverseColorTable.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(activeLayer != null && activeLayer.lut.isInverted() != btnInverseColorTable.isSelected()){
					activeLayer.lut.setInverted(btnInverseColorTable.isSelected());
					repaintComponent();
				}
			}
		});
		
		JLabel lblChannelsTitle = new JLabel("Channels");
		add(lblChannelsTitle, "2, 12");
		
		chckbxRed = new JCheckBox("Red");
		add(chckbxRed, "4, 12");
		chckbxRed.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (activeLayer != null && activeLayer.redChannel.isActivated() != chckbxRed.isSelected()){
					activeLayer.redChannel.setActive(chckbxRed.isSelected());
					repaintComponent();
				}
			}
		});
		
		chckbxGreen = new JCheckBox("Green");
		add(chckbxGreen, "6, 12");
		chckbxGreen.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (activeLayer != null && activeLayer.greenChannel.isActivated() != chckbxGreen.isSelected()){
					activeLayer.greenChannel.setActive(chckbxGreen.isSelected());
					repaintComponent();

				}
			}
		});
		
		chckbxBlue = new JCheckBox("Blue");
		add(chckbxBlue, "8, 12");
		chckbxBlue.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (activeLayer != null && activeLayer.blueChannel.isActivated() != chckbxBlue.isSelected()){
					activeLayer.blueChannel.setActive(chckbxBlue.isSelected());
					repaintComponent();
				}
			}
		});

	}

	private void updateLayer(Layer layer){
		this.activeLayer = layer;
		this.contrastSlider.setValue((int)layer.contrast * 10);
		this.gammaSlider.setValue((int) (Math.log(layer.gamma) / GAMMA_FACTOR));
		this.opacitySlider.setValue((int) layer.opacity * 100);
		this.sharpenSlider.setValue((int) layer.sharpen * 100);
		this.comboBoxColorTable.setSelectedItem(layer.lut.name);
		this.btnInverseColorTable.setSelected(layer.lut.isInverted());
		this.chckbxRed.setSelected(layer.redChannel.isActivated());
		this.chckbxGreen.setSelected(layer.greenChannel.isActivated());
		this.chckbxBlue.setSelected(layer.blueChannel.isActivated());
	}
	
	@Override
	public void newlayerAdded() {
		this.updateLayer(GuiState3DWCS.layers.getActiveLayer());
	}

	@Override
	public void newlayerRemoved(int idx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newtimestampChanged() {
	}

	@Override
	public void activeLayerChanged(Layer layer) {
		this.updateLayer(layer);
	}
	
	private void repaintComponent(){
		GuiState3DWCS.mainComponentView.getComponent().repaint();
	}
	
}
