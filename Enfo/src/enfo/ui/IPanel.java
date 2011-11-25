package enfo.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

public interface IPanel {
	
	Composite createContents(Composite parent);
	
	void saveState(IMemento memento);
	
	void restoreState(IMemento memento);
	
	void dispose();

}
