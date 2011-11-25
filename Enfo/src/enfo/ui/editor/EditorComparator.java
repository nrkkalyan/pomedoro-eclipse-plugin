package enfo.ui.editor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.ui.IEditorDescriptor;

public class EditorComparator extends ViewerComparator {

	public EditorComparator() {
		super();
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if ((e1 instanceof IEditorDescriptor)
				&& (e2 instanceof IEditorDescriptor)) {

			return ((IEditorDescriptor) e1).getLabel().compareTo(
					((IEditorDescriptor) e2).getLabel());
		}
		return super.compare(viewer, e1, e2);
	}

}
