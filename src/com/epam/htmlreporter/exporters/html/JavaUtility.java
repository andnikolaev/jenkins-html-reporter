package com.epam.htmlreporter.exporters.html;

import com.epam.htmlreporter.model.Issue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JavaUtility {

   public static Map<String, List<Issue>> convertToMap(List<Issue> issues) {
      return issues.stream().collect(Collectors.groupingBy(Issue::getComponent));
   }

}
