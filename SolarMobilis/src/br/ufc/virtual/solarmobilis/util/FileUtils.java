package br.ufc.virtual.solarmobilis.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileUtils {
	private String basePath;

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public FileUtils() {
		basePath = Environment.getExternalStorageDirectory() + File.separator;
	}

	/**
	 * Cria um arquivo
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createFile(String fileName) throws IOException {
		File file = new File(basePath + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * Cria um diretório
	 * 
	 * @param dirName
	 * @return
	 * @throws IOException
	 */
	public File createDir(String dirName) throws IOException {
		File dir = new File(basePath + dirName);
		dir.mkdirs();
		return dir;
	}

	/**
	 * Verifica se existe um arquivo
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(basePath + fileName);
		return file.exists();
	}

	/**
	 * Grava dados do InputStream
	 * 
	 * @throws Exception
	 */
	public File writeFromInput(String path, String fileName, InputStream input)
			throws Exception {
		File file = null;
		OutputStream output = null;
		try {
			createDir(path);
			file = createFile(path + fileName);
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