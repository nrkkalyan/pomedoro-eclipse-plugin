package enfo.ui.command;

import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class CommandLabelProvider extends LabelProvider {

	private Image sharedImg;

	public CommandLabelProvider() {
		super();
		sharedImg = PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_OBJ_FOLDER);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Command) {
			try {
				return ((Command) element).getName();
			} catch (NotDefinedException e) {
				return ((Command) element).getId();
			}
		} else if (element instanceof Category) {
			try {
				return ((Category) element).getName();
			} catch (NotDefinedException e) {
				return ((Category) element).getId();
			}
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof Category) {
			return sharedImg;
		}
		return super.getImage(element);
	}
}
