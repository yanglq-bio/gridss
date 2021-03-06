package gridss.analysis;

import au.edu.wehi.idsv.IntermediateFilesTest;
import com.google.common.collect.ImmutableList;
import htsjdk.samtools.SAMFileHeader.SortOrder;
import org.junit.Test;
import picard.analysis.SinglePassSamProgram;
import picard.cmdline.argumentcollections.RequiredOutputArgumentCollection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReportThresholdCoverageTest extends IntermediateFilesTest {
	@Test
	public void should_write_output_file() throws IOException {
		createBAM(input, SortOrder.coordinate, Read(0, 2, "2M"));
		output = new File(testFolder.getRoot(), "coverage.bed");
		ReportThresholdCoverage rtc = new ReportThresholdCoverage();
		rtc.THRESHOLD_COVERAGE = 1;
		rtc.INPUT = input;
		rtc.output = new RequiredOutputArgumentCollection(output);
		SinglePassSamProgram.makeItSo(rtc.INPUT, null, true, 0, ImmutableList.of(rtc));
		assertTrue(rtc.output.getOutputFile().isFile());
	}
	@Test
	public void should_report_in_minimal_bed_format_with_header() throws IOException {
		createBAM(input, SortOrder.coordinate, Read(0, 2, "2M"));
		output = new File(testFolder.getRoot(), "coverage.bed");
		ReportThresholdCoverage rtc = new ReportThresholdCoverage();
		rtc.THRESHOLD_COVERAGE = 1;
		rtc.INPUT = input;
		rtc.output = new RequiredOutputArgumentCollection(output);
		SinglePassSamProgram.makeItSo(rtc.INPUT, null, true, 0, ImmutableList.of(rtc));
		List<String> lines = Files.readAllLines(output.toPath());
		String[] split = lines.get(1).split("\t");
		
		assertEquals(3, split.length);
		// 01234567890 0-based
		// 1234567890 1-based
		//  MM
		assertEquals("polyA", split[0]);
		assertEquals(1, Integer.parseInt(split[1]));
		assertEquals(3, Integer.parseInt(split[2]));
	}
}
