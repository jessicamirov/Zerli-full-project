package common.request_data;

import common.JSONObject;

/**
 * Survey a json class helps up to save the user survey's answers,the survey
 * type and date, so we can transform it between the client and server and save
 * in sql long method descriptor
 * 
 * @author Jessica, Yarden
 */
public class Survey extends JSONObject {

	public int surveyId;
	public double q1;
	public double q2;
	public double q3;
	public double q4;
	public double q5;
	public double q6;
	public String type;
	public String shopName;
	public String date;
	public String surveyAnalyseId;

	public Survey() {
		this.type = null;
		this.date = null;
		this.shopName = null;
		this.surveyId = 0;
		this.q1 = 0;
		this.q2 = 0;
		this.q3 = 0;
		this.q4 = 0;
		this.q5 = 0;
		this.q6 = 0;
		this.surveyAnalyseId = null;
	}

	public Survey(double question1, double question2, double question3, double question4, double question5,
			double question6, String type, String shopName, String date, String surveyAnalyseId) {
		this.type = type;
		this.date = date;
		this.shopName = shopName;
		this.q1 = question1;
		this.q2 = question2;
		this.q3 = question3;
		this.q4 = question4;
		this.q5 = question5;
		this.q6 = question6;
		this.surveyAnalyseId = surveyAnalyseId;
	}

	public String getSurveyAnalyseId() {
		return surveyAnalyseId;
	}

	public void setSurveyAnalyseId(String surveyAnalyseId) {
		this.surveyAnalyseId = surveyAnalyseId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}

	public double getQuestion1() {
		return q1;
	}

	public void setQuestion1(int question1) {
		this.q1 = question1;
	}

	public double getQuestion2() {
		return q2;
	}

	public void setQuestion2(int question2) {
		this.q2 = question2;
	}

	public double getQuestion3() {
		return q3;
	}

	public void setQuestion3(int question3) {
		this.q3 = question3;
	}

	public double getQuestion4() {
		return q4;
	}

	public void setQuestion4(int question4) {
		this.q4 = question4;
	}

	public double getQuestion5() {
		return q5;
	}

	public void setQuestion5(int question5) {
		this.q5 = question5;
	}

	public double getQuestion6() {
		return q6;
	}

	public void setQuestion6(int question6) {
		this.q6 = question6;
	}

	public static Survey fromJson(String s) {
		/* Add such function to each subclass! */
		return (Survey) fromJson(s, Survey.class);
	}
}