package de.sopro.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class FormatTest {

    @Test
    void addVersion() throws Exception{
        Format format = new Format();
        FormatVersion version = format.addVersion("version1");
        assertThat(version.getFormat()).isEqualTo(format);
        assertThat(format.getVersions()).contains(version);
    }

    @Test
    void addVersionTwice() throws Exception{
        Format format = new Format();
        FormatVersion fv = format.addVersion("version1");
        assertThat(format.addVersion("version1")).isEqualTo(fv);
    }
}