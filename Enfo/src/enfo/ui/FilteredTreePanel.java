package enfo.ui;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

public abstract class FilteredTreePanel extends Panel implements
		IDoubleClickListener, ISelectionChangedListener {

	private EnfoView view;
	private FilteredTree fTree;

	public FilteredTreePanel(EnfoView parent) {
		super();
		view = parent;
	}

	protected ViewerComparator createComparator() {
		return new ViewerComparator();
	}

	protected abstract ITreeContentProvider createContentProvider();

	@Override
	public Composite createContents(Composite p) {

		fTree = new FilteredTree(p, getStyles(), createPatternFilter(), true);
		fTree.getFilterControl().setMessage(getFilterMessage());
		fTree.setBackground(PlatformUI.getWorkbench().getDisplay()
				.getSystemColor(SWT.COLOR_WHITE));

		final TreeViewer viewer = fTree.getViewer();
		viewer.addDoubleClickListener(this);
		viewer.addSelectionChangedListener(this);
		viewer.setComparator(createComparator());
		viewer.setLabelProvider(createLabelProvider());
		viewer.setContentProvider(createContentProvider());
		viewer.setInput(getInput());

		return p;
	}

	protected IBaseLabelProvider createLabelProvider() {
		return new LabelProvider();
	}

	protected PatternFilter createPatternFilter() {
		return new PatternFilter();
	}

	@Override
	public void doubleClick(DoubleClickEvent e) {
		IStructuredSelection select = (IStructuredSelection) e.getSelection();
		Object o = select.getFirstElement();

		TreeViewer viewer = getFilteredTree().getViewer();
		if (((ITreeContentProvider) viewer.getContentProvider()).hasChildren(o)) {
			viewer.setExpandedState(o, !viewer.getExpandedState(o));
		} else {
			doubleClickOnLeaf(o);
		}
	}

	protected void doubleClickOnLeaf(Object leaf) {
	}

	protected FilteredTree getFilteredTree() {
		return fTree;
	}

	protected String getFilterMessage() {
		return "Search";
	}

	protected String getMessage(Object treeNode) {
		return "";
	}

	protected abstract Object getInput();

	protected int getStyles() {
		return (SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent e) {
//		Object o = ((IStructuredSelection) e.getSelection()).getFirstElement();
//		view.setContentDescription(getMessage(o));
	}
	
	protected EnfoView getParent() {
		return view;
	}

}
