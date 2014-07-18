package au.edu.wehi.idsv;

import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.Log;
import htsjdk.samtools.util.SequenceUtil;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class DirectedEvidenceFileIterator implements CloseableIterator<DirectedEvidence> {
	private final SamReader svReader;
	private final SamReader mateReader;
	private final SamReader realignReader;
	private final VCFFileReader vcfReader;
	private final SAMRecordIterator svIt;
	private final SAMRecordIterator mateIt;
	private final SAMRecordIterator realignIt;
	private final CloseableIterator<VariantContext> vcfIt;
	private final DirectedEvidenceIterator it;
	private final Log log = Log.getInstance(DirectedEvidenceFileIterator.class);
	public DirectedEvidenceFileIterator(
			ProcessingContext processContext,
			EvidenceSource source,
			File sv,
			File mate,
			File realign,
			File vcf) {
		log.debug(String.format("Loading evidence from: sv:%s, mate:%s, realign:%s, assembly:%s", sv, mate, realign, vcf));
		svReader = sv == null ? null : processContext.getSamReaderFactory().open(sv);
		mateReader = mate == null ? null : processContext.getSamReaderFactory().open(mate);
		realignReader = realign == null ? null : processContext.getSamReaderFactory().open(realign);
		if (realignReader != null) {
			SequenceUtil.assertSequenceDictionariesEqual(
					realignReader.getFileHeader().getSequenceDictionary(),
					processContext.getReference().getSequenceDictionary(),
					realign,
					processContext.getReferenceFile());
		}
		vcfReader = vcf == null ? null : new VCFFileReader(vcf);
		svIt = svReader == null ? null : svReader.iterator();
		mateIt = mateReader == null ? null : mateReader.iterator();
		realignIt = realignReader == null ? null : realignReader.iterator();
		vcfIt = vcfReader == null ? null : vcfReader.iterator();
		it = new DirectedEvidenceIterator(
				processContext,
				source,
				svIt,
				mateIt,
				realignIt,
				vcfIt);
	}
	@Override
	public void close() {
		close(svIt, mateIt, realignIt, vcfIt);
		close(svReader, mateReader, realignReader, vcfReader);
	}
	private void close(Closeable... toClose) {
		for (Closeable c : toClose) {
			if (c != null) {
				try {
					c.close();
				} catch (IOException e) {
					// log and swallow
					log.warn(e);
				}
			}
		}
	}
	@Override
	public boolean hasNext() {
		boolean result = it.hasNext();
		if (!result) {
			close();
		}
		return result;
	}
	@Override
	public DirectedEvidence next() {
		return it.next();
	}
	@Deprecated
	@Override
	public void remove() {
		it.remove();
	}
}
