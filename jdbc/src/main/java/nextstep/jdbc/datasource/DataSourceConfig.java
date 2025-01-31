package nextstep.jdbc.datasource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import org.h2.jdbcx.JdbcDataSource;

public class DataSourceConfig {

    private static javax.sql.DataSource INSTANCE;

    public static javax.sql.DataSource getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = createJdbcDataSource(dataSourceBuild());
        }
        return INSTANCE;
    }

    private static DataSourceProperty dataSourceBuild() {
        try {
            final Properties properties = getDataSourceProperty();
            return new DataSourceProperty.Builder()
                    .url(properties.getProperty("datasource.url"))
                    .username(properties.getProperty("datasource.username"))
                    .password(properties.getProperty("datasource.password"))
                    .build();
        } catch (IOException | URISyntaxException exception) {
            throw new RuntimeException();
        }
    }

    private static Properties getDataSourceProperty() throws URISyntaxException, IOException {
        final Properties properties = new Properties();
        final URL url = DataSourceConfig.class.getClassLoader().getResource("application.properties");
        final File file = Paths.get(url.toURI()).toFile();
        final FileReader fileReader = new FileReader(file);
        properties.load(fileReader);
        return properties;
    }

    private static JdbcDataSource createJdbcDataSource(DataSourceProperty dataSourceProperty) {
        final JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl(dataSourceProperty.getUrl());
        jdbcDataSource.setUser(dataSourceProperty.getUsername());
        jdbcDataSource.setPassword(dataSourceProperty.getPassword());
        return jdbcDataSource;
    }

    private DataSourceConfig() {}
}
