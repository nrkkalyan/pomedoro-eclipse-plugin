package enfo.ui.editor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.dialogs.PatternFilter;

public class EditorPatternFilter extends PatternFilter {
	
	public EditorPatternFilter() {
		super();
	}
	
	@Override
	public boolean isElementSelectable(Object element) {
		return (element instanceof IEditorDescriptor);
	}
	
	@Override
	protected boolean isLeafMatch(Viewer viewer, Object element) {
		if (element instanceof IEditorDescriptor) {
			IEditorDescriptor edit = (IEditorDescriptor) element;
			return (wordMatches(edit.getLabel()) || wordMatches(edit.getId()));
		}
		return false;
	}

}
