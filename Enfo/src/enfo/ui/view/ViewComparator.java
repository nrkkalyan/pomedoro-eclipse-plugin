package enfo.ui.view;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.ui.views.IViewCategory;
import org.eclipse.ui.views.IViewDescriptor;

public class ViewComparator extends ViewerComparator {

	public ViewComparator() {
		super();
	}

	private static final String GENERNAL_CATEGORY = "General";
	private static final String OTHER_CATEGORY = "Other";

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		String text1 = "";
		String text2 = "";
		if ((e1 instanceof IViewDescriptor) && (e2 instanceof IViewDescriptor)) {
			text1 = ((IViewDescriptor) e1).getLabel();
			text2 = ((IViewDescriptor) e2).getLabel();

		} else if ((e1 instanceof IViewCategory)
				&& (e2 instanceof IViewCategory)) {
			text1 = ((IViewCategory) e1).getLabel();
			text2 = ((IViewCategory) e2).getLabel();
		}

		text1 = text1.replace("&", "");
		text2 = text2.replace("&", "");
		
		if (text1.equals(GENERNAL_CATEGORY) || text2.equals(OTHER_CATEGORY)) {
			return -1;
		} else if (text1.equals(OTHER_CATEGORY)
				|| text2.equals(GENERNAL_CATEGORY)) {
			return 1;
		}

		return text1.compareTo(text2);
	}

}
