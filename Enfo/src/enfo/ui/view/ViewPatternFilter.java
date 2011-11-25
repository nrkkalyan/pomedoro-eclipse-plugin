package enfo.ui.view;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.views.IViewDescriptor;

public class ViewPatternFilter extends PatternFilter {

	public ViewPatternFilter() {
		super();
	}
	
	@Override
	protected boolean isLeafMatch(Viewer viewer, Object element) {
		if (element instanceof IViewDescriptor) {
			IViewDescriptor v = (IViewDescriptor) element;
			return wordMatches(v.getLabel()) || wordMatches(v.getId());
		}
		return false;
	}

	@Override
	public boolean isElementSelectable(Object element) {
		return (element instanceof IViewDescriptor);
	}

}
