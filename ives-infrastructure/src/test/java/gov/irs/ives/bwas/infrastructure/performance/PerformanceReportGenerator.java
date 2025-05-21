package gov.irs.ives.bwas.infrastructure.performance;

import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.stream.Collectors;

public class PerformanceReportGenerator {

    private static final String REPORT_DIR = "performance-reports";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public static void generateReport(String... testClasses) throws RunnerException, IOException {
        // Create reports directory if it doesn't exist
        Path reportDir = Paths.get(REPORT_DIR);
        if (!Files.exists(reportDir)) {
            Files.createDirectories(reportDir);
        }

        // Run benchmarks
        Options opt = new OptionsBuilder()
                .include(testClasses)
                .build();
        Collection<RunResult> results = new Runner(opt).run();

        // Generate report
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String reportPath = reportDir.resolve("performance_report_" + timestamp + ".html").toString();

        try (FileWriter writer = new FileWriter(reportPath)) {
            writer.write(generateHtmlReport(results));
        }

        System.out.println("Performance report generated: " + reportPath);
    }

    private static String generateHtmlReport(Collection<RunResult> results) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
            .append("<html>\n<head>\n")
            .append("<title>Performance Test Report</title>\n")
            .append("<style>\n")
            .append("body { font-family: Arial, sans-serif; margin: 20px; }\n")
            .append("table { border-collapse: collapse; width: 100%; margin-top: 20px; }\n")
            .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n")
            .append("th { background-color: #f2f2f2; }\n")
            .append("tr:nth-child(even) { background-color: #f9f9f9; }\n")
            .append(".summary { margin: 20px 0; padding: 10px; background-color: #f0f0f0; }\n")
            .append("</style>\n")
            .append("</head>\n<body>\n")
            .append("<h1>Performance Test Report</h1>\n")
            .append("<div class='summary'>\n")
            .append("<p>Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("</p>\n")
            .append("<p>Total Benchmarks: ").append(results.size()).append("</p>\n")
            .append("</div>\n")
            .append("<table>\n")
            .append("<tr>\n")
            .append("<th>Benchmark</th>\n")
            .append("<th>Mode</th>\n")
            .append("<th>Threads</th>\n")
            .append("<th>Score</th>\n")
            .append("<th>Error</th>\n")
            .append("<th>Unit</th>\n")
            .append("</tr>\n");

        results.forEach(result -> {
            html.append("<tr>\n")
                .append("<td>").append(result.getPrimaryResult().getLabel()).append("</td>\n")
                .append("<td>").append(result.getPrimaryResult().getMode().toString()).append("</td>\n")
                .append("<td>").append(result.getParams().getThreads()).append("</td>\n")
                .append("<td>").append(String.format("%.2f", result.getPrimaryResult().getScore())).append("</td>\n")
                .append("<td>").append(String.format("%.2f", result.getPrimaryResult().getScoreError())).append("</td>\n")
                .append("<td>").append(result.getPrimaryResult().getScoreUnit()).append("</td>\n")
                .append("</tr>\n");
        });

        html.append("</table>\n")
            .append("</body>\n</html>");

        return html.toString();
    }

    public static void main(String[] args) throws RunnerException, IOException {
        generateReport(
            "gov.irs.ives.bwas.infrastructure.clients.EssarClientPerformanceTest",
            "gov.irs.ives.bwas.infrastructure.clients.FBPClientPerformanceTest"
        );
    }
} 