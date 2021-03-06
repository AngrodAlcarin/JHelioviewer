package org.helioviewer.jhv.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.helioviewer.jhv.JHVGlobals;
import org.helioviewer.jhv.SplashScreen;
import org.helioviewer.jhv.gui.IconBank.JHVIcon;
import org.helioviewer.jhv.gui.actions.ExitProgramAction;
import org.helioviewer.jhv.gui.components.ControlPanelContainer;
import org.helioviewer.jhv.gui.components.ImageSelectorPanel;
import org.helioviewer.jhv.gui.components.MainContentPanel;
import org.helioviewer.jhv.gui.components.MainImagePanel;
import org.helioviewer.jhv.gui.components.MenuBar;
import org.helioviewer.jhv.gui.components.MoviePanel;
import org.helioviewer.jhv.gui.components.SideContentPane;
import org.helioviewer.jhv.gui.components.StatusPanel;
import org.helioviewer.jhv.gui.components.TopToolBar;
import org.helioviewer.jhv.gui.components.newComponents.FilterTabPanel;
import org.helioviewer.jhv.gui.components.newComponents.NewImageSelectorPanel;
import org.helioviewer.jhv.gui.components.statusplugins.CurrentTimeLabel;
import org.helioviewer.jhv.gui.components.statusplugins.FramerateStatusPanel;
import org.helioviewer.jhv.gui.components.statusplugins.JPIPStatusPanel;
import org.helioviewer.jhv.gui.components.statusplugins.PositionStatusPanel;
import org.helioviewer.jhv.gui.components.statusplugins.ZoomStatusPanel;
import org.helioviewer.jhv.gui.controller.GL3DCameraMouseController;
import org.helioviewer.jhv.internal_plugins.filter.SOHOLUTFilterPlugin.SOHOLUTPanel;
import org.helioviewer.jhv.internal_plugins.filter.channelMixer.ChannelMixerPanel;
import org.helioviewer.jhv.internal_plugins.filter.contrast.ContrastPanel;
import org.helioviewer.jhv.internal_plugins.filter.gammacorrection.GammaCorrectionPanel;
import org.helioviewer.jhv.internal_plugins.filter.opacity.OpacityPanel;
import org.helioviewer.jhv.internal_plugins.filter.sharpen.SharpenPanel;
import org.helioviewer.jhv.plugins.viewmodelplugin.filter.FilterTabPanelManager;
import org.helioviewer.jhv.viewmodel.view.opengl.GL3DComponentView;

/**
 * A class that sets up the graphical user interface.
 * 
 * @author caplins
 * @author Benjamin Wamsler
 * @author Alen Agheksanterian
 * @author Stephan Pagel
 * @author Markus Langenberg
 * @author Andre Dau
 * 
 */
public class ImageViewerGui {

	/** The sole instance of this class. */
	private static final ImageViewerGui SINGLETON = new ImageViewerGui();

	private static JFrame mainFrame;
	private JPanel contentPanel;
	private JSplitPane midSplitPane;
	private JScrollPane leftScrollPane;

	private MainContentPanel mainContentPanel;
	protected MainImagePanel mainImagePanel;

	private SideContentPane leftPane;
	private ImageSelectorPanel imageSelectorPanel;
	private NewImageSelectorPanel newImageSelectorPanel;
	private MoviePanel moviePanel;
	private ControlPanelContainer moviePanelContainer;
	private ControlPanelContainer filterPanelContainer;
	private JMenuBar menuBar;

	public static final int SIDE_PANEL_WIDTH = 320;

	/**
	 * The private constructor that creates and positions all the gui
	 * components.
	 */
	private ImageViewerGui() {
		mainFrame = createMainFrame();

		menuBar = new MenuBar();
		menuBar.setFocusable(false);

		mainFrame.setJMenuBar(menuBar);
		mainFrame.setFocusable(true);

		prepareGui();
	}

