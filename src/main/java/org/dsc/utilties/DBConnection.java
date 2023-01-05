package org.dsc.utilties;

import org.dsc.dbProviders.postGres;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DBConnection {
    private String dBType;
    private final String[] supportedDbs = {"Postgres", "SQL Server", "Oracle"};
    private String jDBCUrl;
    private postGres pg;
    Map<String, String> jDBCUrls = Stream.of(new String[][]{
            {"Postgres", "jdbc:postgresql://localhost:5432/MetaData"},
            {"SQL Server", "jdbc:sqlserver://host:port;databaseName=xxxx"},
            {"Oracle", "jdbc:oracle:thin:@host:port:xxxx"},
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    public String jDBCUrlformat(String db) {
        return jDBCUrls.get(db);
    }

    public String getJDBCUrl() {
        return jDBCUrl;
    }

    public void setJDBCUrl(String jDBCUrl) {
        this.jDBCUrl = jDBCUrl;
    }

    public String getDbType() {
        return dBType;
    }

    public void setDbType(String dBType) {
        if (inSupportedDbs(dBType)) {
            this.dBType = dBType;
        }
    }

    private boolean inSupportedDbs(String dBType) {
        return Arrays.asList(supportedDbs).contains(dBType);
    }

    public void setDataProviderUrl() {
        pg.setUrl(jDBCUrl);
    }

    public void setDBConnection() throws SQLException {
        pg = new postGres();
        this.setDataProviderUrl();
        pg.setCon();
    }


    public void runStatement(String name, String key, String value) throws SQLException {
        pg.runStatement(name, key, value);
    }
    public void commit() throws SQLException {
        pg.commit();
    }

    public void close() throws SQLException {
        pg.pgCloseConection();
    }
}
