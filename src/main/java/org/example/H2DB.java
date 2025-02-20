package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class H2DB {
    private static final String DB_URL = "jdbc:h2:./cache_db;MODE=MYSQL;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    public H2DB() {
        createTableIfNotExist();
    }

    private void createTableIfNotExist() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS cached_result (" +
                    "method VARCHAR(50)," +
                    "arguments INT," +
                    "result VARCHAR(500)," +
                    "PRIMARY KEY (method, arguments)" +
                    ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getCachedResult(String method, int arguments) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            PreparedStatement preparedStm = conn.prepareStatement("SELECT result" +
                    " FROM cached_result WHERE method = ? AND arguments = ?");
            preparedStm.setString(1, method);
            preparedStm.setInt(2, arguments);
            ResultSet rs = preparedStm.executeQuery();

            if (rs.next()) {
                String cachedResult = rs.getString("result");
                return parseResult(cachedResult);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void cacheResault(String method, int arguments, List<Integer> result) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            PreparedStatement preparedStm = conn.prepareStatement("INSERT INTO cached_result" +
                    "(method, arguments, result) VALUES (?, ?, ?)");
            preparedStm.setString(1, method);
            preparedStm.setInt(2,arguments);
            preparedStm.setString(3, serializeResult(result));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String serializeResult(List<Integer> list) {
        if (list.isEmpty()) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        for (Integer num : list) {
            sb.append(num).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private List<Integer> parseResult(String serialized) {
        String[] split = serialized.split(",");
        List<Integer> result = new ArrayList<>();
        for (String part : split) {
            result.add(Integer.parseInt(part));
        }
        return result;
    }
}