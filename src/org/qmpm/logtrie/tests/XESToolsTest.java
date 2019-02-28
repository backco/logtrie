package org.qmpm.logtrie.tests;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.qmpm.logtrie.exceptions.LabelTypeException;
import org.qmpm.logtrie.tools.XESTools;

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
			for (String key : e.getAttributes().keySet()) {
				XAttributeLiteral label = (XAttributeLiteral) e.getAttributes().get("concept:name");
				System.out.println("LABEL...key: " + label.getKey() + " ...value: " + label.getValue()
						+ " ...extension: " + label.getExtension());
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
}
