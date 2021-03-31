package id.co.learn.ib.constants;

import java.util.HashMap;
import java.util.Map;

public class IBConstants {

	public static final String RDS_USER_LOGIN = "IBUSERLOGIN";
	public static final String RDS_USER_MENU = "IBUSERMENU";
	public static final String RDS_FORGOT_SESSID = "IBFORGOTSESSID";
	public static final String ERR_UNAUTHORIZED_ACCESS = "Oops, unauthorized access";
	public static final String ERR_FORBIDDEN_ACCESS = "Oops, forbidden access";
	public static final String LANG_EN = "EN";
	public static final String LANG_ID = "ID";

	public enum APP_STATUS {
		ACTIVE(1, "ACTIVE"),
		INACTIVE(0, "INACTIVE");
		private final Integer value;
		private final String msg;
		private static Map map = new HashMap<>();

		APP_STATUS(Integer value, String msg) {
			this.value = value;
			this.msg = msg;
		}
		static {
			for (APP_STATUS pageType : APP_STATUS.values()) {
				map.put(pageType.value, pageType);
			}
		}
		public static APP_STATUS valueOf(int appStatus) {
			return (APP_STATUS) map.get(appStatus);
		}
		public Integer getValue() {
			return value;
		}
		public String getMsg() {
			return msg;
		}
	}

}
