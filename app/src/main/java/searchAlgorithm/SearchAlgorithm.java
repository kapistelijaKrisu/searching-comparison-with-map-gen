package searchAlgorithm;

import IOoperations.analysisWriter.AnalysisWriter;
import model.WebMap;
import systemTools.SystemSpecReader;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Base class that does all the ground work for preparing analysis file.
 * work in progress
 */
public abstract class SearchAlgorithm {
    protected WebMap map;
    private Map<String, String> templateValueMap;
    private String reportFilePath;
    private final AnalysisWriter analysisWriter;
    private final SystemSpecReader systemSpecReader; //singleton?


    public SearchAlgorithm(AnalysisWriter analysisWriter) {
        this.analysisWriter = analysisWriter;
        templateValueMap = new HashMap<>();
        systemSpecReader = new SystemSpecReader();
    }

    /**
     * Implementation of the search algorithm
     * During this call handleReportWriting needs to be called to get rest or report done.
     * @param timeOfStart time in nanos of when method is called.
     * @param getAvailableSpace space left when method is called.
     */
    protected abstract void searchAlgorithm(long timeOfStart, long getAvailableSpace);

    /**
     * Runs implemented algorithm and writes report based on it
     * todo announce completion details...where results written etc
     * @throws IllegalStateException when algorithm or map does not have name
     * @throws IOException failing to write on designated report file
     */
    public void runSearch() throws IllegalStateException, IOException {
        if (reportFilePath == null) {
            if (map == null || !map.isValid()) throw new IllegalStateException("Requires valid map and name, and name for algorithm");
            reportFilePath = "/doc/reports/" + map.getName() + "/" + toString();
        }
        searchAlgorithm(System.nanoTime(), systemSpecReader.getAvailableHeapSize());
        fillDefaultTemplateValues(templateValueMap);
        try {
            analysisWriter.writeReport(templateValueMap, reportFilePath);
        } catch (IllegalArgumentException e) {
            System.out.println("Something went with writing the report");
        }
        templateValueMap = new HashMap<>();
    }

    /**
     * Pre-fill analysis template map
     * @param map Internal map used to fill analysis template
     * Uses implementations of toString(), getTheoreticalTime(), getTheoreticalSpace(), getAdditionalDocumentation()
     */
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

    //todo tidy this into smaller bits
    protected void handleReportWriting(Map<Point, Point> path, Set<Point> visited, long startTime, long spaceLeftAtStart) {
        long elapsedTime = System.nanoTime() - startTime;
        long spaceDifference = systemSpecReader.getAvailableHeapSize() - spaceLeftAtStart;
        int[][] baseMap = map.getMap();
        StringBuilder sb = new StringBuilder(baseMap.length * baseMap[0].length + baseMap.length);
        int max_steps = 0;
        int pathWeight = 0;
        sb.append("How map was processed").append(System.lineSeparator());
        for (int y = 0; y < baseMap.length; y++) {
            for (int x = 0; x < baseMap[0].length; x++) {
                if (map.getTileAt().x == x && map.getTileAt().y == y && map.getTileAt().equals(map.getTileTarget())) {
                    sb.append('O');
                }else if (map.getTileAt().x == x && map.getTileAt().y == y) {
                    sb.append('S');
                }else if (map.getTileTarget().x == x && map.getTileTarget().y == y) {
                    max_steps++;
                    pathWeight += baseMap[y][x];
                    sb.append('T');
                } else if (baseMap[y][x] == 0) { //wall
                    sb.append('w');
                } else if (path.containsKey(new Point(x, y)) || path.containsValue(new Point(x, y))) {
                    sb.append('x');
                    max_steps++;
                    pathWeight += baseMap[y][x];
                } else if (visited.contains(new Point(x, y))) {
                    sb.append('v');
                    max_steps++;
                    pathWeight += baseMap[y][x];
                } else {
                    sb.append('.');
                    max_steps++;
                }
            }
            sb.append(System.lineSeparator());
        }


        long nanos = elapsedTime % 1000;
        long millis = elapsedTime / 1000 % 1000;
        long sec = elapsedTime / (1000 * 1000) % (1000 * 1000);
        long mins = elapsedTime / (1000L * 1000 * 60) % (1000L * 1000 * 60 * 60);
        long hours = elapsedTime / (1000L * 1000 * 60 * 60) % (1000L * 1000 * 60 * 60);
        String timeReport = "";
        if (hours != 0)  timeReport += "hours " + hours;
        if (mins != 0)  timeReport += " minutes " + mins;
        if (sec != 0)  timeReport += " seconds " + sec;
        if (millis != 0)  timeReport += " millis " + millis + " ";
        timeReport += "nanos " + nanos;

        templateValueMap.put("{test_processed_map}", sb.toString());
        templateValueMap.put("{test_time}", timeReport);
        templateValueMap.put("{test_space}", "" + spaceDifference);
        templateValueMap.put("{test_used_steps}", "" + (path.size() - 1));
        templateValueMap.put("{test_path_weight}", "" + pathWeight);
        templateValueMap.put("{test_max_steps}", "" + max_steps);
    }

    /**
     * Used to fill {al_time} of analysis report
     * @return theoretical time
     */
    public abstract String getTheoreticalTime();
    /**
     * Used to fill {al_space} of analysis report
     * @return theoretical space
     */
    public abstract String getTheoreticalSpace();
    /**
     * Used to fill {al_doc} of analysis report
     * @return additional documentation of implementation
     */
    public abstract String getAdditionalDocumentation();

    /**
     * Directory is based on map name as well and gets cleaned when a map is set
     * for upkeepin reporting structure.
     * Other than that it's a normal setter.
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
