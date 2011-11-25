package enfo.ui.editor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;

import enfo.ui.EnfoView;
import enfo.ui.FilteredTreePanel;

public class EditorPanel extends FilteredTreePanel {

	public EditorPanel(EnfoView p) {
		super(p);
	}

	@Override
	protected ViewerComparator createComparator() {
		return new EditorComparator();
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new EditorContentProvider();
	}

	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new EditorLabelProvider();
	}

	@Override
	protected PatternFilter createPatternFilter() {
		return new EditorPatternFilter();
	}

	@Override
	protected String getFilterMessage() {
		return "Search by name or ID";
	}

	@Override
	protected Object getInput() {
		Set<IEditorDescriptor> editors = new HashSet<IEditorDescriptor>();
		IEditorRegistry registry = PlatformUI.getWorkbench()
				.getEditorRegistry();
		
		for (IConfigurationElement e : Platform.getExtensionRegistry()
				.getConfigurationElementsFor("org.eclipse.ui.editors")) {
			
			IEditorDescriptor edit = registry.findEditor(e.getAttribute("id"));
			if (edit != null) {
				editors.add(edit);
			}
		}
		return editors;
	}

	@Override
	protected String getMessage(Object treeNode) {
		if (treeNode instanceof IEditorDescriptor) {
			IEditorDescriptor editor = (IEditorDescriptor) treeNode;
			StringBuilder builder = new StringBuilder();
			builder.append(editor.getLabel());
			builder.append("\n");
			builder.append(editor.getId());
			return builder.toString();
		}
		return super.getMessage(treeNode);
	}
}
