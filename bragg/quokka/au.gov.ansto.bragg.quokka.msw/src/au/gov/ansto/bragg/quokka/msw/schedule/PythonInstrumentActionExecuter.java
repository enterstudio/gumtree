package au.gov.ansto.bragg.quokka.msw.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.script.ScriptEngine;

import org.gumtree.msw.RefId;
import org.gumtree.msw.elements.Element;
import org.gumtree.msw.schedule.execution.AcquisitionSummary;
import org.gumtree.msw.schedule.execution.InitializationSummary;
import org.gumtree.msw.schedule.execution.ParameterChangeSummary;
import org.gumtree.msw.schedule.execution.Summary;

import au.gov.ansto.bragg.quokka.msw.ExperimentDescription;
import au.gov.ansto.bragg.quokka.msw.ModelProvider;
import au.gov.ansto.bragg.quokka.msw.User;

public class PythonInstrumentActionExecuter implements IInstrumentExecuter {
	// finals
	private static final String ID_TAG = "%%ID%%";
	private static final String PY_IMPORT_MSW = "import bragg.quokka.msw as msw";
	private static final String PY_SET_CONSOLE_WRITER = "msw.setConsoleWriter(%%ID%%)";
	private static final String PY_INITIATE = "msw.initiate(%%ID%%)";
	private static final String PY_CLEAN_UP = "msw.cleanUp(%%ID%%)";
	private static final String PY_SET_PARAMETERS_SCRIPT = "msw.setParameters(%%ID%%)";
	private static final String PY_PRE_ACQUISITION_SCRIPT = "msw.preAcquisition(%%ID%%)";
	private static final String PY_DO_ACQUISITION_SCRIPT = "msw.doAcquisition(%%ID%%)";
	private static final String PY_POST_ACQUISITION_SCRIPT = "msw.postAcquisition(%%ID%%)";
	private static final String PY_CUSTOM_ACTION_SCRIPT = "msw.customAction(%%ID%%)";
	// java/python interface
	private static final AtomicLong atomicId = new AtomicLong();
	private static final Map<Long, Object> idMap = new HashMap<>();
	
	// fields
	private final ScriptEngine engine;
	private final ModelProvider modelProvider;
	
	// construction
	public PythonInstrumentActionExecuter(ScriptEngine engine, ModelProvider modelProvider) {
		this.engine = engine;
		this.modelProvider = modelProvider;

		long id = atomicId.incrementAndGet();
		setObject(id, engine.getContext().getWriter());
		try {
			engine.eval(PY_IMPORT_MSW);
			engine.eval(PY_SET_CONSOLE_WRITER.replaceAll(ID_TAG, Long.toString(id) + 'L'));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			removeObject(id);
		}
	}
	
	// helper
	private long getPocessingTime(long startTime) {
		long nano = System.nanoTime() - startTime;
		return nano / 1000 / 1000 / 1000;
	}
	
