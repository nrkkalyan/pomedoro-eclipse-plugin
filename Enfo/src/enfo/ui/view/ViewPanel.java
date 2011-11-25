package enfo.ui.view;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.views.IViewCategory;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

import enfo.ui.EnfoView;
import enfo.ui.FilteredTreePanel;

public class ViewPanel extends FilteredTreePanel {

	private static final String MEMENTO_CATEGORY = "expandedViewCategories";
	private static final String MEMENTO_ELEMENT = "categoryId";

	public ViewPanel(EnfoView p) {
		super(p);
	}

	@Override
	protected ViewerComparator createComparator() {
		return new ViewComparator();
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new ViewContentProvider();
	}

	@Override
	public Composite createContents(Composite parent) {
		Composite cmp = super.createContents(parent);

		TreeViewer viewer = getFilteredTree().getViewer();
		viewer.addFilter(createFilter());
		viewer.addSelectionChangedListener(this);
		viewer.addDoubleClickListener(this);
		return cmp;
	}

	protected ViewerFilter createFilter() {
		return new ViewFilter();
	}

	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new ViewLabelProvider();
	}

	@Override
	protected PatternFilter createPatternFilter() {
		return new ViewPatternFilter();
	}

	@Override
	protected void doubleClickOnLeaf(Object o) {
		if (!(o instanceof IViewDescriptor)) {
			return;
		}

		IWorkbenchPage pg = getParent().getSite().getWorkbenchWindow()
				.getActivePage();
		if (pg == null) {
			return;
		}

		try {
			IViewDescriptor v = (IViewDescriptor) o;
			pg.showView(v.getId(), null, IWorkbenchPage.VIEW_VISIBLE);
		} catch (PartInitException ex) {
		}
		super.doubleClickOnLeaf(o);
	}

	@Override
	protected String getFilterMessage() {
		return "Search by name or ID";
	}

	@Override
	protected Object getInput() {
		return PlatformUI.getWorkbench().getViewRegistry();
	}

	@Override
	protected String getMessage(Object o) {
		if (o instanceof IViewCategory) {
			return ((IViewCategory) o).getId();

		} else if (o instanceof IViewDescriptor) {
			return ((IViewDescriptor) o).getId();
		}
		return super.getMessage(o);
	}

	@Override
	public void restoreState(IMemento memento) {
		IMemento m = memento.getChild(MEMENTO_CATEGORY);
		if (m == null) {
			return;
		}

		Set<IViewCategory> result = new HashSet<IViewCategory>();
		IViewRegistry r = PlatformUI.getWorkbench().getViewRegistry();
		for (IViewCategory c : r.getCategories()) {
			for (IMemento child : m.getChildren(MEMENTO_ELEMENT)) {
				if (c.getId().equals(child.getTextData())) {
					result.add(c);
				}
			}
		}
		getFilteredTree().getViewer().setExpandedElements(result.toArray());
	}

	@Override
	public void saveState(IMemento memento) {
		IMemento m = memento.createChild(MEMENTO_CATEGORY);
		Object[] expanded = getFilteredTree().getViewer().getExpandedElements();
		for (Object o : expanded) {
			m.createChild(MEMENTO_ELEMENT).putTextData(
					((IViewCategory) o).getId());
		}
	}
}
