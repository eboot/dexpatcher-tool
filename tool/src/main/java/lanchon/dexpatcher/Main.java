/*
 * DexPatcher - Copyright 2015-2017 Rodrigo Balerdi
 * (GNU General Public License version 3 or later)
 *
 * DexPatcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 */

package lanchon.dexpatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import lanchon.dexpatcher.core.logger.BasicLogger;
import lanchon.dexpatcher.core.logger.Logger;

import org.apache.commons.cli.ParseException;

import static lanchon.dexpatcher.core.logger.Logger.Level.*;

public class Main {

	public static void main(String[] args) {
		Locale locale = new Locale("en", "US");
		Locale.setDefault(locale);
		int value = run(args);
		System.exit(value);
	}

	public static int run(String[] args) {
		return run(args, new BasicLogger());
	}

	public static int run(String[] args, Logger logger) {
		try {
			boolean success = runWithExceptions(args, logger);
			return success ? 0 : 1;
		} catch (ParseException e) {
			logger.log(FATAL, e.getMessage());
			Parser.printUsage();
			return 2;
		} catch (Exception e) {
			if (logger.isLogging(DEBUG)) {
				logger.log(FATAL, "exception:", e);
			} else {
				logger.log(FATAL, "exception: " + e);
			}
			return 3;
		} finally {
			logger.flush();
		}
	}

	public static boolean runWithExceptions(String[] args, Logger logger) throws ParseException, IOException {
		try {
			Configuration config = Parser.parseCommandLine(args);
			if (config == null) return true;
			return Processor.processFiles(logger, config);
		} finally {
			logger.flush();
		}
	}

	public static String getVersion() {
		String version = "<undefined>";
		final String FILE = "version";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(FILE)));
			version = reader.readLine().trim();
			reader.close();
		} catch (Exception e) {}
		return version;
	}

}
