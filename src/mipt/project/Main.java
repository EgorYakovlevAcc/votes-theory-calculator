package mipt.project;

import org.jscience.mathematics.number.FloatingPoint;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
    private static final int FIRST_GROUP_PEOPLE_AMOUNT = 300;
    private static final int SECOND_GROUP_PEOPLE_AMOUNT = 300;
    private static final int SUGGESTIONS_AMOUNT = 1000;
    private static final String FILE_STR_TEMPLATE = "%s,%s";

    public static void main(String[] args) {
        FloatingPoint.setDigits(10);
        long startTime = System.currentTimeMillis();
        System.out.println("START");
        TwoGroupsModel twm = new TwoGroupsModel(
                FIRST_GROUP_PEOPLE_AMOUNT,
                SECOND_GROUP_PEOPLE_AMOUNT,
                SUGGESTIONS_AMOUNT
        );
        List<MuModelResult> muModelResults = twm.getMuAndMaxSPKForAll();

        Map<FloatingPoint, List<MuModelResult>> aCoefMuModelResultMap = muModelResults.stream()
                .collect(Collectors.groupingBy(MuModelResult::getaCoef, Collectors.toList()));

        for (Map.Entry<FloatingPoint, List<MuModelResult>> aCoefMuModelResults : aCoefMuModelResultMap.entrySet()) {
            Map<FloatingPoint, FloatingPoint> resultsToFile = aCoefMuModelResults.getValue().stream()
                    .collect(Collectors.toMap(MuModelResult::getMuValue, MuModelResult::gettValue));
            writeResultToCsvFile(new TreeMap<>(resultsToFile), "MU-test-" + aCoefMuModelResults.getKey());
            Map<FloatingPoint, ModelResult> map = aCoefMuModelResults.getValue().stream()
                            .collect(Collectors.toMap(MuModelResult::getMuValue, MuModelResult::getModelResult));
            for (Map.Entry<FloatingPoint, ModelResult> entry: map.entrySet()) {
                writeResultToCsvFile(new TreeMap<>(entry.getValue().getFirstGroupResult()), "test-first" + entry.getKey() + "-" + aCoefMuModelResults.getKey());
                writeResultToCsvFile(new TreeMap<>(entry.getValue().getSecondGroupResult()), "test-second" + entry.getKey() + "-" + aCoefMuModelResults.getKey());
            }
        }

//        List<ModelResult> modelResults = twm.calculate();
////        Map<Double, Double> map = modelResults.stream()
////                .collect(Collectors.toMap(ModelResult::getMean, Main::getTForMaxSumOfGroups));
////        map.entrySet().stream()
////                .forEach(e -> System.out.println(e.getKey() + ", " + e.getValue()));
//        for (ModelResult modelResult : modelResults) {
//            Map<FloatingPoint, FloatingPoint> firstGroupResult = new TreeMap(modelResult.getFirstGroupResult());
//            Map<FloatingPoint, FloatingPoint> secondGroupResult = new TreeMap(modelResult.getSecondGroupResult());
//            writeResultToCsvFile(firstGroupResult, "two-groups-case-first-" + getPrefix(modelResult) + "-a-" + modelResult.getaCoef());
//            writeResultToCsvFile(secondGroupResult, "two-groups-case-second-" + getPrefix(modelResult) + "-a-" + modelResult.getaCoef());
//        }
        System.out.println("FINISH");
        long finishTime = System.currentTimeMillis();
        System.out.println("YOU SPEND = " + (finishTime - startTime) + " ms");
//    }

//    private static Double getTForMaxSumOfGroups(ModelResult modelResult) {
//        Map<Double, Double> map = Stream.concat(modelResult.getFirstGroupResult().entrySet().stream(), modelResult.getSecondGroupResult().entrySet().stream())
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        Double::sum));
//        return map.entrySet().stream()
//                .max(Map.Entry.comparingByValue())
//                .map(Map.Entry::getKey)
//                .orElseThrow();
    }


    private static String getPrefix(ModelResult modelResult) {
        return modelResult.getModifiedModel() ? "modified" : "ordinary";
    }

    private static void writeResultToCsvFile(Map<FloatingPoint, FloatingPoint> data, String filename) {
        try {
            FileWriter csvWriter = new FileWriter(filename + ".csv");
            csvWriter.append("VALUE_1");
            csvWriter.append(",");
            csvWriter.append("VALUE_2");
            csvWriter.append("\n");
            for (Map.Entry<FloatingPoint, FloatingPoint> row : data.entrySet()) {
                csvWriter.append(String.format(FILE_STR_TEMPLATE, row.getKey().doubleValue(), row.getValue().doubleValue()));
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
