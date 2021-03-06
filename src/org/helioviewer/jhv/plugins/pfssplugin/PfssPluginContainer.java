package org.helioviewer.jhv.plugins.pfssplugin;

import org.helioviewer.jhv.plugins.viewmodelplugin.overlay.OverlayContainer;
import org.helioviewer.jhv.plugins.viewmodelplugin.overlay.OverlayControlComponent;
import org.helioviewer.jhv.plugins.viewmodelplugin.overlay.OverlayControlComponentManager;
import org.helioviewer.jhv.viewmodel.renderer.physical.PhysicalRenderer3d;
import org.helioviewer.jhv.viewmodel.view.OverlayView;
import org.helioviewer.jhv.viewmodel.view.opengl.OverlayPluginContainer;

/**
 * Plugincontainer for Pfss
 * 
 * @author Stefan Meier, Jonas Schwammberger
 */
public class PfssPluginContainer extends OverlayContainer
{
	private PfssPluginPanel pfssPluginPanel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void installOverlayImpl(OverlayView overlayView,
			OverlayControlComponentManager controlList) {
		PfssPlugin3dRenderer renderer = new PfssPlugin3dRenderer();
		pfssPluginPanel = new PfssPluginPanel(renderer);
		OverlayPluginContainer overlayPluginContainer = new OverlayPluginContainer();
		overlayPluginContainer
				.setRenderer3d(renderer);
		overlayView.addOverlay(overlayPluginContainer);
		controlList
				.add(new OverlayControlComponent(pfssPluginPanel, getName()));

	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName()
	{
		return "PFSS";
	}


	@Override
	public Class<? extends PhysicalRenderer3d> getOverlayClass() {
		return PfssPlugin3dRenderer.class;
	}

}
