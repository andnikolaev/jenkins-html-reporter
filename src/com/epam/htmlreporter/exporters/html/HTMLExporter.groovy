package com.epam.htmlreporter.exporters.html;

import com.epam.htmlreporter.exceptions.BadExportationDataTypeException;
import com.epam.htmlreporter.exporters.IExporter
import com.epam.htmlreporter.model.Issue
import com.epam.htmlreporter.model.Report;
import com.hp.gagawa.java.elements.Div

import java.nio.charset.Charset;

//// https://mvnrepository.com/artifact/com.hp.gagawa/gagawa
//@Grapes(
//        @Grab(group = 'com.hp.gagawa', module = 'gagawa', version = '1.0.1')
//)

//@Grab(group = 'commons-io', module = 'commons-io', version = '2.4')
import org.apache.commons.io.IOUtils
import org.apache.commons.io.FileUtils

public class HTMLExporter implements IExporter {

    @Override
    public File export(Object data, String path, String filename) throws IOException, BadExportationDataTypeException {
        if (!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }

        final Report report = (Report) data;
        File htmlFileReport = null;
        InputStream fileInputStream = null;
        try {
            fileInputStream = getClass().getResourceAsStream('/template/template.html')
            String htmlString = IOUtils.toString(fileInputStream, Charset.forName('UTF-8'));
            htmlString = setTitle(htmlString, report.getProjectName());
            htmlString = setAuthor(htmlString, report.getProjectName());
            htmlString = setIssues(htmlString, exportIssuesToHTML(report.getIssues()));
            htmlFileReport = new File(path);
            FileUtils.writeStringToFile(htmlFileReport, htmlString);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }

        return htmlFileReport;
    }

    private String setAuthor(String htmlString, String author) {
        return htmlString.replace('$author', author);
    }

    private String setTitle(String htmlString, String projectName) {
        return htmlString.replace('$title', projectName);
    }

    private String setIssues(String htmlString, String issuesHtml) {
        return htmlString.replace('$issues', issuesHtml);
    }

    private String exportIssuesToHTML(List<Issue> issueList) {
        Map<String, List<Issue>> result = JavaUtility.convertToMap(issueList);
        return exportIssuesToHTML(result);
    }

    private String exportIssuesToHTML(Map<String, List<Issue>> issueMap) {
        Div div = new Div().setCSSClass('article');
        for (String issueClass : issueMap.keySet()) {
            div.appendChild(generateArticleHeaderWithClassName(issueClass));
            div.appendChild(generateArticleBody(issueMap.get(issueClass)));
        }
        return div.write();
    }

    private Div generateArticleHeaderWithClassName(String className) {
        Div div = new Div().setCSSClass('article_header');
        div.appendText(className);
        return div;
    }

    private Div generateArticleBody(List<Issue> issueList) {
        Div div = new Div().setCSSClass('article_body');
        for (Issue issue : issueList) {
            Div divItem = new Div().setCSSClass('article_body_item');
            divItem.appendChild(generateItemDiv('article_item_line', 'Line', issue.getLine()));
            divItem.appendChild(generateItemDiv('article_item_priority', 'Priority', issue.getSeverity()));
            divItem.appendChild(generateItemDiv('article_item_type', 'Type', issue.getType()));
            divItem.appendChild(generateItemDiv('article_item_message', 'Message', issue.getMessage()));

            div.appendChild(divItem);
        }

        return div;
    }

    private Div generateItemDiv(String divClass, String divCapture, String divContent) {
        Div itemDiv = new Div().setCSSClass(divClass);
        Div titleDiv = new Div().setCSSClass('article_item_title');
        titleDiv.appendText(divCapture);
        Div lineNumber = new Div();
        lineNumber.appendText(divContent);
        itemDiv.appendChild(titleDiv).appendChild(lineNumber);
        return itemDiv;
    }
}
