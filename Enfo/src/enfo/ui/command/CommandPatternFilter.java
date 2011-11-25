package enfo.ui.command;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

public class CommandPatternFilter extends PatternFilter {

	public CommandPatternFilter() {
		super();
	}
	
	@Override
	protected boolean isLeafMatch(Viewer viewer, Object element) {
		if (element instanceof Command) {
			Command cmd = (Command) element;
			try {
				return (wordMatches(cmd.getName())) || (wordMatches(cmd.getId()));
			} catch (NotDefinedException e) {
				return wordMatches(cmd.getId());
			}
		}
		return false;
	}
	
	@Override
	public boolean isElementSelectable(Object element) {
		return (element instanceof Command);
	}
}
