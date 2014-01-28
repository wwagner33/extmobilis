package br.ufc.virtual.solarmobilis.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileUtils {
	private String SDPATH;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils() {
		SDPATH = Environment.getExternalStorageDirectory() + File.separator;
	}

	/**
	 * Cria um arquivo no cartão SD
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * Cria um diretório no cartão SD
	 * 
	 * @param dirName
	 * @return
	 * @throws IOException
	 */
	public File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		dir.mkdirs();
		return dir;
	}

	/**
	 * Verifica se existe um arquivo no cartão SD
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 * Grava dados do InputStream no cartão SD
	 * 
	 * @throws Exception
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) throws Exception {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[4 * 1024];
			int readsize = 0;
			while ((readsize = input.read(buffer)) > 0) {
				output.write(buffer, 0, readsize);
			}
			output.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}