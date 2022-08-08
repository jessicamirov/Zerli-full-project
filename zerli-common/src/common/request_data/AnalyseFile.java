package common.request_data;

import java.io.Serializable;

import common.JSONObject;

/**
 * Saves all details about the file pdf which sends to sql
 *
 * @author Jessica, Yarden
 */
public class AnalyseFile extends JSONObject implements Serializable {
	// surveyDetails-not user at this description,will need it in the future
	public String surveyDetails;
	public String fileName = null;
	public int size;
	public byte[] byteArray;
	public String surveyAnalyseId;

	public AnalyseFile() {
		surveyDetails = null;
		fileName = null;
		size = 0;
		byteArray = null;
		surveyAnalyseId = null;
	}

	public AnalyseFile(String fileName) {
		this.fileName = fileName;
		surveyDetails = null;
		size = 0;
	}

	public static AnalyseFile fromJson(String s) {
		/* Add such function to each subclass! */
		return (AnalyseFile) fromJson(s, AnalyseFile.class);
	}

}