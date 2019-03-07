/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.epam.htmlreporter.factory;

import com.epam.htmlreporter.exceptions.BadExportationDataTypeException;
import com.epam.htmlreporter.exporters.html.HTMLExporter;
import com.epam.htmlreporter.model.Report;
import com.epam.htmlreporter.utils.ReportConfiguration;
import com.epam.htmlreporter.utils.StringManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class ReportFactory {

   /**
    * Property for the word report filename.
    */
   private static final String REPORT_FILENAME = "report.output";
   /**
    * Property for the excel report filename.
    */
   private static final String ISSUES_FILENAME = "issues.output";
   /**
    * Pattern for the name of the directory containing configuration files.
    */
   private static final String CONF_FOLDER_PATTERN = "%s/conf";
   /**
    * Error message returned when the program cannot create a folder because it already exists.
    */
   private static final String CNES_MKDIR_ERROR = "Impossible to create the following directory: %s";
   /**
    * Placeholder for the base directory of reporting.
    */
   private static final String BASEDIR = "BASEDIR";
   /**
    * Placeholder for the date of reporting.
    */
   private static final String DATE = "DATE";
   /**
    * Placeholder for the name of the project.
    */
   private static final String NAME = "NAME";
   /**
    * Logger of this class.
    */
   private static final Logger LOGGER = Logger.getLogger(ReportFactory.class.getName());

   /**
    * Private constructor.
    */
   private ReportFactory() {
   }

   /**
    * Generate report from simple parameters.
    *
    * @param configuration Contains all configuration details.
    * @param model         Contains the report as a Java object model.
    * @throws IOException                     Caused by I/O.
    * @throws BadExportationDataTypeException Caused by export.
    */
   public static void report(final ReportConfiguration configuration, final Report model) throws IOException, BadExportationDataTypeException {

      // Files exporters : export the resources in the correct file type
      final HTMLExporter htmlExporter = new HTMLExporter();

      // Export analysis configuration if requested.
      if (configuration.isEnableConf()) {
      }

      // Export issues and metrics in report if requested.
      if (configuration.isEnableReport()) {

         final String htmlFilename = formatFilename(REPORT_FILENAME, configuration.getOutput(), model.getProjectName());
         // export the full docx report
         htmlExporter.export(model, htmlFilename, configuration.getTemplateReport());
      }

   }




   /**
    * Format a given filename pattern.
    * Add the date and the project's name
    *
    * @param propertyName Name of pattern's property
    * @param projectName  Name of the current project
    * @return a formatted filename
    */
   private static String formatFilename(final String propertyName, final String baseDir, final String projectName) {
      // construct the filename by replacing date and name
      return StringManager.getProperty(propertyName)
              .replaceFirst(BASEDIR, baseDir)
              .replaceAll(DATE, new SimpleDateFormat(StringManager.DATE_PATTERN).format(new Date()))
              .replaceAll(NAME, projectName);
   }

}
