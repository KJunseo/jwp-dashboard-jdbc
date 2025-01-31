package com.techcourse.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;

import com.techcourse.domain.User;
import com.techcourse.support.jdbc.init.DatabasePopulatorUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jdbc.datasource.DataSourceConfig;
import nextstep.jdbc.exception.DataAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserDaoTest {

    private final DataSource dataSource = DataSourceConfig.getInstance();
    private UserDao userDao;

    @BeforeEach
    void setup() {
        DatabasePopulatorUtils.execute(dataSource);

        userDao = new UserDao();
        final User user = new User("gugu", "password", "hkkang@woowahan.com");
        userDao.insert(user);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("drop table users");
        }
    }

    @Test
    void findAll() {
        final User user = new User("air", "password", "air.junseo@gmail.com");
        userDao.insert(user);

        final List<User> users = userDao.findAll();

        assertThat(users).isNotEmpty();
        assertThat(users).hasSize(2);
    }

    @Test
    void findById() {
        final User user = userDao.findById(1L);

        assertThat(user.getAccount()).isEqualTo("gugu");
    }

    @Test
    void findByAccount() {
        final String account = "gugu";
        final User user = userDao.findByAccount(account);

        assertThat(user.getAccount()).isEqualTo(account);
    }

    @Test
    void findByEmail() {
        final String email = "hkkang@woowahan.com";
        final User user = userDao.findByEmail(email);

        assertThat(user.getEmail()).isEqualTo(email);
    }

    @Test
    void insert() {
        final String account = "insert-gugu";
        final User user = new User(account, "password", "hkkang@woowahan.com");
        userDao.insert(user);

        final User actual = userDao.findById(2L);

        assertThat(actual.getAccount()).isEqualTo(account);
    }

    @Test
    void update() {
        final String newPassword = "password99";
        final User user = userDao.findById(1L);
        user.changePassword(newPassword);

        userDao.update(user);

        final User actual = userDao.findById(1L);

        assertThat(actual.getPassword()).isEqualTo(newPassword);
    }

    @Test
    @DisplayName("동일한 이름으로 가입시 가입되지 않음")
    void rollback() {
        final User user = new User("gugu", "password2", "hkkang2@woowahan.com");
        int originalSize = userDao.findAll().size();

        assertThatThrownBy(() -> userDao.insert(user))
                .isInstanceOf(DataAccessException.class);

        int afterSize = userDao.findAll().size();

        assertThat(originalSize).isEqualTo(afterSize);
    }
}
