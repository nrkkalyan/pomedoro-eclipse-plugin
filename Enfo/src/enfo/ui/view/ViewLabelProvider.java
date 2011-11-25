package enfo.ui.view;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewCategory;
import org.eclipse.ui.views.IViewDescriptor;

public class ViewLabelProvider extends LabelProvider {

	private ImageRegistry images;

	/**
	 * Not to be disposed.
	 */
	private Image sharedImg;

	public ViewLabelProvider() {
		images = new ImageRegistry(PlatformUI.getWorkbench().getDisplay());
		sharedImg = PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_FOLDER);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IViewCategory) {
			return sharedImg;

		} else if (element instanceof IViewDescriptor) {

			IViewDescriptor view = (IViewDescriptor) element;
			Image img = images.get(view.getId());
			if (img == null) {
				img = view.getImageDescriptor().createImage();
				images.put(view.getId(), img);
			}
			return img;
		}
		return null;
	}

	@Override
	public void dispose() {
		images.dispose();
		super.dispose();
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IViewDescriptor) {
			return ((IViewDescriptor) element).getLabel().replace("&", "");
		} else if (element instanceof IViewCategory) {
			return ((IViewCategory) element).getLabel().replace("&", "");
		}
		return null;
	}
}
