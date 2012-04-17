package com.mobilis.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import com.mobilis.controller.Constants;

public class ZipManager {

	private void createDir(File dir) {
		if (dir.exists()) {
			return;
		}
		if (!dir.mkdirs()) {
			throw new RuntimeException("Cannot create dir " + dir);
		}
	}

	private void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir)
			throws IOException {

		if (entry.isDirectory()) {
			createDir(new File(outputDir, entry.getName()));
			return;
		}

		File outputFile = new File(outputDir, entry.getName());
		if (!outputFile.getParentFile().exists()) {
			createDir(outputFile.getParentFile());
		}

		BufferedInputStream inputStream = new BufferedInputStream(
				zipfile.getInputStream(entry));
		BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(outputFile));

		try {
			IOUtils.copy(inputStream, outputStream);
		} finally {
			outputStream.close();
			inputStream.close();
		}

	}

	@SuppressWarnings("rawtypes")
	public void unzipFile() {

		String destinationPath = Constants.PATH_IMAGES;
		File ImagesFolder = new File(destinationPath);
		if (!ImagesFolder.exists()) {

			ImagesFolder.mkdirs();
		}

		File file = new File(Constants.PATH_IMAGESZIP);
		try {
			ZipFile zipFile = new ZipFile(file);

			for (Enumeration e = zipFile.entries(); e.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				unzipEntry(zipFile, entry, destinationPath);

			}

			file.delete();

		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
