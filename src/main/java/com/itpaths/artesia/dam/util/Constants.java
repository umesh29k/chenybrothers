package com.itpaths.artesia.dam.util;

public class Constants {
    public static final String URL = "http://ot-dam-dev.cheneybrothers.com:11090/otmmapi/v5/";
    public static String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<!DOCTYPE TEAMS_ASSET_FILE PUBLIC \"-//TEAMS//DTD asset and link file//EN\" " +
            "\"Tasset.dtd\" " +
            "[" +
            "{0}" +
            "]>";
    public static String entity = "<!ENTITY {0} SYSTEM \"{1}\" NDATA {2}>";
    public static String asset = "<ASSET>" +
            "<METADATA>" +
            "<UOIS IS_EDITABLE=\"Y\"" +
            "    METADATA_STATE=\"NORMAL\"" +
            "    MODEL_ID=\"102\"" +
            "    CONTENT_STATE=\"NORMAL\"" +
            "    CONTENT_STATE_USER_ID=\"1001\"" +
            "    NAME=\"{0}\"" +
            "    IMPORT_USER_ID=\"1001\"  >" +
            "<OPENTEXT_HYBRIS_PRODUCTS PRODUCT_TAG_NAME=\"{1}\"" +
            "                        PRODUCT_ATTRIBUTE_NAME=\"{2}\"" +
            "                        PRODUCT_ID=\"{3}\"" +
            "                        SYSTEM_ID=\"{4}\" />" +
            "</UOIS>" +
            "</METADATA>" +
            "<CONTENT>" +
            "<MASTER FILE=\"{5}\"/>" +
            "</CONTENT>" +
            "</ASSET>";
    public static String body = "<TEAMS_ASSET_FILE>" +
            "<ASSETS>" +
            "{0}" +
            "</ASSETS>" +
            "</TEAMS_ASSET_FILE>";
    //header- {0}entity
    //entity- {0} fil-ref {1} file-path {3} extention-type [append entity to itself to make entities list
    //asset- {0} file-name {1} product-tag-name {2} product-attribute-name {3} product-id {4} system-id {5} file-ref [append asset to itself to make asset list]
    //assets- {0} asset-list [append appended-asset]
    //append header and assests into one
}
