package org.dinky.data.model.maven;

import lombok.Data;

/**
 * {
 * "responseHeader": {
 * "status": 0,
 * "QTime": 3,
 * "params": {
 * "q": "g:org.apache.flink",
 * "core": "",
 * "indent": "off",
 * "spellcheck": "true",
 * "fl": "id,g,a,latestVersion,p,ec,repositoryId,text,timestamp,versionCount",
 * "start": "0",
 * "spellcheck.count": "5",
 * "sort": "score desc,timestamp desc,g asc,a asc",
 * "rows": "20",
 * "wt": "json",
 * "version": "2.2"
 * }
 * },
 * "response": {
 * "numFound": 690,
 * "start": 0,
 * "docs": [
 * {
 * "id": "org.apache.flink:flink-sql-connector-opensearch2",
 * "g": "org.apache.flink",
 * "a": "flink-sql-connector-opensearch2",
 * "latestVersion": "2.0.0-1.19",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715808419000,
 * "versionCount": 2,
 * "text": [
 * "org.apache.flink",
 * "flink-sql-connector-opensearch2",
 * "-sources.jar",
 * ".pom",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-connector-opensearch2",
 * "g": "org.apache.flink",
 * "a": "flink-connector-opensearch2",
 * "latestVersion": "2.0.0-1.19",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715808385000,
 * "versionCount": 2,
 * "text": [
 * "org.apache.flink",
 * "flink-connector-opensearch2",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * "-tests.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * "-tests.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-connector-opensearch-base",
 * "g": "org.apache.flink",
 * "a": "flink-connector-opensearch-base",
 * "latestVersion": "2.0.0-1.19",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715808373000,
 * "versionCount": 4,
 * "text": [
 * "org.apache.flink",
 * "flink-connector-opensearch-base",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * "-tests.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * "-tests.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-connector-opensearch-parent",
 * "g": "org.apache.flink",
 * "a": "flink-connector-opensearch-parent",
 * "latestVersion": "2.0.0-1.19",
 * "repositoryId": "central",
 * "p": "pom",
 * "timestamp": 1715808358000,
 * "versionCount": 9,
 * "text": [
 * "org.apache.flink",
 * "flink-connector-opensearch-parent",
 * ".pom"
 * ],
 * "ec": [
 * ".pom"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-sql-connector-opensearch",
 * "g": "org.apache.flink",
 * "a": "flink-sql-connector-opensearch",
 * "latestVersion": "1.2.0-1.19",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715464032000,
 * "versionCount": 7,
 * "text": [
 * "org.apache.flink",
 * "flink-sql-connector-opensearch",
 * "-sources.jar",
 * ".pom",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-connector-opensearch",
 * "g": "org.apache.flink",
 * "a": "flink-connector-opensearch",
 * "latestVersion": "1.2.0-1.19",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715463988000,
 * "versionCount": 7,
 * "text": [
 * "org.apache.flink",
 * "flink-connector-opensearch",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * "-tests.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * "-tests.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-cdc-pipeline-connector-paimon",
 * "g": "org.apache.flink",
 * "a": "flink-cdc-pipeline-connector-paimon",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715392345000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-cdc-pipeline-connector-paimon",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-cdc-pipeline-connector-kafka",
 * "g": "org.apache.flink",
 * "a": "flink-cdc-pipeline-connector-kafka",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715392297000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-cdc-pipeline-connector-kafka",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-cdc-pipeline-connector-starrocks",
 * "g": "org.apache.flink",
 * "a": "flink-cdc-pipeline-connector-starrocks",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715392273000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-cdc-pipeline-connector-starrocks",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-cdc-pipeline-connector-doris",
 * "g": "org.apache.flink",
 * "a": "flink-cdc-pipeline-connector-doris",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715392245000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-cdc-pipeline-connector-doris",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-cdc-pipeline-connector-mysql",
 * "g": "org.apache.flink",
 * "a": "flink-cdc-pipeline-connector-mysql",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715392216000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-cdc-pipeline-connector-mysql",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * "-tests.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * "-tests.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-sql-connector-vitess-cdc",
 * "g": "org.apache.flink",
 * "a": "flink-sql-connector-vitess-cdc",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715392180000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-sql-connector-vitess-cdc",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-sql-connector-tidb-cdc",
 * "g": "org.apache.flink",
 * "a": "flink-sql-connector-tidb-cdc",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715392138000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-sql-connector-tidb-cdc",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-sql-connector-sqlserver-cdc",
 * "g": "org.apache.flink",
 * "a": "flink-sql-connector-sqlserver-cdc",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715392091000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-sql-connector-sqlserver-cdc",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-sql-connector-postgres-cdc",
 * "g": "org.apache.flink",
 * "a": "flink-sql-connector-postgres-cdc",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715392064000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-sql-connector-postgres-cdc",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-sql-connector-oracle-cdc",
 * "g": "org.apache.flink",
 * "a": "flink-sql-connector-oracle-cdc",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715392035000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-sql-connector-oracle-cdc",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-sql-connector-oceanbase-cdc",
 * "g": "org.apache.flink",
 * "a": "flink-sql-connector-oceanbase-cdc",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715392007000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-sql-connector-oceanbase-cdc",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-sql-connector-mysql-cdc",
 * "g": "org.apache.flink",
 * "a": "flink-sql-connector-mysql-cdc",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715391978000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-sql-connector-mysql-cdc",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-sql-connector-mongodb-cdc",
 * "g": "org.apache.flink",
 * "a": "flink-sql-connector-mongodb-cdc",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715391945000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-sql-connector-mongodb-cdc",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * },
 * {
 * "id": "org.apache.flink:flink-sql-connector-db2-cdc",
 * "g": "org.apache.flink",
 * "a": "flink-sql-connector-db2-cdc",
 * "latestVersion": "3.1.0",
 * "repositoryId": "central",
 * "p": "jar",
 * "timestamp": 1715391915000,
 * "versionCount": 1,
 * "text": [
 * "org.apache.flink",
 * "flink-sql-connector-db2-cdc",
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ],
 * "ec": [
 * "-sources.jar",
 * ".pom",
 * "-javadoc.jar",
 * ".jar"
 * ]
 * }
 * ]
 * },
 * "spellcheck": {
 * "suggestions": [
 * <p>
 * ]
 * }
 * }
 */
@Data
public class MavenResult {

    private MavenResponseHeader responseHeader;

    private MavenResponseBody response;


    /**
     * {
     * "status": 0,
     * "QTime": 3,
     * "params": {
     * "q": "g:org.apache.flink",
     * "core": "",
     * "indent": "off",
     * "spellcheck": "true",
     * "fl": "id,g,a,latestVersion,p,ec,repositoryId,text,timestamp,versionCount",
     * "start": "0",
     * "spellcheck.count": "5",
     * "sort": "score desc,timestamp desc,g asc,a asc",
     * "rows": "20",
     * "wt": "json",
     * "version": "2.2"
     * }
     */


}

