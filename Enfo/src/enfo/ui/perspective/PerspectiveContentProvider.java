package enfo.ui.perspective;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IPerspectiveRegistry;

public class PerspectiveContentProvider implements ITreeContentProvider {

	public PerspectiveContentProvider() {
		super();
	}

	@Override
	public Object[] getChildren(Object o) {
		if (o instanceof IPerspectiveRegistry) {
			return ((IPerspectiveRegistry) o).getPerspectives();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return (element instanceof IPerspectiveRegistry);
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