	// methods
	@Override
	public InitializationSummary initiate() {
		PyInitiateInfo info = null;
		long startTime = System.nanoTime();
		
		try {
			List<User> users = new ArrayList<>();
			modelProvider.getUserList().fetchElements(users);
			Collections.sort(users, Element.INDEX_COMPARATOR);
			
			StringBuilder usersStr = new StringBuilder();
			for (User user : users) {
				String name = user.getName();
				if ((name != null) && (name.length() > 0)) {
					if (usersStr.length() != 0)
						usersStr.append(", ");
					usersStr.append(name);
				}
			}

			ExperimentDescription experimentDescription = modelProvider.getExperimentDescription();
			info = new PyInitiateInfo(
					experimentDescription.getProposalNumber(),
					experimentDescription.getExperimentTitle(),
					experimentDescription.getSampleStage(),
					usersStr.toString());
			
			long id = atomicId.incrementAndGet();
			setObject(id, info);
			try {
				engine.eval(PY_IMPORT_MSW);
				engine.eval(PY_INITIATE.replaceAll(ID_TAG, Long.toString(id) + 'L'));
			}
			finally {
				removeObject(id);
			}
		}
		catch (Exception e) {
			info = null;
			e.printStackTrace();
		}
		
		if (info == null)
			return new InitializationSummary(
					Collections.<String, Object>emptyMap(),
					getPocessingTime(startTime),
					true,
					null);
		else
			return new InitializationSummary(
					Collections.<String, Object>emptyMap(),
					info.proposalNumber,
					info.experimentTitle,
					info.sampleStage,
					getPocessingTime(startTime),
					info.interrupted,
					info.notes);
	}
	@Override
	public Summary cleanUp() {
		PyInfo info = null;
		long startTime = System.nanoTime();
		
		try {
			info = new PyInfo();

			long id = atomicId.incrementAndGet();
			setObject(id, info);
			try {
				engine.eval(PY_IMPORT_MSW);
				engine.eval(PY_CLEAN_UP.replaceAll(ID_TAG, Long.toString(id) + 'L'));
			}
			finally {
				removeObject(id);
			}
		}
		catch (Exception e) {
			info = null;
			e.printStackTrace();
		}
		
		if (info == null)
			return new Summary(
					Collections.<String, Object>emptyMap(),
					getPocessingTime(startTime),
					true,
					null);
		else
			return new Summary(
					Collections.<String, Object>emptyMap(),
					getPocessingTime(startTime),
					info.interrupted,
					info.notes);
	}
	// steps
	@Override
	public ParameterChangeSummary setParameters(String name, Map<String, Object> parameters) {
		PyParameterInfo info = null;
		long startTime = System.nanoTime();
		
		try {
			int indexId = name.indexOf(RefId.HASH);
			if (indexId != -1)
				name = name.substring(0, indexId);
			
			info = new PyParameterInfo(name, parameters);
			
			long id = atomicId.incrementAndGet();
			setObject(id, info);
			try {
				engine.eval(PY_IMPORT_MSW);
				engine.eval(PY_SET_PARAMETERS_SCRIPT.replaceAll(ID_TAG, Long.toString(id) + 'L'));
			}
			finally {
				removeObject(id);
			}
		}
		catch (Exception e) {
			info = null;
			e.printStackTrace();
		}
		
		if (info == null)
			return new ParameterChangeSummary(
					name,
					parameters,
					getPocessingTime(startTime),
					true,
					null);
		else
			return new ParameterChangeSummary(
					name,
					parameters,
					getPocessingTime(startTime),
					info.interrupted,
					info.notes);
	}
	@Override
	public Summary preAcquisition() {
		PyInfo info = null;
		long startTime = System.nanoTime();
		
		try {
			info = new PyInfo();
			
			long id = atomicId.incrementAndGet();
			setObject(id, info);
			try {
				engine.eval(PY_IMPORT_MSW);
				engine.eval(PY_PRE_ACQUISITION_SCRIPT.replaceAll(ID_TAG, Long.toString(id) + 'L'));
			}
			finally {
				removeObject(id);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		if (info == null)
			return new Summary(
					Collections.<String, Object>emptyMap(),
					getPocessingTime(startTime),
					true,
					null);
		else
			return new Summary(
					Collections.<String, Object>emptyMap(),
					getPocessingTime(startTime),
					info.interrupted,
					info.notes);
	}
	@Override
	public AcquisitionSummary doAcquisition(Map<String, Object> parameters) {
		PyAcquisitionInfo info = null;
		long startTime = System.nanoTime();
		
		try {
			info = new PyAcquisitionInfo(parameters);

			long id = atomicId.incrementAndGet();
			setObject(id, info);
			try {
				engine.eval(PY_IMPORT_MSW);
				engine.eval(PY_DO_ACQUISITION_SCRIPT.replaceAll(ID_TAG, Long.toString(id) + 'L'));
			}
			finally {
				removeObject(id);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (info == null)
			return new AcquisitionSummary(
					parameters,
					getPocessingTime(startTime),
					true,
					null);
		else
			return new AcquisitionSummary(
					parameters,
					info.filename,
					info.totalSeconds,
					info.totalCounts,
					info.monitorCounts,
					getPocessingTime(startTime),
					info.interrupted,
					info.notes);
	}
	@Override
	public Summary postAcquisition() {
		PyInfo info = null;
		long startTime = System.nanoTime();
		
		try {
			info = new PyInfo();
			
			long id = atomicId.incrementAndGet();
			setObject(id, info);
			try {
				engine.eval(PY_IMPORT_MSW);
				engine.eval(PY_POST_ACQUISITION_SCRIPT.replaceAll(ID_TAG, Long.toString(id) + 'L'));
			}
			finally {
				removeObject(id);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		if (info == null)
			return new Summary(
					Collections.<String, Object>emptyMap(),
					getPocessingTime(startTime),
					true,
					null);
		else
			return new Summary(
					Collections.<String, Object>emptyMap(),
					getPocessingTime(startTime),
					info.interrupted,
					info.notes);
	}
	// custom
	@Override
	public Summary customAction(String action, Map<String, Object> parameters) {
		PyCustomActionInfo info = null;
		long startTime = System.nanoTime();
		
		try {
			info = new PyCustomActionInfo(action, parameters);
			
			long id = atomicId.incrementAndGet();
			setObject(id, info);
			try {
				engine.eval(PY_IMPORT_MSW);
				engine.eval(PY_CUSTOM_ACTION_SCRIPT.replaceAll(ID_TAG, Long.toString(id) + 'L'));
			}
			finally {
				removeObject(id);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		if (info == null)
			return new Summary(
					parameters,
					getPocessingTime(startTime),
					true,
					null);
		else
			return new Summary(
					parameters,
					getPocessingTime(startTime),
					info.interrupted,
					info.notes);
	}
	
	// java/python interface
	public static synchronized Object getObject(long id) {
		return idMap.get(id);
	}
	private static synchronized void setObject(long id, Object object) {
		idMap.put(id, object);
	}
	private static synchronized void removeObject(long id) {
		idMap.remove(id);
	}

	public static class PyInfo {
		// fields
		public boolean interrupted;
		public String notes;
		
		// construction
		public PyInfo() {
			this.interrupted = false;
			this.notes = null;
		}
	}

	public static class PyInitiateInfo extends PyInfo {
		// fields
		public String proposalNumber;
		public String experimentTitle;
		public String sampleStage;
		// others
		public String users;
		
		// construction
		public PyInitiateInfo(String proposalNumber, String experimentTitle, String sampleStage, String users) {
			this.proposalNumber = proposalNumber;
			this.experimentTitle = experimentTitle;
			this.sampleStage = sampleStage;
			this.users = users;
		}
	}
	
	public static class PyParameterInfo extends PyInfo {
		// fields
		public String name;
		public Map<String, Object> parameters;
		
		// construction
		public PyParameterInfo(String name, Map<String, Object> parameters) {
			this.name = name;
			this.parameters = parameters;
		}
	}
	
	public static class PyAcquisitionInfo extends PyInfo {
		// fields
		public Map<String, Object> parameters; // optional: MinTime, MaxTime, Counts, BmCounts
		public String filename;
		public long totalSeconds;
		public long totalCounts;
		public long monitorCounts;
		
		// construction
		public PyAcquisitionInfo(Map<String, Object> parameters) {
			this.parameters = parameters;
			this.filename = null;
			this.totalSeconds = 0;
			this.totalCounts = 0;
			this.monitorCounts = 0;
		}
	}

	public static class PyCustomActionInfo extends PyInfo {
		// fields
		public String action;
		public Map<String, Object> parameters;
		
		// construction
		public PyCustomActionInfo(String action, Map<String, Object> parameters) {
			this.action = action;
			this.parameters = parameters;
		}
	}
}
