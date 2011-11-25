package enfo.ui.perspective;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.ui.IPerspectiveDescriptor;

public class PerspectiveComparator extends ViewerComparator {

	public PerspectiveComparator() {
		super();
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if ((e1 instanceof IPerspectiveDescriptor)
				&& (e2 instanceof IPerspectiveDescriptor)) {

			return ((IPerspectiveDescriptor) e1).getLabel().compareTo(
					((IPerspectiveDescriptor) e2).getLabel());
		}
		return super.compare(viewer, e1, e2);
	}
}
