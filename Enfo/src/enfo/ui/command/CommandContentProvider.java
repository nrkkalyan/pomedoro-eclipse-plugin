package enfo.ui.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.commands.ICommandService;

public class CommandContentProvider implements ITreeContentProvider {
	
	private Set<Command> commands;
	
	public CommandContentProvider() {
		commands = new HashSet<Command>();
	}

	@Override
	public Object[] getChildren(Object o) {
		if (o instanceof ICommandService) {
			ICommandService service = (ICommandService) o;
			commands.addAll(Arrays.asList(service.getDefinedCommands()));
			return service.getDefinedCategories();
			
		} else if (o instanceof Category) {
			Category cat = (Category) o;
			Set<Command> children = new HashSet<Command>();
			for (Command cmd : commands) {
				try {
					if (cmd.getCategory().equals(cat)) {
						children.add(cmd);
					}
				} catch (NotDefinedException e) {
					e.printStackTrace();
				}
			}
			return children.toArray();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
//		if (element instanceof Command) {
//			try {
//				return ((Command) element).getCategory();
//			} catch (NotDefinedException e) {
//				return null;
//			}
//		}
		return null;
	}

	@Override
	public boolean hasChildren(Object o) {
		return ((o instanceof ICommandService) || (o instanceof Category));
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
