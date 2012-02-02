package com.lazerycode.jmeter;

import com.lazerycode.jmeter.properties.PropertyFileMerger;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class PropertyFileMergerTest {

    private URL testFile = this.getClass().getResource("/jmeter.properties");

    @Test
    public void validMergeProperties() throws Exception {
        HashMap<String, String> customProperties = new HashMap<String, String>();
        customProperties.put("log_level.jmeter.control", "INFO");
        customProperties.put("log_level.jmeter", "DEBUG");

        Properties propertiesFile = new Properties();
        propertiesFile.load(new FileInputStream(new File(this.testFile.toURI())));

        PropertyFileMerger merger = new PropertyFileMerger(propertiesFile);
        Properties modifiedProperties = merger.mergeProperties(customProperties);

        assertEquals("property was not overwritten", "DEBUG", modifiedProperties.get("log_level.jmeter"));
        assertEquals("property was not added", "INFO", modifiedProperties.get("log_level.jmeter.control"));
        assertEquals("property should not differ from file entry", "DEBUG", modifiedProperties.get("log_level.jmeter.junit"));
    }

    @Test
    public void attemptToMergeReservedProperty() throws Exception {
        HashMap<String, String> customProperties = new HashMap<String, String>();
        customProperties.put("user.dir", "/home/foo");

        Properties propertiesFile = new Properties();
        propertiesFile.load(new FileInputStream(new File(this.testFile.toURI())));

        PropertyFileMerger merger = new PropertyFileMerger(propertiesFile);
        Properties modifiedProperties = merger.mergeProperties(customProperties);

        assertThat(modifiedProperties.containsKey("user.dir"),
                is(equalTo(false)));
        assertThat(modifiedProperties.size(),
                is(equalTo(2)));
    }

    @Test
    public void showWarningWithSimilarProperty() throws Exception {
        HashMap<String, String> customProperties = new HashMap<String, String>();
        customProperties.put("log_level.Jmeter.control", "INFO");

        Properties propertiesFile = new Properties();
        propertiesFile.load(new FileInputStream(new File(this.testFile.toURI())));

        PropertyFileMerger merger = new PropertyFileMerger(propertiesFile);
        Properties modifiedProperties = merger.mergeProperties(customProperties);

        assertThat(modifiedProperties.size(),
                is(equalTo(3)));
    }

}