	public void prepareGui() {
			contentPanel = new JPanel(new BorderLayout());
			mainFrame.setContentPane(contentPanel);

			//midSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
			midSplitPane = new JSplitPane();
			midSplitPane.setOneTouchExpandable(false);

			contentPanel.add(midSplitPane, BorderLayout.CENTER);
			mainContentPanel = new MainContentPanel();
			mainContentPanel.setMainComponent(getMainImagePanel());

			// STATE - GET TOP TOOLBAR
			contentPanel.add(getTopToolBar(), BorderLayout.PAGE_START);

			// // FEATURES / EVENTS
			// solarEventCatalogsPanel = new SolarEventCatalogsPanel();
			// leftPane.add("Features/Events", solarEventCatalogsPanel, false);

			// STATE - GET LEFT PANE
			leftScrollPane = new JScrollPane(getLeftContentPane(),
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			leftScrollPane.setFocusable(false);
			leftScrollPane.getVerticalScrollBar().setUnitIncrement(10);
			midSplitPane.setLeftComponent(leftScrollPane);

			midSplitPane.setRightComponent(mainContentPanel);

			// ///////////////////////////////////////////////////////////////////////////////
			// STATUS PANEL
			// ///////////////////////////////////////////////////////////////////////////////

			ZoomStatusPanel zoomStatusPanel = new ZoomStatusPanel();
			FramerateStatusPanel framerateStatus = new FramerateStatusPanel();

			PositionStatusPanel positionStatusPanel = null;

			positionStatusPanel = new PositionStatusPanel(getMainImagePanel());

			JPIPStatusPanel jpipStatusPanel = new JPIPStatusPanel();

			StatusPanel statusPanel = new StatusPanel(5, 5);
			statusPanel.addLabel(new CurrentTimeLabel() , StatusPanel.Alignment.LEFT);
			statusPanel.addPlugin(zoomStatusPanel, StatusPanel.Alignment.LEFT);
			statusPanel.addPlugin(framerateStatus, StatusPanel.Alignment.LEFT);
			statusPanel.addPlugin(jpipStatusPanel, StatusPanel.Alignment.RIGHT);
			statusPanel.addPlugin(positionStatusPanel,
					StatusPanel.Alignment.RIGHT);
			
			contentPanel.add(statusPanel, BorderLayout.PAGE_END);
			
			contentPanel.validate();
            leftScrollPane.setMinimumSize(new Dimension(leftScrollPane.getWidth(),0));
	}

	/**
	 * Packs, positions and shows the GUI
	 * 
	 * @param _show
	 *            If GUI should be displayed.
	 */
	public void packAndShow(final boolean _show) {

		final SplashScreen splash = SplashScreen.getSingletonInstance();

		// load images which should be displayed first in a separated thread
		// that splash screen will be updated
		splash.setProgressText("Loading Images...");

		Thread thread = new Thread(new Runnable() {

			public void run() {

				// show GUI
				splash.setProgressText("Starting JHelioviewer...");
				splash.nextStep();
				mainFrame.pack();
				mainFrame.setLocationRelativeTo(null);
				mainFrame.setVisible(_show);
				splash.setProgressValue(100);

				// remove splash screen
				splash.dispose();

			}
		}, "LoadImagesOnStartUp");
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Initializes the main and overview view chain.
	 */
	public void createViewchains() {
		GuiState3DWCS.createViewChains();
		
		// prepare gui again
		mainImagePanel.setInputController(new GL3DCameraMouseController());

		mainFrame.validate();

		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				updateComponentPanels();
				
			}
		});
		
