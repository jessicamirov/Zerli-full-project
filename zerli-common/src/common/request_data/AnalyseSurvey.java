package common.request_data;

import common.JSONObject;

/**
 * Saves all details about the Analyse survey
 *
 * @author Jessica, Yarden
 */
public class AnalyseSurvey extends JSONObject {

	public String syrveyType;
	public String shopName;
	public String date;

	public static AnalyseSurvey fromJson(String s) {
		/* Add such function to each subclass! */
		return (AnalyseSurvey) fromJson(s, AnalyseSurvey.class);
	}
}