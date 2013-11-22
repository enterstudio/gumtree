package au.gov.ansto.bragg.kookaburra.workbench;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.nebula.widgets.pgroup.AbstractGroupStrategy;
import org.eclipse.nebula.widgets.pgroup.ChevronsToggleRenderer;
import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.nebula.widgets.pgroup.SimpleGroupStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.gumtree.gumnix.sics.ui.widgets.HMVetoGadget;
import org.gumtree.gumnix.sics.ui.widgets.SicsStatusGadget;
import org.gumtree.gumnix.sics.widgets.swt.DeviceStatusWidget;
import org.gumtree.gumnix.sics.widgets.swt.ShutterStatusWidget;
import org.gumtree.service.dataaccess.IDataAccessManager;
import org.gumtree.ui.cruise.support.AbstractCruisePageWidget;
import org.gumtree.util.messaging.IDelayEventExecutor;
import org.gumtree.util.messaging.ReducedDelayEventExecutor;

import au.gov.ansto.bragg.nbi.ui.core.SharedImage;
import au.gov.ansto.bragg.nbi.workbench.ReactorStatusWidget;

@SuppressWarnings("restriction")
public class KookaburraCruisePageWidget extends AbstractCruisePageWidget {

	@Inject
	private IEclipseContext eclipseContext;

	private IDelayEventExecutor delayEventExecutor;

	@Inject
	private IDataAccessManager dataAccessManager;

	public KookaburraCruisePageWidget(Composite parent, int style) {
		super(parent, style);
	}

