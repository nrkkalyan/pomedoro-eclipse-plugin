package enfo.ui.perspective;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.dialogs.PatternFilter;

public class PerspectivePatternFilter extends PatternFilter {

	public PerspectivePatternFilter() {
		super();
	}
	
	@Override
	public boolean isElementSelectable(Object element) {
		return (element instanceof IPerspectiveDescriptor);
	}
	
	@Override
	protected boolean isLeafMatch(Viewer viewer, Object element) {
		if (element instanceof IPerspectiveDescriptor) {
			IPerspectiveDescriptor p = (IPerspectiveDescriptor) element;
			return (wordMatches(p.getLabel()) || (wordMatches(p.getId())));
		}
		return false;
	}
}
