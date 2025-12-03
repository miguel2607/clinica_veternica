package com.veterinaria.clinica_veternica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;

/**
 * Configuración de base de datos para producción (Render, etc.)
 * Convierte DATABASE_URL de Render al formato JDBC si es necesario.
 */
@Configuration
@Profile("prod")
public class DatabaseConfig {

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    /**
     * Configura el DataSource, convirtiendo DATABASE_URL de Render si es necesario.
     */
    @Bean
    @Primary
    public HikariDataSource dataSource(
            @Value("${spring.datasource.username:}") String username,
            @Value("${spring.datasource.password:}") String password) {
        
        String jdbcUrl = datasourceUrl;
        String dbUsername = username;
        String dbPassword = password;

        // Si DATABASE_URL está presente (formato de Render: postgresql://user:pass@host:port/db)
        // y spring.datasource.url no está configurado o es el valor por defecto
        if ((databaseUrl != null && !databaseUrl.isEmpty()) && 
            (datasourceUrl == null || datasourceUrl.isEmpty() || 
             datasourceUrl.contains("localhost") || datasourceUrl.contains("change_this"))) {
            
            try {
                // Parsear DATABASE_URL de Render (formato: postgresql://user:pass@host:port/db)
                // Ejemplo: postgresql://user:pass@host:5432/dbname
                // Remover el prefijo postgresql:// y parsear como URI
                String urlWithoutPrefix = databaseUrl.replaceFirst("^postgresql://", "");
                
                // Manejar URLs con y sin puerto
                // Si no tiene puerto, agregar :5432 antes de parsear
                if (!urlWithoutPrefix.matches(".*@[^:]+:\\d+.*")) {
                    // No tiene puerto, agregarlo después del host
                    urlWithoutPrefix = urlWithoutPrefix.replaceFirst("@([^/]+)/", "@$1:5432/");
                }
                
                URI dbUri = new URI("http://" + urlWithoutPrefix);
                
                // Extraer usuario y contraseña
                String[] userInfo = dbUri.getUserInfo() != null ? dbUri.getUserInfo().split(":") : new String[0];
                if (userInfo.length >= 1) {
                    dbUsername = userInfo[0];
                }
                if (userInfo.length >= 2) {
                    // La contraseña puede contener caracteres especiales, unir todo después del primer :
                    dbPassword = String.join(":", java.util.Arrays.copyOfRange(userInfo, 1, userInfo.length));
                }
                
                String host = dbUri.getHost();
                int port = dbUri.getPort() == -1 ? 5432 : dbUri.getPort();
                String dbName = dbUri.getPath() != null ? dbUri.getPath().replaceFirst("/", "") : "clinica_veterinaria";
                
                // Construir JDBC URL
                jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
                
                System.out.println("✅ DATABASE_URL parseado correctamente");
                System.out.println("   JDBC URL: " + jdbcUrl.replace(dbPassword, "***"));
                System.out.println("   Username: " + dbUsername);
                System.out.println("   Host: " + host);
                System.out.println("   Port: " + port);
                System.out.println("   Database: " + dbName);
            } catch (Exception e) {
                System.err.println("⚠️ Error al parsear DATABASE_URL: " + e.getMessage());
                System.err.println("   DATABASE_URL recibido: " + (databaseUrl != null ? databaseUrl.replaceAll(":[^:@]+@", ":***@") : "null"));
                System.err.println("   Usando configuración por defecto");
                e.printStackTrace();
            }
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        return new HikariDataSource(config);
    }
}

