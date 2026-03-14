package com.pingboard.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RenderPostgresDataSourceConfigTest {

    @Test
    void parsesRenderStylePostgresUrlIntoJdbcUrlAndCredentials() {
        RenderPostgresDataSourceConfig.ParsedPostgresUrl parsed =
                RenderPostgresDataSourceConfig.ParsedPostgresUrl.parse(
                        "postgresql://pingboard:secret@dpg-abc123-a/pingboard");

        assertThat(parsed.jdbcUrl()).isEqualTo("jdbc:postgresql://dpg-abc123-a:5432/pingboard");
        assertThat(parsed.username()).isEqualTo("pingboard");
        assertThat(parsed.password()).isEqualTo("secret");
    }
}
