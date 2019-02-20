package search_algorithm;

import file_operations.analysis_writer.AnalysisWriter;
import model.structure.NodeHandlingType;
import model.web.WebMap;
import model.web.WeightedPoint;
import system_tools.SystemSpecReader;

import java.io.IOException;
import java.util.*;

/**
 * Base class for an algorithm that does all the ground work for preparing and writing analysis file.
 */
public abstract class AnalysableAlgorithm {
    protected WebMap map;
    private Map<String, String> templateValueMap;
    private String reportFilePath;
    private final AnalysisWriter analysisWriter;
    private final SystemSpecReader systemSpecReader; //singleton?

    /**
     * sets given analysis writer instantiates itself
     * @param analysisWriter writer to write analyses on a file
     */
    public AnalysableAlgorithm(AnalysisWriter analysisWriter) {
        this.analysisWriter = analysisWriter;
        templateValueMap = new TreeMap<>();
        systemSpecReader = new SystemSpecReader();
    }

    /**
     * Implementation of the search algorithm
     * During this call handleReportWriting needs to be called with given parameters to get rest of analysis done.
     *
     * @param timeOfStart    time in nanos of when method is called.
     * @param availableSpace space left in jvm heap when method is called.
     * @param fromToNodeSet  place to store which step is taken form where.
     */
    protected abstract void searchAlgorithm(long timeOfStart, long availableSpace, Map<WeightedPoint, WeightedPoint> fromToNodeSet);

    /**
     * Runs implemented algorithm and writes report based on it
     * todo announce completion details...where results written etc
     *
     * @throws IllegalStateException when algorithm or map does not have name
     * @throws IOException           failing to write on designated report file
     */
    public void runSearch() throws IllegalStateException, IOException {
        if (reportFilePath == null) {
            if (map == null || !map.isValid())
                throw new IllegalStateException("Requires valid map and name, and name for algorithm");
            reportFilePath = "/doc/reports/" + map.getName() + "/" + toString();
        }
        Map<WeightedPoint, WeightedPoint> path = new HashMap<>();
        System.out.println("Starting search...");
        searchAlgorithm(System.nanoTime(), systemSpecReader.getAvailableHeapSize(), path);
        fillDefaultTemplateValues(templateValueMap);
        try {
            System.out.println("Analyze completed! Beginning to write report...");
            analysisWriter.writeReport(templateValueMap, reportFilePath);
        } catch (IllegalArgumentException e) {
            System.out.println("Something went with writing the report");
        }
        templateValueMap = new HashMap<>();
    }

    private void fillDefaultTemplateValues(Map<String, String> map) {
        map.put("{algorithm}", toString());
        map.put("{env_cpu}", systemSpecReader.getCpu());
        map.put("{env_os}", systemSpecReader.getOperatingSystem());
        map.put("{env_compiler}", systemSpecReader.getCompiler());
        map.put("{env_runtime}", systemSpecReader.getRuntime());
        map.put("{env_vm_name}", systemSpecReader.getVirtualMachineName());
        map.put("{env_vm_version}", systemSpecReader.getVirtualMachineVersion());
        map.put("{env_heap}", systemSpecReader.getAvailableHeapSizeReadable());
        map.put("{map_info}", "TBD");

        map.put("{al_time}", getTheoreticalTime());
        map.put("{al_space}", getTheoreticalSpace());
        map.put("{al_doc}", getAdditionalDocumentation());

    }

    /**
     * When search is completed add the resulting path and passed parameters in searchAlgorithm to fill the rest of analyze values
     * TODO a bit more description how process works
     * @param path
     * @param startTime
     * @param spaceLeftAtStart
     */
    protected void handleReportWriting(Map<WeightedPoint, WeightedPoint> path, long startTime, long spaceLeftAtStart) {
        System.out.println("Search completed! Beginning to analyze search results...");
        long spaceDifference = systemSpecReader.getAvailableHeapSize() - spaceLeftAtStart;
        templateValueMap.put("{test_space}", "" + spaceDifference);
        templateValueMap.put("{test_used_steps}", "" + (path.size() - 1));
        analyzeSearch(path);
        setTimeElapsed(startTime);
    }

