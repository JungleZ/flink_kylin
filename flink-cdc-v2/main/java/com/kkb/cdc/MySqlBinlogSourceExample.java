package com.kkb.cdc;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
public class MySqlBinlogSourceExample {
    public static void main(String[] args)  {
        try {
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("175.178.61.85")
                .port(3306)
                .databaseList("inventory") // set captured database
                .tableList("inventory.products") // set captured table
                .username("root")
                .password("123456")
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // enable checkpoint
        env.enableCheckpointing(3000);

        env.
                fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
                // set 4 parallel source tasks
                .setParallelism(2)
                .print().setParallelism(1); // use parallelism 1 for sink to keep message ordering


            env.execute("Print MySQL Snapshot + Binlog");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
