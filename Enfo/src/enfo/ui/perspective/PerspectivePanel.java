package enfo.ui.perspective;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;

import enfo.ui.EnfoView;
import enfo.ui.FilteredTreePanel;

public class PerspectivePanel extends FilteredTreePanel {

	public PerspectivePanel(EnfoView parent) {
		super(parent);
	}

	@Override
	protected ViewerComparator createComparator() {
		return new PerspectiveComparator();
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new PerspectiveContentProvider();
	}

	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new PerspectiveLabelProvider();
	}

	@Override
	protected PatternFilter createPatternFilter() {
		return new PerspectivePatternFilter();
	}

	@Override
	protected String getFilterMessage() {
		return "Search by name or ID";
	}

	@Override
	protected Object getInput() {
		return PlatformUI.getWorkbench().getPerspectiveRegistry();
	}

	@Override
	protected String getMessage(Object treeNode) {
		if (treeNode instanceof IPerspectiveDescriptor) {
			return ((IPerspectiveDescriptor) treeNode).getDescription();
		}
		return super.getMessage(treeNode);
	}
}
