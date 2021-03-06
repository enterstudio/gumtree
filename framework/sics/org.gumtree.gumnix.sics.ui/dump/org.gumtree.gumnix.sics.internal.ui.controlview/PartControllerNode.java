package org.gumtree.gumnix.sics.internal.ui.controlview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;
import org.gumtree.gumnix.sics.control.IDeviceController;
import org.gumtree.gumnix.sics.control.IPartController;
import org.gumtree.gumnix.sics.internal.ui.Activator;
import org.gumtree.gumnix.sics.internal.ui.controlview.ControlViewerConstants.Column;
import org.gumtree.ui.util.ITreeNode;
import org.gumtree.ui.util.TreeNode;

import ch.psi.sics.hipadaba.Device;
import ch.psi.sics.hipadaba.Part;

public class PartControllerNode extends TreeNode {

	private static Image imagePart;

	static {
		if(Activator.getDefault() != null) {
			imagePart = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/full/obj16/package_obj.gif").createImage();
		}
	}

	public PartControllerNode(IPartController controller, StructuredViewer viewer) {
		super(controller, viewer);
	}

	public ITreeNode[] getChildren() {
		Widget item = getViewer().testFindItem(this);
		item.setData("componentController", getController());
		List<ITreeNode> children = new ArrayList<ITreeNode>();
		for(IPartController partController : getController().getChildPartControllers()) {
			children.add(new PartControllerNode(partController, getViewer()));
		}
		for(IDeviceController deviceController : getController().getChildDeviceControllers()) {
			children.add(new DeviceControllerNode(deviceController, getViewer()));
		}
		return children.toArray(new ITreeNode[children.size()]);
	}

	public String getColumnText(int columnIndex) {
		if(columnIndex == Column.NODE.getIndex()) {
			String text = getPart().getLabel();
			if(text == null || text.equals(EMPTY_STRING)) {
				text = getPart().getId();
			}
			if(text != null) {
				return text;
			}
		}
		return EMPTY_STRING;
	}

	public Image getColumnImage(int columnIndex) {
		if(columnIndex == Column.NODE.getIndex()) {
			return imagePart;
		}
		return null;
	}

	private IPartController getController() {
		return (IPartController)getOriginalObject();
	}

	private Part getPart() {
		return getController().getPart();
	}
}
