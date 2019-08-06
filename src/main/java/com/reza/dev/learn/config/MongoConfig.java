package com.reza.dev.learn.config;

import org.json.JSONObject;

public class MongoConfig {
    private String stringJSONParam;

    /**
     * Set variable in constructor.
     * @param stringJSONParam -> String JSON parameter
     */
    public MongoConfig(String stringJSONParam) {
        this.stringJSONParam = stringJSONParam;
    }

    /**
     * Set MongoURI
     * @return String MongoURI
     */
    public String mongoURI() {
        JSONObject jsonParam = new JSONObject(stringJSONParam);

        String user = jsonParam.getString("user");
        String password = jsonParam.getString("password");
        String ip = jsonParam.getString("ip");
        String port = jsonParam.getString("port");
        String database = jsonParam.getString("database");
        String collection = jsonParam.getString("collection");

        return "mongodb://" + user + ":" + password + "@" + ip + ":" + port + "/" + database + "." + collection + "?authSource=admin";
    }
}