package enfo.ui.perspective;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

public class PerspectiveLabelProvider extends LabelProvider {
	
	private ImageRegistry images;
	
	public PerspectiveLabelProvider() {
		images = new ImageRegistry(PlatformUI.getWorkbench().getDisplay());
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IPerspectiveDescriptor) {
			IPerspectiveDescriptor p = (IPerspectiveDescriptor) element;
			Image img = images.get(p.getId());
			if (img == null) {
				img = p.getImageDescriptor().createImage();
				images.put(p.getId(), img);
			}
			return img;
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IPerspectiveDescriptor) {
			return ((IPerspectiveDescriptor) element).getLabel();
		}
		return super.getText(element);
	}

	@Override
	public void dispose() {
		images.dispose();
		super.dispose();
	}

}
