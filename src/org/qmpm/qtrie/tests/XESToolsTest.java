package org.qmpm.qtrie.tests;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.qmpm.qtrie.exceptions.LabelTypeException;
import org.qmpm.qtrie.tools.XESTools;

class XESToolsTest {

	static XLog log;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		log = XESTools.loadXES("./logs/log1.xes", false);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@Test
	void xTraceTest() {
		XTrace trace = log.get(0);
		System.out.println("=== Trace 0 ===");
		for (String key : trace.getAttributes().keySet()) {
			System.out.println(key + ": " + trace.getAttributes().get(key));
		}
		for (XEvent e : trace) {
			System.out.println("ID: " + e.getID());
			XAttributeLiteral label = (XAttributeLiteral) e.getAttributes().get("concept:name");
			System.out.println("LABEL...key: " + label.getKey() + " ...value: " + label.getValue() + " ...extension: "
					+ label.getExtension());
			for (String key : e.getAttributes().keySet()) {

				System.out.println(key + ": " + e.getAttributes().get(key) + "("
						+ e.getAttributes().get(key).getClass().getSimpleName() + ")");
			}
		}
	}

	// @Test
	void toXTraceTest() throws LabelTypeException {

		List<String> strList = Arrays.asList("c", "a", "b");

		XTrace xTrace = XESTools.toXTrace(strList, new XFactoryBufferedImpl());

		try {
			System.out.println(XESTools.xTraceToString(xTrace));
		} catch (LabelTypeException e) {
			fail("");
			e.printStackTrace();
		}
	}

	@Test
	void buildXLogTest() {

		String activities = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvabcdefghijklmnopqrstuvwxyz";
		List<List<String>> strLog = new ArrayList<>();

		XFactory xFactory = new XFactoryBufferedImpl();
		XLog log = xFactory.createLog();

		// String version

		long start = System.nanoTime();

		for (int i = 0; i < 10000; i++) {
			System.out.println(i);
			// XTrace trace = xFactory.createTrace();
			List<String> strList = new ArrayList<>();

			for (int j = 0; j < 100; j++) {
				strList.add(activities.substring((i % 100 + j) / 2, (i % 100 + j) / 2 + 1));
			}

			strLog.add(strList);

			// System.out.println("Building XTrace...");
			// XTrace trace = XESTools.toXTrace(strList, xFactory);
			// System.out.println("Adding XTrace " + i + " to XLog...");
			// log.add(trace);
		}

		long strFinish = System.nanoTime() - start;

		// XLog version

		start = System.nanoTime();

		for (int i = 0; i < 10000; i++) {
			System.out.println(i);
			// XTrace trace = xFactory.createTrace();
			List<String> strList = new ArrayList<>();

			for (int j = 0; j < 100; j++) {
				strList.add(activities.substring((i % 100 + j) / 2, (i % 100 + j) / 2 + 1));
			}

			// System.out.println("Building XTrace...");
			XTrace trace = XESTools.toXTrace(strList, xFactory);
			// System.out.println("Adding XTrace " + i + " to XLog...");
			log.add(trace);
		}

		long xFinish = System.nanoTime() - start;
		System.out.println("STRING: " + strFinish);
		System.out.println("XLOG:   " + xFinish);
	}

}
