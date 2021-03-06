package org.helioviewer.jhv.gui.components.statusplugins;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.helioviewer.jhv.base.math.Vector3d;
import org.helioviewer.jhv.base.physics.Constants;
import org.helioviewer.jhv.gui.GL3DCameraSelectorModel;
import org.helioviewer.jhv.gui.components.BasicImagePanel;
import org.helioviewer.jhv.gui.interfaces.ImagePanelPlugin;
import org.helioviewer.jhv.layers.LayersModel;
import org.helioviewer.jhv.opengl.camera.GL3DCamera;
import org.helioviewer.jhv.opengl.scenegraph.rt.GL3DRay;
import org.helioviewer.jhv.viewmodel.view.View;

import ch.fhnw.i4ds.helio.coordinate.converter.Hcc2HgConverter;
import ch.fhnw.i4ds.helio.coordinate.coord.HeliocentricCartesianCoordinate;
import ch.fhnw.i4ds.helio.coordinate.coord.HeliographicCoordinate;

/**
 * Status panel for displaying the current mouse position.
 * 
 * <p>
 * If the the physical dimension of the image are known, the physical position
 * will be shown, otherwise, shows the screen position.
 * 
 * <p>
 * Basically, the information of this panel is independent from the active
 * layer.
 * 
 * <p>
 * If there is no layer present, this panel will be invisible.
 */
public class PositionStatusPanel extends ViewStatusPanelPlugin implements
		MouseMotionListener, ImagePanelPlugin, MouseListener {

	private static final long serialVersionUID = 1L;

	private View view;
	private BasicImagePanel imagePanel;
	private Point lastPosition;

	private static final char DEGREE = '\u00B0';
	private String title = " (X, Y) : ";
	private PopupState popupState;

	/**
	 * Default constructor.
	 * 
	 * @param imagePanel
	 *            ImagePanel to show mouse position for
	 */
	public PositionStatusPanel(BasicImagePanel imagePanel) {
		setBorder(BorderFactory.createEtchedBorder());

		// setPreferredSize(new Dimension(170, 20));
		LayersModel.getSingletonInstance().addLayersListener(this);

		imagePanel.addPlugin(this);

		popupState = new PopupState();
		this.addMouseListener(this);
		this.setComponentPopupMenu(popupState);
	}

	/**
	 * Updates the displayed position.
	 * 
	 * If the physical dimensions are available, translates the screen
	 * coordinates to physical coordinates.
	 * 
	 * @param position
	 *            Position on the screen.
	 */
	private void updatePosition(Point position) {
		GL3DCamera camera = GL3DCameraSelectorModel.getInstance()
				.getCurrentCamera();

		if (camera != null && camera.getLastMouseRay() != null
				&& camera.getLastMouseRay().getHitPoint() != null) {
			GL3DRay ray = camera.getLastMouseRay();
			Vector3d hitPoint = ray.getHitPoint();
			if (LayersModel.getSingletonInstance().getActiveView() != null
					&& hitPoint != null) {
				HeliocentricCartesianCoordinate cart = new HeliocentricCartesianCoordinate(
						hitPoint.x, hitPoint.y, hitPoint.z);

				DecimalFormat df;
				String point = null;
				switch (this.popupState.getSelectedState()) {
				case ARCSECS:
					Vector3d earthToSun = new Vector3d(0, 0,
							Constants.SUN_MEAN_DISTANCE_TO_EARTH);
					earthToSun = earthToSun.subtract(hitPoint);
					double theta = -Math.atan(earthToSun.x
							/ Math.sqrt(earthToSun.y * earthToSun.y
									+ earthToSun.z * earthToSun.z));
					double phi = -Math.atan2(earthToSun.y, earthToSun.z);
					df = new DecimalFormat("#");
					point = "(" + df.format(Math.toDegrees(theta) * 3600)
							+ "\" ," + df.format(Math.toDegrees(phi) * 3600)
							+ "\") ";
					break;
				case DEGREE:
					Hcc2HgConverter converter = new Hcc2HgConverter();
					HeliographicCoordinate newCoord = converter.convert(cart);
					df = new DecimalFormat("#.##");
					if (ray.isOnSun)
						point = "("
								+ df.format(newCoord.getHgLongitudeDegree())
								+ DEGREE + " ,"
								+ df.format(newCoord.getHgLatitudeDegree())
								+ DEGREE + ") ";
					else
						point = "";
					break;

				default:
					break;
				}
				this.setText(title + point);
				return;

			}
		}
		this.setText(title);

	}

	/**
	 * {@inheritDoc}
	 */
	public View getView() {
		return view;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setView(View newView) {
		view = newView;
	}

	/**
	 * {@inheritDoc}
	 */
	public BasicImagePanel getImagePanel() {
		return imagePanel;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setImagePanel(BasicImagePanel newImagePanel) {
		if (imagePanel != null) {
			imagePanel.removeMouseMotionListener(this);
		}
		imagePanel = newImagePanel;
		imagePanel.addMouseMotionListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void mouseDragged(MouseEvent e) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void mouseMoved(MouseEvent e) {
		updatePosition(e.getPoint());
	}

	/**
	 * {@inheritDoc}
	 */
	public void activeLayerChanged(int idx) {
		if (LayersModel.getSingletonInstance().isValidIndex(idx)) {
			setVisible(true);
		} else {
			setVisible(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */

	public void viewportGeometryChanged() {
		// a view change (e.g. a zoom) can change the coordinates in the
		// picture,
		// so we have to recalculate the position
		if (lastPosition != null) {
			updatePosition(lastPosition);
		}
	}

	public void detach() {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isPopupTrigger()) {
			// popup(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			// popup(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	class PopupState extends JPopupMenu {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5268038408623722705L;
		private PopupItemState.PopupItemStates selectedItem = PopupItemState.PopupItemStates.ARCSECS;

		public PopupState() {
			for (PopupItemState.PopupItemStates popupItems : PopupItemState.PopupItemStates
					.values()) {
				this.add(popupItems.popupItem);
				popupItems.popupItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						for (PopupItemState.PopupItemStates popupItems : PopupItemState.PopupItemStates
								.values()) {
							if (popupItems.popupItem == e.getSource()) {
								selectedItem = popupItems;
								break;
							}
						}
						updateText();
					}
				});
			}
			this.updateText();
		}

		private void updateText() {
			for (PopupItemState.PopupItemStates popupItems : PopupItemState.PopupItemStates
					.values()) {
				if (selectedItem == popupItems)
					popupItems.popupItem
							.setText(popupItems.popupItem.selectedText);
				else
					popupItems.popupItem
							.setText(popupItems.popupItem.unselectedText);
			}
		}

		public PopupItemState.PopupItemStates getSelectedState() {
			return this.selectedItem;
		}

	}

	private static class PopupItemState extends JMenuItem {
		private static final long serialVersionUID = -4382532722049627152L;

		public enum PopupItemStates {
			DEGREE("degree"), ARCSECS("arcsecs");

			private PopupItemState popupItem;

			private PopupItemStates(String name) {
				this.popupItem = new PopupItemState(name);
			}

		}

		/**
		 * 
		 */
		private String unselectedText;
		private String selectedText;

		public PopupItemState(String name) {
			this.unselectedText = name;
			this.selectedText = name + "  \u2713";
		}
	}
}
