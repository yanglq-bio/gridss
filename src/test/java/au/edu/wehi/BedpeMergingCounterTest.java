package au.edu.wehi;

import au.edu.wehi.idsv.BreakpointSummary;
import au.edu.wehi.idsv.TestHelper;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class BedpeMergingCounterTest extends TestHelper {
    @Test
    public void shouldIgnoreNominal() throws IOException {
        List<Pair<BreakpointSummary, Integer>> result = new ArrayList<>();
        BedpeMergingCounter c = new BedpeMergingCounter();
        result.addAll(c.process(new BreakpointSummary(0, FWD, 2, 1, 3, 0, BWD, 3, 3, 3)));
        result.addAll(c.process(new BreakpointSummary(0, FWD, 1, 1, 3, 0, BWD, 3, 3, 3)));
        result.addAll(c.finish());
        assertEquals(1, result.size());
        assertEquals(2, (int)result.get(0).getSecond());
    }
    @Test
    public void shouldAddContained() throws IOException {
        List<Pair<BreakpointSummary, Integer>> result = new ArrayList<>();
        BedpeMergingCounter c = new BedpeMergingCounter();
        result.addAll(c.process(new BreakpointSummary(0, FWD, 1, 1, 1, 0, BWD, 3, 3, 3)));
        result.addAll(c.process(new BreakpointSummary(0, FWD, 1, 1, 3, 0, BWD, 3, 3, 3)));
        result.addAll(c.finish());
        assertEquals(1, result.size());
        assertEquals(2, (int)result.get(0).getSecond());
    }
    @Test
    public void shouldMergeContained() throws IOException {
        List<Pair<BreakpointSummary, Integer>> result = new ArrayList<>();
        BedpeMergingCounter c = new BedpeMergingCounter();
        result.addAll(c.process(new BreakpointSummary(0, FWD, 1, 1, 3, 0, BWD, 3, 3, 3)));
        result.addAll(c.process(new BreakpointSummary(0, FWD, 3, 3, 3, 0, BWD, 3, 3, 3)));
        result.addAll(c.finish());
        assertEquals(1, result.size());
        assertEquals(2, (int)result.get(0).getSecond());
    }
    @Test
    public void shouldChainMerge() throws IOException {
        List<Pair<BreakpointSummary, Integer>> result = new ArrayList<>();
        BedpeMergingCounter c = new BedpeMergingCounter();
        result.addAll(c.process(new BreakpointSummary(0, FWD, 1, 1, 1, 0, BWD, 3, 3, 3)));
        result.addAll(c.process(new BreakpointSummary(0, FWD, 2,2,2, 0, BWD, 3, 3, 3)));
        result.addAll(c.process(new BreakpointSummary(0, FWD, 3,3,4, 0, BWD, 3, 3, 3)));
        result.addAll(c.finish());
        assertEquals(1, result.size());
        assertEquals(3, (int)result.get(0).getSecond());
    }
    @Test
    public void shouldMergeRemote() throws IOException {
        List<Pair<BreakpointSummary, Integer>> result = new ArrayList<>();
        BedpeMergingCounter c = new BedpeMergingCounter();
        result.addAll(c.process(new BreakpointSummary(0, FWD, 1, 0, BWD, 3)));
        result.addAll(c.process(new BreakpointSummary(0, FWD, 1, 0, BWD, 4)));
        result.addAll(c.process(new BreakpointSummary(0, FWD, 1, 0, BWD, 5)));
        result.addAll(c.finish());
        assertEquals(1, result.size());
        assertEquals(3, (int)result.get(0).getSecond());
    }
    @Test
    public void shouldNotMergeDifferent() throws IOException {
        List<Pair<BreakpointSummary, Integer>> result = new ArrayList<>();
        BedpeMergingCounter c = new BedpeMergingCounter();
        result.addAll(c.process(new BreakpointSummary(0, FWD, 1, 1, 3, 0, BWD, 3, 3, 3)));
        result.addAll(c.process(new BreakpointSummary(0, FWD, 1, 1, 3, 0, FWD, 3, 3, 3)));
        result.addAll(c.process(new BreakpointSummary(0, FWD, 5,5,5, 0, BWD, 3, 3, 3)));
        result.addAll(c.process(new BreakpointSummary(0, BWD, 1, 1, 3, 0, BWD, 3, 3, 3)));
        result.addAll(c.process(new BreakpointSummary(0, FWD, 1, 1, 3, 0, BWD, 5,5,5)));
        result.addAll(c.finish());
        assertEquals(5, result.size());
    }
}