package com.reza.dev.learn.main;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.WriteConfig;
import com.reza.dev.learn.config.MongoConfig;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Main_Read {
    public static void main(String[] args) {

        // Instantiate config
        MongoConfig mongoConfig = new MongoConfig(args[0]);

        // Instantiate Spark
        SparkSession sparkSession = SparkSession.builder()
                .appName("WriteMain")
                .config("spark.mongodb.output.uri", mongoConfig.mongoURI())
                .getOrCreate();
        JavaSparkContext sparkContext = new JavaSparkContext(sparkSession.sparkContext());

        // Set WriteConfig which specifies various write configuration settings
        Map<String, String> writeOverrides = new HashMap<String, String>();
        writeOverrides.put("replaceDocument", "true");
        writeOverrides.put("writeConcern.w", "0");

        WriteConfig writeConfig = WriteConfig.create(sparkSession).withOptions(writeOverrides);

        // Spark transformation
        JavaRDD<String> jsonStringRDD = sparkContext
                .textFile("file://" + args[1])
                .map(stringJSON -> {
                    JSONObject jsonObject = new JSONObject(stringJSON);

                    String _id = jsonObject.getString("country") + "-" + jsonObject.getString("city");
                    jsonObject.put("_id", _id.toLowerCase().replace(" ", "_"));

                    Double latitude = Double.parseDouble(jsonObject.getString("latitude"));
                    jsonObject.remove("latitude");
                    jsonObject.put("latitude", latitude);

                    Double longitude = Double.parseDouble(jsonObject.getString("longitude"));
                    jsonObject.remove("longitude");
                    jsonObject.put("longitude", longitude);

                    return jsonObject.toString();
                });

        // Create Dataset from RDD to write
        Dataset<Row> mongoDataset = sparkSession.read().json(jsonStringRDD);

        // Save to MongoDB
        MongoSpark.save(mongoDataset, writeConfig);
    }
}