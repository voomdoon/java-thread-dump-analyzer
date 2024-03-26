package de.voomdoon.tool.java.threaddump.analyzer;

import static de.voomdoon.csv.testing.CsvAssert.assertCsv;
import static de.voomdoon.csv.testing.CsvAssert.column;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import de.voomdoon.testing.tests.TestBase;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
class ThreadDumpsToCsvTest extends TestBase {

	/**
	 * DOCME add JavaDoc for method test_multiple
	 * 
	 * @since 0.1.0
	 */
	@Test
	void test_multipleThreadDumps() throws Exception {
		logTestStart();

		ThreadDumpsToCsv threadDumpsToCsv = new ThreadDumpsToCsv();

		Path outputFile = Path.of(getTempDirectory().toString(), "output.csv");
		threadDumpsToCsv.convert(Path.of("src/test/resources/1"), outputFile);

		assertCsv(outputFile.toString()).assertColumns(column("thread_id", "0x000001f93dd801b0"),
				column("2024-03-07 09:32:38", "RUNNABLE"));
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_name() throws Exception {
		logTestStart();

		ThreadDumpsToCsv threadDumpsToCsv = new ThreadDumpsToCsv();

		Path outputFile = Path.of(getTempDirectory().toString(), "output.csv");
		threadDumpsToCsv.convert(Path.of("src/test/resources/1"), outputFile);

		assertCsv(outputFile.toString()).assertColumns(column("thread_id", "0x000001f93dd801b0"),
				column("thread_name", "main"));
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_state() throws Exception {
		logTestStart();

		ThreadDumpsToCsv threadDumpsToCsv = new ThreadDumpsToCsv();

		Path outputFile = Path.of(getTempDirectory().toString(), "output.csv");
		threadDumpsToCsv.convert(Path.of("src/test/resources/2"), outputFile);

		assertCsv(outputFile.toString()).assertColumns(column("thread_id", "0x000001f93dd801b0"),
				column("2024-03-07 12:00:01", "RUNNABLE"), column("2024-03-07 12:00:02", "RUNNABLE"));
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_withEmptyFile() throws Exception {
		logTestStart();

		ThreadDumpsToCsv threadDumpsToCsv = new ThreadDumpsToCsv();

		Path outputFile = Path.of(getTempDirectory().toString(), "output.csv");
		threadDumpsToCsv.convert(Path.of("src/test/resources/withEmptyFile"), outputFile);

		assertCsv(outputFile.toString()).assertColumns(column("thread_id", "0x000001f93dd801b0"),
				column("thread_name", "main"));
	}
}
