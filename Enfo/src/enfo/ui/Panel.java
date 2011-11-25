package enfo.ui;

import org.eclipse.ui.IMemento;

public abstract class Panel implements IPanel {

	public Panel() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void restoreState(IMemento memento) {
	}

	@Override
	public void saveState(IMemento memento) {
	}

}
