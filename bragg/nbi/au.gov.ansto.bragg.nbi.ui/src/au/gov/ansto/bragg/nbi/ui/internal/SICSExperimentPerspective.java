/*******************************************************************************
 * Copyright (c) 2007 Australian Nuclear Science and Technology Organisation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Norman Xiong (Bragg Institute) - initial API and implementation
 *******************************************************************************/
package au.gov.ansto.bragg.nbi.ui.internal;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SICSExperimentPerspective implements IPerspectiveFactory {

	public static final String SICS_EXPERIMENT_PERSPECTIVE_ID = "au.gov.ansto.bragg.nbi.ui.scripting.ScriptingPerspective";
	public static final String SICS_BATCH_RUNNER_VIEW_ID = "org.gumtree.gumnix.sics.batch.ui.batchBufferManagerView";
	public static final String SICS_TERMINAL_VIEW_ID = "org.gumtree.ui.terminal.commandLineTerminal";
	public static final String PROJECT_EXPLORER_VIEW_ID = "org.eclipse.ui.navigator.ProjectExplorer";
	
	public void createInitialLayout(final IPageLayout factory) {
		factory.addShowViewShortcut(SICS_BATCH_RUNNER_VIEW_ID);
		factory.addShowViewShortcut(SICS_TERMINAL_VIEW_ID);
		factory.addShowViewShortcut(PROJECT_EXPLORER_VIEW_ID);
		
		factory.addPerspectiveShortcut(SICS_EXPERIMENT_PERSPECTIVE_ID);

		IFolderLayout bottomRight =
			factory.createFolder(
				"bottomRight", //NON-NLS-1
				IPageLayout.BOTTOM,
				0.70f,
				factory.getEditorArea());
		bottomRight.addView(SICS_TERMINAL_VIEW_ID);
		
		IFolderLayout bottomLeft  =
			factory.createFolder(
				"bottomLeft", //NON-NLS-1
				IPageLayout.LEFT,
				0.33f,
				"bottomRight");
		bottomLeft.addView(PROJECT_EXPLORER_VIEW_ID);

		IFolderLayout right = factory.createFolder(
				"right", 
				IPageLayout.RIGHT, 
				0.50f, 
				factory.getEditorArea());
		right.addView(SICS_BATCH_RUNNER_VIEW_ID);

		factory.setEditorAreaVisible(true);
		factory.getViewLayout("bottomLeft").setMoveable(false);
		factory.getViewLayout("bottomLeft").setCloseable(false);
		factory.getViewLayout("right").setMoveable(false);
		factory.getViewLayout("right").setCloseable(false);
		factory.setFixed(true);
		
	}


}
