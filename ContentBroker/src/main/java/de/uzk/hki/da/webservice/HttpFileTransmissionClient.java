/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uzk.hki.da.webservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Does a HTTP transmission (POST) to an remote address.
 * Expects the converted file as response. 
 * 
 * TODO: this should be a JSON based client instead of just doing HTTP Posts and read 
 * the responses. 
 * TODO: There might be some issues with large files (timeouts). 
 * @author Jens Peters
 */
public class HttpFileTransmissionClient {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(HttpFileTransmissionClient.class);

	/** The url. */
	private String url = "";
	
	/** The source mime type. */
	private String sourceMimeType = "";
	
	/** The dest mime type. */
	private String destMimeType = "";

	/**
	 * Post file.
	 *
	 * @param file the file
	 * @param toFile the to file
	 */
	public File postFileAndReadResponse(File file, File toFile) {
		HttpClient httpclient = null;;
		try {
			if (!file.exists()) {
				throw new RuntimeException("Source File does not exist "
						+ file.getAbsolutePath());
			}
			if (url.isEmpty()) {
				throw new RuntimeException(
						"Webservice called but Url is empty");
			}

			httpclient = new DefaultHttpClient();
			logger.info("starting new http client for url " + url);
			HttpPost httppost = new HttpPost(url);
			HttpParams httpRequestParameters = httppost.getParams();
			httpRequestParameters.setParameter(ClientPNames.HANDLE_REDIRECTS,
					true);
			httppost.setParams(httpRequestParameters);

			MultipartEntity multiPartEntity = new MultipartEntity();
			multiPartEntity.addPart("fileDescription", new StringBody(
					"doxc Converison"));
			multiPartEntity.addPart("fileName", new StringBody(file.getName()));
			if (sourceMimeType.isEmpty())
				sourceMimeType = "application/octet-stream";
			if (destMimeType.isEmpty())
				destMimeType = "application/octet-stream";
			FileBody fileBody = new FileBody(file, sourceMimeType);
			multiPartEntity.addPart("attachment", fileBody);

			httppost.setEntity(multiPartEntity);

			logger.debug("calling webservice now. recieving response");
			HttpResponse response = httpclient.execute(httppost);

			HttpEntity resEntity = response.getEntity();
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() == 200 && resEntity.getContentType().getValue().startsWith(destMimeType)) {
				InputStream in = resEntity.getContent();
				
				FileOutputStream fos = new FileOutputStream(toFile);

				byte[] buffer = new byte[4096];
				int length;
				while ((length = in.read(buffer)) > 0) {
					fos.write(buffer, 0, length);
				}
				logger.debug("successfully stored recieved content to "
						+ toFile.getAbsolutePath());
				in.close();
				fos.close();
				cleanup();	
			} else {
				logger.error("Recieved reponse of "
						+ resEntity.getContentType() + ", but expected "
						+ destMimeType);
				printResponse(resEntity);
			}
		} catch (Exception e) {
			logger.error("Exception occured in remotefileTransmission "
					+ e.getStackTrace());
			throw new RuntimeException("Webservice error " + url, e);
		} finally {
			if (httpclient!= null) httpclient.getConnectionManager().shutdown();
			return toFile;
		}
	}
	
	/**
	 * Cleanup.
	 *
	 * @throws ClientProtocolException the client protocol exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void cleanup() {
		HttpClient httpclient = null;
		try {
		httpclient = new DefaultHttpClient();
	
		HttpGet httpget = new HttpGet(url);
		HttpParams httpRequestParameters = httpget.getParams();
		httpRequestParameters.setParameter(ClientPNames.HANDLE_REDIRECTS,
				true);
		httpget.setParams(httpRequestParameters);
		java.net.URI uri = new URIBuilder(httpget.getURI()).addParameter("cleanup","1").build();
		logger.debug("calling webservice for cleanup now. recieving response");
		httpget.setURI(uri);
		
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity resEntity = response.getEntity();
		printResponse(resEntity);
		} catch (Exception e) {
			logger.error("Exception occured in cleanup "
					+ e.getStackTrace());
			throw new RuntimeException("Webservice error " + url, e);
		} finally {
			if (httpclient!= null) httpclient.getConnectionManager().shutdown();
		}
		}

	/**
	 * Prints the response.
	 *
	 * @param resEntity the res entity
	 */
	private void printResponse(HttpEntity resEntity) {
		try {
			InputStream in = resEntity.getContent();
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"utf-8"));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			logger.debug(sb.toString());
		} catch (Exception e) {

		}
	}

	/**
	 * Sets the url.
	 *
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the source mime type.
	 *
	 * @param mimeType the mimeType to set
	 */
	public void setSourceMimeType(String mimeType) {
		this.sourceMimeType = mimeType;
	}

	/**
	 * Gets the source mime type.
	 *
	 * @return the mimeType
	 */
	public String getSourceMimeType() {
		return sourceMimeType;
	}

	/**
	 * Gets the dest mime type.
	 *
	 * @return the dest mime type
	 */
	public String getDestMimeType() {
		return destMimeType;
	}

	/**
	 * Sets the dest mime type.
	 *
	 * @param destMimeType the new dest mime type
	 */
	public void setDestMimeType(String destMimeType) {
		this.destMimeType = destMimeType;
	}

}
