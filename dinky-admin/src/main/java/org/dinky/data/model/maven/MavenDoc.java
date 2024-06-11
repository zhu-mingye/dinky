package org.dinky.data.model.maven;

import java.util.List;
import lombok.Data;

@Data
public class MavenDoc {
    private String id;
    // groupId
    private String g;
    // artifactId
    private String a;
    // version
    private String latestVersion;
    private String v;
    // repositoryId
    private String repositoryId;
    // packaging
    private String p;
    // timestamp
    private long timestamp;
    // versionCount
    private int versionCount;
    // text
    private List<String> text;
    private List<String> ec;
}