    private void analyzeSearch(Map<WeightedPoint, WeightedPoint> path) {
        List<WeightedPoint> goalPath = new ArrayList<>();
        WeightedPoint locationAt = map.getTileTarget();
        String pathWeight = path.get(locationAt) == null ? "Target was not found" : "" + (int)(locationAt.weight + path.get(locationAt).weight);

        while (locationAt != null) {
            goalPath.add(locationAt);
            locationAt = path.getOrDefault(locationAt, null);
        }
        int max_steps = 0;
        StringBuilder sb = new StringBuilder((map.width() * map.height() * 2) + (map.height() * 2));

        // going through each coordinate we compare it to given path and mark how coordinates were processed
        for (int y = 0; y < map.height(); y++) {
            for (int x = 0; x < map.width(); x++) {
                WeightedPoint coordinate = new WeightedPoint(x, y, map.getLocationWeight(x, y));

                if (map.getTileStart().x == x && map.getTileStart().y == y && map.getTileStart().equals(map.getTileTarget())) {
                    sb.append(NodeHandlingType.IS_START_AND_TARGET.getCharValue());
                    max_steps--;

                } else if (map.getTileStart().x == x && map.getTileStart().y == y) {
                    sb.append(NodeHandlingType.START_LOCATION.getCharValue());
                    max_steps--;

                } else if (map.getTileTarget().x == x && map.getTileTarget().y == y) {
                    if (path.containsKey(coordinate)) {//replan
                        sb.append(NodeHandlingType.TARGET_LOCATION_AND_FOUND.getCharValue());
                    } else {
                        sb.append(NodeHandlingType.TARGET_LOCATION_AND_NOT_FOUND.getCharValue());
                    }

                } else if (coordinate.weight == 0) {
                    sb.append(NodeHandlingType.WALL.getCharValue());
                    max_steps--;

                } else if (goalPath.contains(coordinate)) {
                    sb.append(NodeHandlingType.PROCESSED_IN_PATH.getCharValue());

                } else if (path.containsKey(coordinate)) {
                    sb.append(NodeHandlingType.PROCESSED_NOT_IN_PATH.getCharValue());

                } else {
                    sb.append(NodeHandlingType.NOT_PROCESSED.getCharValue());
                }
                sb.append(' ');
                max_steps++;
            }
            sb.append(System.lineSeparator());
        }
        // take off leading whitespace
        sb.setLength(Math.max(sb.length() - 2, 0));
        //fill analysis values
        templateValueMap.put("{test_processed_map}", sb.toString());
        templateValueMap.put("{test_path_weight}", "" + pathWeight);
        templateValueMap.put("{test_max_steps}", "" + max_steps);
    }


    private void setTimeElapsed(long startTime) {
        long elapsedTime = System.nanoTime() - startTime;
        long nanos = elapsedTime % 1000;
        long milliseconds = (elapsedTime / 1000) % 1000;
        long seconds = (milliseconds / 1000) % 60;
        long minutes = ((milliseconds / (1000 * 60)) % 60);
        long hours = ((milliseconds / (1000 * 60 * 60)) % 24);
        String timeReport = "";
        if (hours > 0) timeReport += "hours " + hours;
        if (minutes > 0) timeReport += " minutes " + minutes;
        if (seconds > 0) timeReport += " seconds " + seconds;
        if (milliseconds > 0) timeReport += " millis " + milliseconds + " ";
        timeReport += "nanos " + nanos;
        templateValueMap.put("{test_time}", timeReport);
    }

    /**
     * Used to fill {al_time} of analysis report
     *
     * @return theoretical time
     */
    public abstract String getTheoreticalTime();

    /**
     * Used to fill {al_space} of analysis report
     *
     * @return theoretical space
     */
    public abstract String getTheoreticalSpace();

    /**
     * Used to fill {al_doc} of analysis report
     *
     * @return additional documentation of implementation
     */
    public abstract String getAdditionalDocumentation();

    /**
     * Directory is based on map name as well and gets cleaned when a map is set
     * for upkeepin reporting structure.
     * Other than that it's a normal setter.
     *
     * @param map map to be set
     */
    public void setMapClean(WebMap map) {
        this.map = map;
        this.reportFilePath = null;
    }

    // for testing
    public WebMap getMap() {
        return map;
    }

    public String getReportFilePath() {
        return reportFilePath;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }
}
