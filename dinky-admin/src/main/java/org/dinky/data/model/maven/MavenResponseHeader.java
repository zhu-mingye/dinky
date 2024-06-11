package org.dinky.data.model.maven;

import java.util.Map;
import lombok.Data;

@Data
public class MavenResponseHeader {
    private int status;
    private String QTime;
    private Map<String, String> params;
}