	@PostConstruct
	public void render() {
		GridLayoutFactory.swtDefaults().spacing(1, 0)
				.applyTo(this);
		getEclipseContext().set(IDelayEventExecutor.class,
				getDelayEventExecutor());
		
		// Reactor Source
		PGroup sourceGroup = createGroup("REACTOR SOURCE",
				SharedImage.REACTOR.getImage());
		ReactorStatusWidget reactorWidget = new ReactorStatusWidget(sourceGroup, SWT.NONE);
		reactorWidget.addDevice("reactorPower", "Power", "MW")
				.addDevice("cnsInTemp", "CNS Inlet Temp", "K")
				.addDevice("cnsOutTemp", "CNS Outlet Temp", "K");
		reactorWidget.createWidgetArea();
		configureWidget(reactorWidget);
		sourceGroup.setExpanded(false);
		reactorWidget.setExpandingEnabled(true);

		// Shutter Status
		PGroup shutterGroup = createGroup("SHUTTER STATUS",
				SharedImage.SHUTTER.getImage());
		ShutterStatusWidget shutterStatuswidget = new ShutterStatusWidget(
				shutterGroup, SWT.NONE);
		configureWidget(shutterStatuswidget);
		shutterGroup.setExpanded(false);

		// Server Status
		PGroup sicsStatusGroup = createGroup("SERVER STATUS", 
				SharedImage.SERVER.getImage());
		SicsStatusGadget statusGadget = new SicsStatusGadget(sicsStatusGroup,
				SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(statusGadget);
		configureWidget(statusGadget);

		// Pause Counter
		PGroup pauseGroup = createGroup("PAUSE COUNTING",
				SharedImage.SHUTTER.getImage());
		HMVetoGadget pauseStatuswidget = new HMVetoGadget(
				pauseGroup, SWT.NONE);
		configureWidget(pauseStatuswidget);

		// Hist Counter
//		PGroup histGroup = createGroup("HISTOGRAM",
//				SharedImage.SHUTTER.getImage());
//		HMImageDisplayWidget hmWidget = new HMImageDisplayWidget(
//				histGroup, SWT.NONE);
//		String path = "http://localhost:60030/admin/openimageinformat.egi&#63;type=HISTOPERIOD_XYT&#38;open_format=DISLIN_PNG&#38;open_colour_table=RAIN&#38;open_plot_zero_pixels=AUTO&#38;open_annotations=ENABLE";
////		String path = "http://localhost:60030/admin/openimageinformat.egi?type=HISTOPERIOD_XYT&open_format=DISLIN_PNG&open_colour_table=RAIN&open_plot_zero_pixels=AUTO&open_annotations=ENABLE";
//		hmWidget.setDataURI(path);
//		configureWidget(hmWidget);

		// Monochromator
		PGroup monochromatorGroup = createGroup("MONOCHROMATOR",
				SharedImage.MONOCHROMATOR.getImage());
		DeviceStatusWidget deviceStatusWidget = new DeviceStatusWidget(monochromatorGroup, SWT.NONE);
		deviceStatusWidget
				.addDevice("/instrument/crystal/m1om", "m1om", null, "deg")
				.addDevice("/instrument/crystal/m1x", "m1x", null, "mm")
				.addDevice("/instrument/crystal/m2om", "m2om", null, "deg")
				.addDevice("/instrument/crystal/m2x", "m2x", null, "mm")
				.addDevice("/instrument/crystal/m2y", "m2y", null, "mm")
				.addDevice("/instrument/crystal/pmchi", "pmchi", null, "deg")
				.addDevice("/instrument/crystal/pmom", "pmom", null, "deg")
				.addDevice("/instrument/crystal/mdet", "mdet", null, "mm")
				;
		configureWidget(deviceStatusWidget);

		// Monitor Event Rate
		PGroup monitorGroup = createGroup("NEUTRON COUNTS",
				SharedImage.MONITOR.getImage());
		deviceStatusWidget = new DeviceStatusWidget(monitorGroup, SWT.NONE);
		deviceStatusWidget
				.addDevice("/monitor/bm1_counts", "BM1 counts", null, "c")
				.addDevice("/monitor/bm2_counts", "BM2 counts", null, "c")
				.addDevice("/instrument/detector/att", "att", null, "mm")
				.addDevice("/instrument/detector/total_counts", "Detector counts", null, "c")
				.addDevice("/instrument/detector/time", "Time of counting", null, "s")
				;
		configureWidget(deviceStatusWidget);

		// Slits Info
		PGroup slitsGroup = createGroup("SLITS STATUS",
				SharedImage.SLITS.getImage());
		deviceStatusWidget = new DeviceStatusWidget(slitsGroup, SWT.NONE);
		deviceStatusWidget
				.addDevice("/instrument/slits/ss1u", "ss1u", null, "mm")
				.addDevice("/instrument/slits/ss1d", "ss1d", null, "mm")
				.addDevice("/instrument/slits/ss1l", "ss1l", null, "mm")
				.addDevice("/instrument/slits/ss1r", "ss1r", null, "mm")
				.addDevice("/instrument/slits/ss2u", "ss2u", null, "mm")
				.addDevice("/instrument/slits/ss2d", "ss2d", null, "mm")
				.addDevice("/instrument/slits/ss2l", "ss2l", null, "mm")
				.addDevice("/instrument/slits/ss2r", "ss2r", null, "mm")
				;
		configureWidget(deviceStatusWidget);


		// Furnace Temp
		PGroup furnaceGroup = createGroup("FURNACE TEMP",
				SharedImage.FURNACE.getImage());
		deviceStatusWidget = new DeviceStatusWidget(furnaceGroup, SWT.NONE);
		deviceStatusWidget
				.addDevice("/sample/tempone/sensorA/value", "temperature")
				.addDevice("/sample/tempone/setpoint", "set point");
		configureWidget(deviceStatusWidget);


		// Temperature TC1 Control
		PGroup tempControlGroup = createGroup("TEMPERATURE CONTR",
				SharedImage.FURNACE.getImage());
		deviceStatusWidget = new DeviceStatusWidget(tempControlGroup, SWT.NONE);
		deviceStatusWidget
				.addDevice("/sample/tc1/sensor/sensorValueA", "TC1A",
						SharedImage.A.getImage(), null)
				.addDevice("/sample/tc1/sensor/sensorValueB", "TC1B",
						SharedImage.B.getImage(), null)
				.addDevice("/sample/tc1/sensor/sensorValueC", "TC1C",
						SharedImage.C.getImage(), null)
				.addDevice("/sample/tc1/sensor/sensorValueD", "TC1D",
						SharedImage.D.getImage(), null);
		configureWidget(deviceStatusWidget);

	}

	@Override
	protected void disposeWidget() {
		if (delayEventExecutor != null) {
			delayEventExecutor.deactivate();
			delayEventExecutor = null;
		}
		eclipseContext = null;
		dataAccessManager = null;
	}

	/*************************************************************************
	 * Components
	 *************************************************************************/

	public IEclipseContext getEclipseContext() {
		return eclipseContext;
	}

	public void setEclipseContext(IEclipseContext eclipseContext) {
		this.eclipseContext = eclipseContext.createChild();
	}

	public IDataAccessManager getDataAccessManager() {
		return dataAccessManager;
	}

	public void setDataAccessManager(IDataAccessManager dataAccessManager) {
		this.dataAccessManager = dataAccessManager;
	}

	public IDelayEventExecutor getDelayEventExecutor() {
		if (delayEventExecutor == null) {
			delayEventExecutor = new ReducedDelayEventExecutor(1000).activate();
		}
		return delayEventExecutor;
	}

	public void setDelayEventExecutor(IDelayEventExecutor delayEventExecutor) {
		this.delayEventExecutor = delayEventExecutor;
	}

	/*************************************************************************
	 * Utilities
	 *************************************************************************/

	protected PGroup createGroup(String text, Image image) {
		PGroup group = new PGroup(this, SWT.NONE);
		AbstractGroupStrategy strategy = new SimpleGroupStrategy();
		group.setStrategy(strategy);
		group.setToggleRenderer(new ChevronsToggleRenderer());
		group.setText(text);
		group.setFont(JFaceResources.getFontRegistry().getBold(
				JFaceResources.DEFAULT_FONT));
		group.setImage(image);
		group.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		group.setLinePosition(SWT.CENTER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(group);
		GridLayoutFactory.swtDefaults().numColumns(1).spacing(1, 1)
				.margins(10, SWT.DEFAULT).applyTo(group);
		return group;
	}

	protected void configureWidget(Control widget) {
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(widget);
		if (getEclipseContext() != null) {
			ContextInjectionFactory.inject(widget, getEclipseContext());
		}
	}

}

