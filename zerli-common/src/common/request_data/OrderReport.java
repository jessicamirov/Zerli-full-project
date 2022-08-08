package common.request_data;

import common.JSONObject;

/**
 * 
 * Json object for data or order reports
 * 
 * @author Yohan, Yarden
 */
public class OrderReport extends JSONObject {
	public Shop shop;
	public String year;
	public String month;
	public String Field_Beauty;
	public String Warm_White;
	public String Pink_Spring;
	public String Cute_Ball;
	public String High_Ground;
	public String With_Love;
	public String Happy_moments;
	public String Memories;
	public String Pink_Orchid;
	public String White_Rose;
	public String Red_Rose;
	public String TON;

	public OrderReport() {
		/*
		 * Default constructor is required for JSONObject if any other constructor
		 * defined.
		 */
	}

	public OrderReport(Shop shop, String year, String month, String Field_Beauty, String Warm_White, String Pink_Spring,
			String Cute_Ball, String High_Ground, String With_Love, String Happy_moments, String Memories,
			String Pink_Orchid, String White_Rose, String Red_Rose, String TON) {
		this.shop = shop;
		this.year = year;
		this.month = month;
		this.Field_Beauty = Field_Beauty;
		this.Warm_White = Warm_White;
		this.Pink_Spring = Pink_Spring;
		this.Cute_Ball = Cute_Ball;
		this.High_Ground = High_Ground;
		this.With_Love = With_Love;
		this.Happy_moments = Happy_moments;
		this.Memories = Memories;
		this.Pink_Orchid = Pink_Orchid;
		this.White_Rose = White_Rose;
		this.Red_Rose = Red_Rose;
		this.TON = TON;

	}

	public static OrderReport fromJson(String s) {
		/* Add such function to each subclass! */
		return (OrderReport) fromJson(s, OrderReport.class);
	}
}