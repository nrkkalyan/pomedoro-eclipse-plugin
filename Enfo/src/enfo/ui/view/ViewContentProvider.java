package enfo.ui.view;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.IViewCategory;
import org.eclipse.ui.views.IViewRegistry;

public class ViewContentProvider implements ITreeContentProvider {
	
	public ViewContentProvider() {
	}

	@Override
	public Object[] getChildren(Object element) {
		if (element instanceof IViewRegistry) {
			return ((IViewRegistry) element).getCategories();
		} else if (element instanceof IViewCategory) {
			return ((IViewCategory) element).getViews();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object e) {
		return (e instanceof IViewRegistry) || (e instanceof IViewCategory);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
