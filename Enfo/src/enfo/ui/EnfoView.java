package enfo.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import enfo.ui.command.CommandPanel;
import enfo.ui.editor.EditorPanel;
import enfo.ui.perspective.PerspectivePanel;
import enfo.ui.view.ViewPanel;

public class EnfoView extends ViewPart {

	private static final String MEMENTO_SELECTION_INDEX = "tabFolderSelectionIndex";
	private IMemento memento;
	private CTabFolder folder;
	

	private final IPanel[] panels = new IPanel[] {
			new ViewPanel(this),
			new EditorPanel(this),
			new PerspectivePanel(this),
			new CommandPanel(this),
	};

	private final String[] panelNames = new String[] {
			"Views",
			"Editors",
			"Perspectives",
			"Commands",
	};
	
	public EnfoView() {
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		this.memento = memento;
		super.init(site, memento);
	}

	@Override
	public void createPartControl(Composite parent) {
		
		folder = new CTabFolder(parent, SWT.BOTTOM);
		for (int i = 0; i < panels.length; i++) {
			Composite cmp = new Composite(folder, SWT.NONE);
			cmp.setLayout(new FillLayout());
			
			CTabItem item = new CTabItem(folder, SWT.NONE);
			item.setControl(panels[i].createContents(cmp));
			item.setText(panelNames[i]);
		}
		folder.setSelection(0);
		
		if (memento != null) {
			IMemento m = memento.getChild(MEMENTO_SELECTION_INDEX);
			if (m != null) {
				try {
					folder.setSelection(Integer.parseInt(m.getTextData()));
				} catch (Exception e) {
				}
			}
			for (IPanel p : panels) {
				p.restoreState(memento);
			}
		}
	}
	
	@Override
	public void setContentDescription(String description) {
		super.setContentDescription(description);
	}
	
	@Override
	public void saveState(IMemento memento) {
		IMemento m = memento.createChild(MEMENTO_SELECTION_INDEX);
		m.putTextData(Integer.toString(folder.getSelectionIndex()));
		for (IPanel p : panels) {
			p.saveState(memento);
		}
	}

	@Override
	public void setFocus() {
	}

}
