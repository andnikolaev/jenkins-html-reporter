package com.epam.htmlreporter

import com.epam.htmlreporter.exceptions.BadExportationDataTypeException
import com.epam.htmlreporter.exceptions.BadSonarQubeRequestException
import com.epam.htmlreporter.exceptions.SonarQubeException
import com.epam.htmlreporter.exceptions.UnknownQualityGateException
import com.epam.htmlreporter.factory.ReportFactory
import com.epam.htmlreporter.factory.ReportModelFactory
import com.epam.htmlreporter.factory.ServerFactory
import com.epam.htmlreporter.model.Report
import com.epam.htmlreporter.model.SonarQubeServer
import com.epam.htmlreporter.utils.ReportConfiguration

import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

public final class Reporter {

    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(ReportFactory.class.getName());

    /**
     * Private constructor to not be able to instantiate it.
     */
    private Reporter() {
    }

    /**
     * Main method.
     * See help message for more information about using this program.
     * Entry point of the program.
     *
     * @param args Arguments that will be preprocessed.
     */
    public static void main(final String[] args) {
        Reporter reporter = new Reporter();
        reporter.start(args);
    }

    private void start(String[] args) {
        // main catches all exceptions
        try {
            // Log message.
            String message;

            // Parse command line arguments.
            final ReportConfiguration conf = ReportConfiguration.create(args);

            // Display version information and exit.
            if (conf.isVersion()) {
                final String name = Reporter.class.getPackage().getImplementationTitle();
                final String version = Reporter.class.getPackage().getImplementationVersion();
                final String vendor = Reporter.class.getPackage().getImplementationVendor();
                message = String.format("%s %s by %s", name, version, vendor);
                LOGGER.info(message);
                System.exit(0);
            }

            // Print information about SonarQube.
            message = String.format("SonarQube URL: %s", conf.getServer());
            LOGGER.info(message);

            // Initialize connexion with SonarQube and retrieve primitive information
            final SonarQubeServer server = new ServerFactory(conf.getServer(), conf.getToken()).create();

            message = String.format("SonarQube online: %s", server.isUp());
            LOGGER.info(message);

            if (!server.isUp()) {
                throw new SonarQubeException("Impossible to reach SonarQube instance.");
            }

            message = String.format("Detected SonarQube version: %s", server.getNormalizedVersion());
            LOGGER.info(message);

            if (!server.isSupported()) {
                throw new SonarQubeException("SonarQube instance is not supported by cnesreport.");
            }

            // Generate the model of the report.
            final Report model = new ReportModelFactory(server, conf).create();
            // Generate results files.
            ReportFactory.report(conf, model);

            message = "Report generation: SUCCESS";
            LOGGER.info(message);

        } catch (BadExportationDataTypeException | BadSonarQubeRequestException | IOException | UnknownQualityGateException | SonarQubeException e) {
            // it logs all the stack trace
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            System.exit(-1);
        }
    }

}
