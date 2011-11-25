package enfo.ui.command;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.PatternFilter;

import enfo.ui.EnfoView;
import enfo.ui.FilteredTreePanel;

public class CommandPanel extends FilteredTreePanel {

	private static final String MEMENTO_CATEGORY = "expandedCommandCategories";
	private static final String MEMENTO_ELEMENT = "categoryId";

	public CommandPanel(EnfoView parent) {
		super(parent);
	}

	@Override
	protected ViewerComparator createComparator() {
		return new CommandComparator();
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new CommandContentProvider();
	}

	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new CommandLabelProvider();
	}

	@Override
	protected PatternFilter createPatternFilter() {
		return new CommandPatternFilter();
	}

	@Override
	protected String getFilterMessage() {
		return "Search by name or ID";
	}

	@Override
	protected Object getInput() {
		return PlatformUI.getWorkbench().getService(ICommandService.class);
	}

	@Override
	protected String getMessage(Object treeNode) {
		if (treeNode instanceof Category) {
			return ((Category) treeNode).getId();
			
		} else if (treeNode instanceof Command) {
			Command cmd = (Command) treeNode;
			try {
				return cmd.getId() + "    " + cmd.getDescription();
			} catch (NotDefinedException e) {
				return cmd.getId();
			}
		}
		return super.getMessage(treeNode);
	}

	@Override
	public void restoreState(IMemento memento) {
		IMemento m = memento.getChild(MEMENTO_CATEGORY);
		if (m == null) {
			return;
		}

		Set<Category> result = new HashSet<Category>();
		ICommandService service = (ICommandService) PlatformUI.getWorkbench()
				.getService(ICommandService.class);
		for (Category c : service.getDefinedCategories()) {
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
					((Category) o).getId());
		}
	}

}
