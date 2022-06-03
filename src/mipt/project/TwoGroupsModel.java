package mipt.project;

import org.jscience.mathematics.number.FloatingPoint;
import org.jscience.mathematics.vector.DenseMatrix;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Matrix;
import org.jscience.mathematics.vector.Vector;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class TwoGroupsModel {
    private static final boolean IS_MODEL_MODIFIED = true;
    private static final FloatingPoint GAUSS_MEAN = FloatingPoint.ZERO;
    private static final FloatingPoint START_GAUSS_MEAN = FloatingPoint.valueOf(-1000);
    private static final FloatingPoint END_GAUSS_MEAN = FloatingPoint.valueOf(1000);
    private static final Integer NET_SIZE_GAUSS_MEAN = 5;
    private static final FloatingPoint GAUSS_VARIANCE = FloatingPoint.valueOf(10);
    private static final FloatingPoint START_BALANCE = FloatingPoint.ZERO;
    private static final FloatingPoint START_T_VALUE = FloatingPoint.valueOf(-1000);
    private static final FloatingPoint END_T_VALUE = FloatingPoint.valueOf(1000);
    private static final List<FloatingPoint> A_COEFS = List.of(FloatingPoint.valueOf("0"), FloatingPoint.valueOf("0.5"));
    private static final Integer NET_SIZE = 20;
    //    private static final FloatingPoint UNIFORMITY_BOUNDARY = FloatingPoint.valueOf(500);
//    private static final Integer NUMBER_OF_POINTS_AT_THE_ENDS = 1;
    private int firstGroupPeopleAmount;
    private int secondGroupPeopleAmount;
    private int suggestionsAmount;

    public TwoGroupsModel(int firstGroupPeopleAmount, int secondGroupPeopleAmount, int suggestionsAmount) {
        this.firstGroupPeopleAmount = firstGroupPeopleAmount;
        this.secondGroupPeopleAmount = secondGroupPeopleAmount;
        this.suggestionsAmount = suggestionsAmount;
    }

    public int getFirstGroupPeopleAmount() {
        return firstGroupPeopleAmount;
    }

    public void setFirstGroupPeopleAmount(int firstGroupPeopleAmount) {
        this.firstGroupPeopleAmount = firstGroupPeopleAmount;
    }

    public int getSecondGroupPeopleAmount() {
        return secondGroupPeopleAmount;
    }

    public void setSecondGroupPeopleAmount(int secondGroupPeopleAmount) {
        this.secondGroupPeopleAmount = secondGroupPeopleAmount;
    }

    public int getSuggestionsAmount() {
        return suggestionsAmount;
    }

    public void setSuggestionsAmount(int suggestionsAmount) {
        this.suggestionsAmount = suggestionsAmount;
    }

    public List<ModelResult> calculate() {
        return calculate(GAUSS_MEAN, A_COEFS);
    }

    public List<MuModelResult> getMuAndMaxSPKForAll() {
        System.out.println("GENERATE NET [GAUSS MEAN] [START]");
        List<FloatingPoint> gaussMeanNet = generateNet(START_GAUSS_MEAN, END_GAUSS_MEAN, NET_SIZE_GAUSS_MEAN);
        System.out.println("GENERATE NET [GAUSS MEAN] [FINISH]");

        List<MuModelResult> muModelResultList = new ArrayList<>();
        for (FloatingPoint gaussMean : gaussMeanNet) {
            Map<FloatingPoint, TAndValueResult> aCoefAndTValueAndResultMap = calculate(gaussMean, A_COEFS).parallelStream()
                    .collect(Collectors.toMap(ModelResult::getaCoef, x -> getTValueAndMaxSpk(x.getAllGroupsResult())));
            for (Map.Entry<FloatingPoint, TAndValueResult> aCoefAndTValueAndResult : aCoefAndTValueAndResultMap.entrySet()) {
                MuModelResult muModelResult = new MuModelResult(gaussMean);
                muModelResult.setaCoef(aCoefAndTValueAndResult.getKey());

                TAndValueResult tAndValueResult = aCoefAndTValueAndResult.getValue();
                muModelResult.settValue(tAndValueResult.gettValue());
                muModelResult.setMaxSpk(tAndValueResult.getSpk());
                muModelResultList.add(muModelResult);
            }
        }
        return muModelResultList;
    }

    private TAndValueResult getTValueAndMaxSpk(Map<FloatingPoint, FloatingPoint> tValuesAndSPKsMap) {
        return tValuesAndSPKsMap.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(TAndValueResult::new)
                .orElseThrow();
    }

    public List<ModelResult> calculate(FloatingPoint gaussMean, List<FloatingPoint> aCoefs) {
        List<ModelResult> mrs = null;
        System.out.println("GENERATE SUGGESTIONS MATRIX [START]");
        int peopleAmount = this.firstGroupPeopleAmount + this.secondGroupPeopleAmount;
        final Matrix<FloatingPoint> suggestionsMatrix =
                generateSuggestionsMatrix(
                        peopleAmount,
                        this.suggestionsAmount,
                        gaussMean
                );
        System.out.println("GENERATE SUGGESTIONS MATRIX [FINISH]");

        System.out.println("GENERATE START BALANCES [START]");
        final DenseVector<FloatingPoint> startBalances = generatePeopleStartBalancesVector(peopleAmount);
        System.out.println("GENERATE START BALANCES [FINISH]");

        System.out.println("GENERATE NET [START]");
        List<FloatingPoint> tNet = generateNet(START_T_VALUE, END_T_VALUE, NET_SIZE);
        System.out.println("GENERATE NET [FINISH]");

        System.out.println("EXECUTE VOTING PROCESS [START]");
        mrs = executeVotingProcess(tNet, suggestionsMatrix, startBalances, gaussMean, aCoefs);
        System.out.println("EXECUTE VOTING PROCESS [FINISH]");
        return mrs;
    }

    private List<ModelResult> executeVotingProcess(
            final List<FloatingPoint> tNet,
            final Matrix<FloatingPoint> suggestionsMatrix,
            final DenseVector<FloatingPoint> startBalances,
            final FloatingPoint mean,
            final List<FloatingPoint> aCoefs
    ) {
        List<ModelResult> mrs = new ArrayList<>();
        for (FloatingPoint aCoef : aCoefs) {
            ExecutorService executorService = Executors.newFixedThreadPool(20);
            ModelResult mr = new ModelResult();
            List<Future<OneDotResult>> oneDotResultsFuture = tNet.stream()
                    .map(tValue -> executorService.submit(() -> calcForOneDot(tValue, startBalances, suggestionsMatrix, aCoef)))
                    .collect(Collectors.toList());
            executorService.shutdown();
            List<OneDotResult> oneDotResults = oneDotResultsFuture.stream()
                    .map(odr -> {
                        try {
                            return odr.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
            mr.setaCoef(aCoef);
            mr.setModifiedModel(IS_MODEL_MODIFIED);
            mr.setFirstGroupResult(oneDotResults.stream()
                    .collect(Collectors.toMap(OneDotResult::gettValue, OneDotResult::getResultForFirst)));
            mr.setSecondGroupResult(oneDotResults.stream()
                    .collect(Collectors.toMap(OneDotResult::gettValue, OneDotResult::getResultForSecond)));
            mr.setAllGroupsResult(oneDotResults.stream()
                    .collect(Collectors.toMap(OneDotResult::gettValue, OneDotResult::getCommonResult)));
            mr.setMean(mean);
            mrs.add(mr);
        }
        return mrs;
    }

    private OneDotResult calcForOneDot(final FloatingPoint tValue, final DenseVector<FloatingPoint> startBalances, final Matrix<FloatingPoint> suggestionsMatrix, FloatingPoint aCoef) {
        System.out.println("NOW T = " + tValue);
        int matrixRowsAmount = suggestionsMatrix.getNumberOfRows();
        DenseVector<FloatingPoint> balances = DenseVector.valueOf(startBalances);
        Vector<FloatingPoint> suggestions = null;
        Vector<FloatingPoint> differents = null;
        for (int i = 0; i < matrixRowsAmount; i++) {
            suggestions = suggestionsMatrix.getRow(i);
            differents = getDiffers(suggestions, balances, tValue, aCoef);
            balances = balances.plus(differents);
        }
        Vector<FloatingPoint> diffBalances = divide(balances.minus(startBalances), FloatingPoint.valueOf(matrixRowsAmount));
        OneDotResult odr = new OneDotResult();
        odr.settValue(tValue);
        odr.setResultForFirst(calculateVectorAvg(getSuggestionsVector(diffBalances, 0, this.firstGroupPeopleAmount)));
        odr.setResultForSecond(calculateVectorAvg(getSuggestionsVector(diffBalances, this.firstGroupPeopleAmount, this.firstGroupPeopleAmount + this.secondGroupPeopleAmount)));
        odr.setCommonResult(calculateVectorAvg(getSuggestionsVector(diffBalances, 0, this.firstGroupPeopleAmount + this.secondGroupPeopleAmount)));
        return odr;
    }

    private Vector<FloatingPoint> divide(Vector<FloatingPoint> balances, FloatingPoint value) {
        List<FloatingPoint> results = new ArrayList<>();
        for (int i = 0; i < balances.getDimension(); i++) {
            results.add(balances.get(i).divide(value));
        }
        return DenseVector.valueOf(results);
    }

    private Vector<FloatingPoint> getDiffers(Vector<FloatingPoint> inputSuggestions, DenseVector<FloatingPoint> balances, FloatingPoint tValue, FloatingPoint aCoef) {
        Vector<FloatingPoint> suggestions = getSuggestions(inputSuggestions, balances, aCoef, IS_MODEL_MODIFIED);
        DenseVector<FloatingPoint> firstGroupSuggestions = getSuggestionsVector(suggestions, 0, this.firstGroupPeopleAmount);
        DenseVector<FloatingPoint> secondGroupSuggestions = getSuggestionsVector(suggestions, this.firstGroupPeopleAmount, this.firstGroupPeopleAmount + this.secondGroupPeopleAmount);
        if (isSuggestionApplyed(firstGroupSuggestions, tValue) && isSuggestionApplyed(secondGroupSuggestions, tValue)) {
            return suggestions;
        }
        return DenseVector.valueOf(getZeroPeopleBalancesList(suggestions.getDimension()));
    }

    private Vector<FloatingPoint> getSuggestions(Vector<FloatingPoint> inputSuggestions, DenseVector<FloatingPoint> balances, FloatingPoint aCoef, boolean isModifiedModel) {
        if (isModifiedModel) {
            return applyBalanceFunction(inputSuggestions, balances, aCoef);
        }
        return inputSuggestions;
    }

    private Vector<FloatingPoint> applyBalanceFunction(Vector<FloatingPoint> inputSuggestions, DenseVector<FloatingPoint> balances, FloatingPoint aCoef) {
        int suggestionSize = inputSuggestions.getDimension();
        List<FloatingPoint> suggestionsList = new ArrayList<>();
        for (int i = 0; i < suggestionSize; i++) {
            suggestionsList.add(balanceFunction(inputSuggestions.get(i), balances.get(i), aCoef));
        }
        return DenseVector.valueOf(suggestionsList);
    }

    private FloatingPoint balanceFunction(FloatingPoint suggestion, FloatingPoint balance, FloatingPoint aCoef) {
        FloatingPoint ln = MathUtils.ln(balance);
        FloatingPoint result = ln.times(aCoef).plus(FloatingPoint.ONE).times(suggestion);
        return result;
    }

    private FloatingPoint calculateVectorSum(DenseVector<FloatingPoint> vector) {
        FloatingPoint vectorSum = FloatingPoint.valueOf(0);
        int vectorSize = vector.getDimension();
        for (int i = 0; i < vectorSize; i++) {
            FloatingPoint val = vector.get(i);
            vectorSum = vectorSum.plus(val);
        }
        return vectorSum;
    }

    private FloatingPoint calculateVectorAvg(DenseVector<FloatingPoint> vector) {
        int vectorSize = vector.getDimension();
        FloatingPoint vectorSum = calculateVectorSum(vector);
        FloatingPoint result = vectorSum.divide(FloatingPoint.valueOf(vectorSize));
        return result;
    }

    private DenseVector<FloatingPoint> getSuggestionsVector(Vector<FloatingPoint> vector, int startIndex, int endIndex) {
        List<FloatingPoint> list = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            list.add(vector.get(i));
        }
        return DenseVector.valueOf(list);
    }

    private boolean isSuggestionApplyed(DenseVector<FloatingPoint> groupSuggestions, FloatingPoint tValue) {
        FloatingPoint sum = calculateVectorSum(groupSuggestions);
        return sum.compareTo(tValue) >= 0;
    }

    private List<FloatingPoint> generateNet(FloatingPoint startValue, FloatingPoint endValue, int netSize) {
        List<FloatingPoint> nodesList = new ArrayList<>();
//        int n = netSize - 2 * NUMBER_OF_POINTS_AT_THE_ENDS;
        int n = netSize;
        FloatingPoint standardStep = (endValue.minus(startValue)).divide(FloatingPoint.valueOf(n));
//        FloatingPoint expendedStepLeft = startValue.plus(UNIFORMITY_BOUNDARY).times(FloatingPoint.valueOf(-1)).divide(FloatingPoint.valueOf(NUMBER_OF_POINTS_AT_THE_ENDS));
//        FloatingPoint expendedStepRight = endValue.minus(UNIFORMITY_BOUNDARY).divide(FloatingPoint.valueOf(NUMBER_OF_POINTS_AT_THE_ENDS));
//        System.out.println("standardStep = " + standardStep + " expendedStepLeft = " + expendedStepLeft + " expendedStepRight = " + expendedStepRight);
        FloatingPoint lastValue = startValue;
        nodesList.add(lastValue);
        for (int i = 0; i < netSize - 1; i++) {
            lastValue = lastValue.plus(standardStep);
            nodesList.add(lastValue);
        }
        nodesList.add(endValue);
        System.out.println("NET SIZE = " + nodesList.size());
        return nodesList;
    }

    private DenseVector<FloatingPoint> generatePeopleStartBalancesVector(int peopleAmount) {
        return DenseVector.valueOf(getZeroPeopleBalancesList(peopleAmount));
    }

    private List<FloatingPoint> getZeroPeopleBalancesList(int peopleAmount) {
        List<FloatingPoint> peopleStartBalancesList = new ArrayList<>();
        FloatingPoint FloatingPoint = START_BALANCE;
        for (int i = 0; i < peopleAmount; i++) {
            peopleStartBalancesList.add(FloatingPoint);
        }
        return peopleStartBalancesList;
    }

    private Matrix<FloatingPoint> generateSuggestionsMatrix(int peopleAmount, int suggestionsAmount, FloatingPoint mean) {
        List<DenseVector<FloatingPoint>> vectorsList = new ArrayList<>();
        for (int i = 0; i < suggestionsAmount; i++) {
            vectorsList.add(generateGaussSuggestionsVector(peopleAmount, mean));
        }
        return DenseMatrix.valueOf(vectorsList);
    }

    private DenseVector<FloatingPoint> generateGaussSuggestionsVector(int peopleAmount, FloatingPoint mean) {
        return DenseVector.valueOf(generateGaussNumbersList(peopleAmount, mean));
    }

    private List<FloatingPoint> generateGaussNumbersList(int peopleAmount, FloatingPoint mean) {
        List<FloatingPoint> randomNumbersList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < peopleAmount; i++) {
            randomNumbersList.add(FloatingPoint.valueOf(random.nextGaussian()).times(GAUSS_VARIANCE).plus(mean));
        }
        return randomNumbersList;
    }
}
