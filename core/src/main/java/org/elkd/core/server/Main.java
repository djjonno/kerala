package org.elkd.core.server;

import org.elkd.client.lib.TestModel;

import java.util.logging.Logger;

public class Main {
	private static final Logger LOG = Logger.getLogger(Main.class.getName());

	public static void main(final String[] args) {
		final TestModel model = new TestModel("elkdModel");
		LOG.info("Running elkd server");
		LOG.info(model.toString());
	}
}
