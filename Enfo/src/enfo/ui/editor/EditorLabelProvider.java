package enfo.ui.editor;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class EditorLabelProvider extends LabelProvider {

	private ImageRegistry images;

	public EditorLabelProvider() {
		images = new ImageRegistry(PlatformUI.getWorkbench().getDisplay());
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof Object[]) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_FOLDER);

		} else if (element instanceof IEditorDescriptor) {
			IEditorDescriptor editor = (IEditorDescriptor) element;
			String id = editor.getId();
			Image img = images.get(id);
			if (img == null) {
				img = editor.getImageDescriptor().createImage();
				images.put(id, img);
			}
			return img;
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Object[]) {
			return "Editors";

		} else if (element instanceof IEditorDescriptor) {
			return ((IEditorDescriptor) element).getLabel();
		}
		return null;
	}

	@Override
	public void dispose() {
		images.dispose();
		super.dispose();
	}
}
