package com.esteel.common.constant;

/**
 * @version 1.0.0
 * @ClassName SysConstants.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
public abstract  class SysConstants {

    public static final String ROOT_ID = "0";

	public static final String NO = "0";

	public static final String USER_SESSION_KEY="userSessionKey";

	public static final String SESSION_MENUS = "menus";

	public static final String COOKIE_MENU_PARENTID = "menuParentId";

	public static final int PAGE_NO = 10;

	public static final String USER_ROLES_SESSION_KEY="userRolesSessionKey";
	public static final String USER_RESOURCES_SESSION_KEY="userResourcesSessionKey";
	public static final String USER_RESOURCES_FOR_PAGE="resources";
	//-1为内存cookie（负数为内存cookie）关闭浏览器,就自动清除
	public static final int COOKIES_MAX_AGE = -1;

	public static final String REQUEST_PARAM_LIST_SEPARATOR = ",";

	public static final String UPLOAD_NET_FILE_FOLDER = "/fileUpload/net/";
	public static final String UPLOAD_NET_FILE_SEPARATOR = ".";


	public static final String ROOT_URL_PREFIX = "/api";

	public static final String REDIS_KEY_TABLE_PRIMARY_ID  = "FRAMEWORK:TABLE:PRIMARY:${tableName}:${time}";
}
