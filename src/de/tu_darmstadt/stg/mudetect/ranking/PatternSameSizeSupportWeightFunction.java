package de.tu_darmstadt.stg.mudetect.ranking;

import de.tu_darmstadt.stg.mudetect.mining.Model;
import de.tu_darmstadt.stg.mudetect.model.Overlap;
import de.tu_darmstadt.stg.mudetect.model.Overlaps;
import de.tu_darmstadt.stg.mudetect.mining.Pattern;

public class PatternSameSizeSupportWeightFunction implements ViolationWeightFunction {
    @Override
    public float getWeight(Overlap violation, Overlaps overlaps, Model model) {
        Pattern pattern = violation.getPattern();
        return pattern.getSupport() / (float) model.getMaxPatternSupport(pattern.getNodeSize());
    }

    @Override
    public String getFormula(Overlap violation, Overlaps overlaps, Model model) {
        Pattern pattern = violation.getPattern();
        return String.format("pattern support = %d / %d", pattern.getSupport(), model.getMaxPatternSupport(pattern.getNodeSize()));
    }
}
