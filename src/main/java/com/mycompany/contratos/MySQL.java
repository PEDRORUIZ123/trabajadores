
package com.mycompany.contratos;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
 private static final String DEFAULT_URL =
        "jdbc:mysql://127.0.0.1:3306/trabajadores"
        + "?useSSL=false"
        + "&allowPublicKeyRetrieval=true"
        + "&serverTimezone=Europe/Madrid"
        + "&characterEncoding=UTF-8";

    // ingresamos a la base de datos my sql
    private static final String DEFAULT_USER = "root"; // usuario
    private static final String DEFAULT_PASS = "1234"; // contraseña

    private static final String KEY_URL  = "DB_URL";
    private static final String KEY_USER = "DB_USER";
    private static final String KEY_PASS = "DB_PASS";

    private static final String URL  = pick(System.getProperty(KEY_URL), System.getenv(KEY_URL), DEFAULT_URL);
    private static final String USER = pick(System.getProperty(KEY_USER), System.getenv(KEY_USER), DEFAULT_USER);
    private static final String PASS = pick(System.getProperty(KEY_PASS), System.getenv(KEY_PASS), DEFAULT_PASS);
 
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DriverManager.setLoginTimeout(10);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Falta el driver JDBC (com.mysql.cj.jdbc.Driver).", e);
        }
    }

    private MySQL() {}

     public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void ping() throws SQLException {
        try (Connection cn = getConnection()) {
            if (!cn.isValid(5)) throw new SQLException("Conexión no válida (timeout).");
        }
    }

    private static String pick(String... vals) {
        for (String v : vals) if (v != null && !v.trim().isEmpty()) return v.trim();
        return null;
    }
} 
