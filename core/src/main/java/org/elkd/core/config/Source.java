package org.elkd.core.config;

import java.util.Map;

interface Source {
  Map<String, String> apply(Map<String, String> map);
}
