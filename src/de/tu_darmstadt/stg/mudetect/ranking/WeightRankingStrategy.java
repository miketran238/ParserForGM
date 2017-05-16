package de.tu_darmstadt.stg.mudetect.ranking;

import de.tu_darmstadt.stg.mudetect.mining.Model;
import de.tu_darmstadt.stg.mudetect.ViolationRankingStrategy;
import de.tu_darmstadt.stg.mudetect.model.Overlap;
import de.tu_darmstadt.stg.mudetect.model.Overlaps;
import de.tu_darmstadt.stg.mudetect.model.Violation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class WeightRankingStrategy implements ViolationRankingStrategy {
    private ViolationWeightFunction weightFunction;

    public WeightRankingStrategy(ViolationWeightFunction weightFunction) {
        this.weightFunction = weightFunction;
    }

    @Override
    public List<Violation> rankViolations(Overlaps overlaps, Model model) {
        PriorityQueue<Violation> violations = new PriorityQueue<>(Comparator.reverseOrder());
        for (Overlap violation : overlaps.getViolations()) {
            float confidence = weightFunction.getWeight(violation, overlaps, model);
            String confidenceString = weightFunction.getFormula(violation, overlaps, model);
            violations.add(new Violation(violation, confidence, confidenceString));
        }
        return toList(violations);
    }

    private List<Violation> toList(PriorityQueue<Violation> violations) {
        List<Violation> result = new ArrayList<>();
        while (!violations.isEmpty()) {
            result.add(violations.poll());
        }
        return result;
    }
}
