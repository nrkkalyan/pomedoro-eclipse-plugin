package enfo.ui.view;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.views.IViewDescriptor;

public class ViewFilter extends ViewerFilter {
	
	private static final String WELCOME_VIEW_ID = "org.eclipse.ui.internal.introview";

	public ViewFilter() {
		super();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ((element instanceof IViewDescriptor)
				&& ((IViewDescriptor) element).getId().equals(WELCOME_VIEW_ID)) {
			return false;
		}
		return true;
	}

}
