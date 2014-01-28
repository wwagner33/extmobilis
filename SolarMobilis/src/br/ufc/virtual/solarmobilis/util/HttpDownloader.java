package br.ufc.virtual.solarmobilis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.StrictMode;

@SuppressLint("NewApi")
public class HttpDownloader {
	private URL url = null;

	/**
	 * Download de arquivo a partir de uma URL
	 * 
	 * @param urlStr
	 * @return text
	 */
	public String download(String urlStr) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			buffer = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream()));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * Retorno -1: Indica erro arquivo de download, 0: Arquivo baixado com
	 * sucesso 1: Arquivo já existe
	 * 
	 * @param urlStr
	 * @param path
	 * @param fileName
	 * @return
	 * @throws Exception 
	 */
	public int downFile(String urlStr, String path, String fileName)
			throws Exception {
		InputStream inputStream = null;
		try {
			FileUtils fileUtils = new FileUtils();
			if (fileUtils.isFileExist(path + fileName)) {
				return 1;
			}

			inputStream = getInputStreamFromUrl(urlStr);
			File resultFile = fileUtils.write2SDFromInput(path, fileName,
					inputStream);
			if (resultFile == null) {
				return -1;
			}
		} catch (MalformedURLException malformedURLException) {
			throw malformedURLException;
		} catch (IOException ioException) {
			throw ioException;			
		} 
		finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public InputStream getInputStreamFromUrl(String urlStr)
			throws MalformedURLException, IOException {
		url = new URL(urlStr);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		InputStream inputStream = urlConn.getInputStream();
		// System.out
		// .println("inputStream.available = " + inputStream.available());
		return inputStream;
	}

	public static void initStrictMode() {
		// System.out.println("Build.VERSION.SDK_INT = " +
		// Build.VERSION.SDK_INT);
		if (Build.VERSION.SDK_INT > 10) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork() // pode
																			// ser
																			// substituído
																			// por
																			// detectAll()
					.penaltyLog() // Imprime logcat, também pode ser enviado ao
									// Dropbox, através do arquivo de log
					.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects() // Detecta operações de banco
													// de dados SQLite
					.penaltyLog().penaltyDeath().build());
		}
	}
}