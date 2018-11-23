package org.elkd.core.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConfigCLISource implements Source {

  private static final Option CLUSTER_SET = Option.builder()
      .required(false)
      .desc("nodes to be included in your static cluster.")
      .longOpt("cluster-set")
      .numberOfArgs(1)
      .build();

  private static final Option DATA_DIR = Option.builder()
      .desc("directory to store data")
      .longOpt("data-dir")
      .numberOfArgs(1)
      .build();

  private static final Option HELP = Option.builder()
      .desc("print this message")
      .longOpt("help")
      .build();

  private static final Option PORT = Option.builder()
      .desc("port to bind server")
      .longOpt("port")
      .numberOfArgs(1)
      .build();

  private final Options mOptions = new Options();
  private final HashMap<String, String> mConfig = new HashMap<>();

  /* package */ ConfigCLISource(final String[] args) throws Exception {
    this(args, new Option[] {
        CLUSTER_SET,
        DATA_DIR,
        HELP,
        PORT
    });
  }

  @VisibleForTesting
  ConfigCLISource(final String[] args, final Option[] options) throws Exception {
    Preconditions.checkNotNull(options, "options");
    for (final Option option : options) {
      mOptions.addOption(option);
    }
    parse(args);
  }

  private void parse(final String[] args) throws Exception {
    final CommandLineParser cliParser = new DefaultParser();
    final CommandLine cli;
    try {
      cli = cliParser.parse(mOptions, args);
      if (cli.hasOption("help")) {
        showHelp();
        throw new Exception();
      }

      final Iterator<Option> iterator = cli.iterator();
      while (iterator.hasNext()) {
        final Option next = iterator.next();
        mConfig.put(convertConfigKey(next.getLongOpt()), next.getValue());
      }
    } catch (final ParseException e) {
      showHelp();
      throw e;
    }
  }

  private void showHelp() {
    new HelpFormatter().printHelp("elkd-server", mOptions);
  }

  @Override
  public Map<String, String> apply(final Map<String, String> map) {
    map.putAll(mConfig);
    return map;
  }

  private String convertConfigKey(final String key) {
    return key.replaceAll("-", ".");
  }
}
