package com.coldlion.mobilenew.type;

/**
 * Description:  constant values definition.
 * ===========================
 * Author：           Bill Wang
 * Create Date：  2014/11/25
 */
public interface ConstValue {

    public final static String APP_TAG = "coldLionDemo";

    //webservice define.
    public final static String BASE_SITE = "http://ll01.loglogisticsllc.com/coMetricsDevPortal";
    public final static String WDSL = BASE_SITE + "/" + "test.asmx";
    public final static String WEBAPI_LOGIN = "LN2";
    public final static String WEBAPI_FC = "FC";
    public final static String WEBAPI_CHANGE_PASSWORD = "CP";
    public final static String WEBAPI_LOGOFF = "LF";
    public final static String WEBAPI_FP = "FP";

    //params.
    public final static String KEY_INIT_CONTEXT = "pcInitContext";
    public final static String KEY_SESSION_ID = "OutSessionId";
    public final static String PC_USER_ID = "pcUserId";
    public final static String PC_PASSWORD = "pcPassword";
    public final static String PC_SESSION_ID = "pcSessionId";
    public final static String PC_INSTANCE_ID = "pcInstanceId";
    public final static String PC_CONTROL_VALUES = "pcControlValues";
    public final static String PC_CONTROL_ID = "pcControlId";
    public final static String PC_SUB_CONTROL_ID = "pcSubControlId";
    public final static String PC_FOCUS_CONTROL_ID = "FocusControlId";
    public final static String PC_EVENT_TYPE = "pcEventType";
    public final static String PC_DIALOG_RESULT = "pcDialogResult";

    public final static String PC_OLD_PASSWORD = "pcOldPassword";
    public final static String PC_NEW_PASSWORD = "pcNewPassword";
    public final static String PC_FORM_ID = "pcFormId";
    public final static String PI_WIDTH = "piWidth";
    public final static String PI_HEIGHT = "piHeight";

    //out.
    public final static String PL_SESSION_TIMEOUT = "plSessionTimeout";


    //main menu item.
    public final static String MENU_ITEM_TITLE = "item title";
    public final static String MENU_ITEM_ICON = "item icon";
    public final static String MENU_ITEM_COMMAND = "item command";


    //preference setting.
    public final static String PREF_FILENAME = "colddemo.pref";
    public final static String KEY_USER = "User";
    public final static String KEY_PASSWORD = "Password";
    public final static String KEY_CONN_BEAN_ID = "connectBeanID";

    public final float INPUT_WIDTH = 260.0f; // base on 5 inch screen.
    public final int TB_DEFAULT_SIZE =  24;

    //const
    public static String SC_RECORD_ID = "SC_RecordId";

}
