package de.voomdoon.tool.java.threaddump.analyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.voomdoon.java.threaddump.adapter.file.EmptyThreadDumpException;
import de.voomdoon.java.threaddump.adapter.file.ThreadDumpReader;
import de.voomdoon.java.threaddump.model.Snapshot;
import de.voomdoon.java.threaddump.model.ThreadDump;
import de.voomdoon.java.threaddump.model.ThreadMetadata;
import de.voomdoon.java.threaddump.model.ThreadSnapshot;
import de.voomdoon.util.csv.writer.CsvWriter;
import de.voomdoon.util.csv.writer.CsvWriterBuilder;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class ThreadDumpsToCsv {

	/**
	 * @since 0.1.0
	 */
	private static final Comparator<ThreadMetadata> ID_COMPARATOR = (m1, m2) -> m1.id().compareToIgnoreCase(m2.id());

	/**
	 * @since 0.1.0
	 */
	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * DOCME add JavaDoc for method convert
	 * 
	 * @param inputDirectory
	 * @param outputFile
	 * @throws IOException
	 * @since 0.1.0
	 */
	public void convert(Path inputDirectory, Path outputFile) throws IOException {
		List<ThreadDump> threadDumps = readThreadDumps(inputDirectory);

		CsvWriter writer = new CsvWriterBuilder(outputFile.toString()).build();
		writer.writeRow(getHeadline(threadDumps));

		writeBody(threadDumps, writer, threadDumps.stream().map(ThreadDump::time).sorted().toList());

		writer.close();
	}

	/**
	 * DOCME add JavaDoc for method convert
	 * 
	 * @param threadDumps
	 * @since 0.1.0
	 */
	private Map<ThreadMetadata, Map<LocalDateTime, Snapshot>> convert(List<ThreadDump> threadDumps) {
		Map<ThreadMetadata, Map<LocalDateTime, Snapshot>> result = new HashMap<>();

		for (ThreadDump threadDump : threadDumps) {
			for (ThreadSnapshot threadSnapshot : threadDump.threads()) {
				ThreadMetadata metadata = threadSnapshot.metadata();
				Snapshot snapshot = threadSnapshot.snapshot();
				LocalDateTime time = threadDump.time();

				Map<LocalDateTime, Snapshot> snapshots = result.computeIfAbsent(metadata, k -> new HashMap<>());
				snapshots.put(time, snapshot);
			}
		}

		return result;
	}

	/**
	 * DOCME add JavaDoc for method getHeadline
	 * 
	 * @param threadDumps
	 * @return
	 * @since 0.1.0
	 */
	private List<String> getHeadline(List<ThreadDump> threadDumps) {
		List<String> result = new ArrayList<>();
		result.add("thread_id");
		result.add("thread_name");

		for (ThreadDump threadDump : threadDumps) {
			result.add(threadDump.time().format(dateTimeFormatter));
		}

		return result;
	}

	/**
	 * DOCME add JavaDoc for method initRow
	 * 
	 * @param thread
	 * @return
	 * @since 0.1.0
	 */
	private List<String> initRow(ThreadMetadata thread) {
		List<String> result = new ArrayList<>();
		result.add(thread.id());
		result.add(thread.name());// FEATURE make configurable

		return result;
	}

	/**
	 * DOCME add JavaDoc for method readThreadDumps
	 * 
	 * @param inputDirectory
	 * @return
	 * @throws IOException
	 * @since 0.1.0
	 */
	private List<ThreadDump> readThreadDumps(Path inputDirectory) throws IOException {
		ThreadDumpReader reader = new ThreadDumpReader();
		List<ThreadDump> result = new ArrayList<>();

		for (File file : inputDirectory.toFile().listFiles()) {
			try {
				ThreadDump threadDump = reader.read(file.toPath());
				result.add(threadDump);
			} catch (EmptyThreadDumpException e) {
				// ignore
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException("Failed to read " + file + ": " + e.getMessage(), e);
			}
		}

		return result;
	}

	/**
	 * DOCME add JavaDoc for method writeBody
	 * 
	 * @param threadDumps
	 * @param writer
	 * @param times
	 * @throws IOException
	 * @since 0.1.0
	 */
	private void writeBody(List<ThreadDump> threadDumps, CsvWriter writer, List<LocalDateTime> times)
			throws IOException {
		Map<ThreadMetadata, Map<LocalDateTime, Snapshot>> data = convert(threadDumps);

		List<ThreadMetadata> threads = data.keySet().stream().sorted(ID_COMPARATOR).toList();

		for (ThreadMetadata thread : threads) {
			writeThread(thread, writer, data, times);
		}
	}

	/**
	 * DOCME add JavaDoc for method write
	 * 
	 * @param thread
	 * @param writer
	 * @param data
	 * @param times
	 * @throws IOException
	 * @since 0.1.0
	 */
	private void writeThread(ThreadMetadata thread, CsvWriter writer,
			Map<ThreadMetadata, Map<LocalDateTime, Snapshot>> data, List<LocalDateTime> times) throws IOException {
		List<String> row = initRow(thread);

		for (LocalDateTime time : times) {
			Snapshot snapshot = data.get(thread).get(time);
			row.add(snapshot == null ? null : snapshot.state().toString());
		}

		writer.writeRow(row);
	}
}
