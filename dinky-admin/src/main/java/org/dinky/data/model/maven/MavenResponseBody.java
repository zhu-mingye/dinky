package org.dinky.data.model.maven;

import java.util.List;
import lombok.Data;

@Data
public class MavenResponseBody {
    private int numFound;
    private int start;
    private List<MavenDoc> docs;
}
