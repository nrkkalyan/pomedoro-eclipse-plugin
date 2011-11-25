package enfo.ui.editor;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class EditorContentProvider implements ITreeContentProvider {

	public EditorContentProvider() {
	}

	@Override
	public Object[] getChildren(Object element) {
		if (element instanceof Collection<?>) {
			return ((Collection<?>) element).toArray();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return (element instanceof Collection<?>);
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