		packAndShow(true);
		mainFrame.validate();
		mainFrame.repaint();
	}

	/**
	 * Method that creates and initializes the main JFrame.
	 * 
	 * @return the created and initialized main frame.
	 */
	private JFrame createMainFrame() {
		JFrame frame = new JFrame("ESA JHelioviewer");

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent arg0) {
				ExitProgramAction exitAction = new ExitProgramAction();
				exitAction.actionPerformed(new ActionEvent(this, 0, ""));
			}
		});

		Dimension maxSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension minSize = new Dimension(800, 600);

		maxSize.width -= 200;
		// if the display is not very high, we want to take most of the height,
		// as the rest is not useful anyway
		if (maxSize.height < 1000) {
			maxSize.height -= 100;
		} else {
			maxSize.height -= 150;
		}

		minSize.width = Math.min(minSize.width, maxSize.width);
		minSize.height = Math.min(minSize.height, maxSize.height);
		//frame.setMaximumSize(maxSize);
		frame.setMinimumSize(minSize);
		frame.setPreferredSize(maxSize);
		frame.setFont(new Font("SansSerif", Font.BOLD, 12));
		frame.setIconImage(IconBank.getIcon(JHVIcon.HVLOGO_SMALL).getImage());
		return frame;
	}

	/**
	 * Returns instance of the main ComponentView.
	 * 
	 * @return instance of the main ComponentView.
	 */
	public GL3DComponentView getMainView() {
		return GuiState3DWCS.mainComponentView;
	}


	/**
	 * Returns the scrollpane containing the left content pane.
	 * 
	 * @return instance of the scrollpane containing the left content pane.
	 * */
	public SideContentPane getLeftContentPane() {
		// ////////////////////////////////////////////////////////////////////////////////
		// LEFT CONTROL PANEL
		// ////////////////////////////////////////////////////////////////////////////////

		if (this.leftPane != null) {
			return this.leftPane;
		} else {
			leftPane = new SideContentPane();

			// Movie control
			moviePanelContainer = new ControlPanelContainer();
			this.moviePanel = new MoviePanel();
			moviePanelContainer.setDefaultPanel(moviePanel);
			
			leftPane.add("Overview", GuiState3DWCS.overViewPanel, JHVGlobals.OLD_RENDER_MODE);			
			leftPane.add("Movie Controls", moviePanelContainer, true);

			// Layer control
			if (JHVGlobals.OLD_RENDER_MODE) imageSelectorPanel = new ImageSelectorPanel();
			else newImageSelectorPanel = new NewImageSelectorPanel();

			if (JHVGlobals.OLD_RENDER_MODE)leftPane.add("Layers", imageSelectorPanel, true);
			else leftPane.add("new Layers", newImageSelectorPanel, true);

			// Image adjustments and filters
			FilterTabPanelManager compactPanelManager = new FilterTabPanelManager();
			compactPanelManager.add(new OpacityPanel());
			//compactPanelManager.add(new QualitySpinner(null));
			compactPanelManager.add(new SOHOLUTPanel());
			compactPanelManager.add(new GammaCorrectionPanel());
			compactPanelManager.add(new ContrastPanel());
			compactPanelManager.add(new SharpenPanel());
			compactPanelManager.add(new ChannelMixerPanel());
			
			JPanel compactPanel = compactPanelManager.createCompactPanel();
			compactPanel.setEnabled(false);
            compactPanel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

			filterPanelContainer = new ControlPanelContainer();
			filterPanelContainer.setDefaultPanel(compactPanel);
			if (JHVGlobals.OLD_RENDER_MODE)leftPane.add("Adjustments", filterPanelContainer, false);
			else leftPane.add("NEWAdjustments", new FilterTabPanel(), true);

			return leftPane;
		}
	}

	/**
	 * Returns the instance of the ImageSelectorPanel.
	 * 
	 * @return instance of the image selector panel.
	 * */
	public ImageSelectorPanel getImageSelectorPanel() {
		return this.imageSelectorPanel;
	}

	public NewImageSelectorPanel getNewImageSelectorPanel(){
		return this.newImageSelectorPanel;
	}
	
	/**
	 * @return the movie panel container
	 */
	public ControlPanelContainer getMoviePanelContainer() {
		return this.moviePanelContainer;
	}

	/**
	 * @return the filter panel container
	 */
	public ControlPanelContainer getFilterPanelContainer() {
		return this.filterPanelContainer;
	}

	/**
	 * @return the menu bar of jhv
	 */
	public JMenuBar getMenuBar() {
		return menuBar;
	}

	public MainImagePanel getMainImagePanel() {
		// ////////////////////////////////////////////////////////////////////////////////
		// MAIN IMAGE PANEL
		// ////////////////////////////////////////////////////////////////////////////////
		if (mainImagePanel == null) {

			// set up main image panel
			mainImagePanel = new MainImagePanel();
			mainImagePanel.setAutoscrolls(true);
			mainImagePanel.setFocusable(false);

		}

		return mainImagePanel;
	}

	public TopToolBar getTopToolBar() {
		return GuiState3DWCS.topToolBar;
	}

	public void addTopToolBarPlugin(
			PropertyChangeListener propertyChangeListener, JToggleButton button) {
		GuiState3DWCS.topToolBar.addToolbarPlugin(button);

		GuiState3DWCS.topToolBar
				.addPropertyChangeListener(propertyChangeListener);

		GuiState3DWCS.topToolBar.validate();
		GuiState3DWCS.topToolBar.repaint();
	}

	private void updateComponentPanels() {

		if (getMainView() != null) {
			getMainImagePanel().setView(getMainView());
		}
		mainFrame.validate();
	}

	/**
	 * Returns the only instance of this class.
	 * 
	 * @return the only instance of this class.
	 * */
	public static ImageViewerGui getSingletonInstance() {
		return SINGLETON;
	}

	/**
	 * Returns the main frame.
	 * 
	 * @return the main frame.
	 * */
	public static JFrame getMainFrame() {
		return mainFrame;
	}

	/**
	 * Returns the scrollpane containing the left content pane.
	 * 
	 * @return instance of the scrollpane containing the left content pane.
	 * */
	public JScrollPane getLeftScrollPane() {
		return leftScrollPane;
	}

	/**
	 * Calls the update methods of sub components of the main frame so they can
	 * e.g. reload data.
	 */
	public void updateComponents() {
	}

	/**
	 * Returns the content panel of JHV
	 * 
	 * @return The content panel of JHV
	 */
	public JPanel getContentPane() {
		return contentPanel;
	}

	public final MainContentPanel getMainContentPanel() {
		return mainContentPanel;
	}
}
