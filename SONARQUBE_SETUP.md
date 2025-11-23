# SonarQube Scanner Setup and Usage Guide

This document provides step-by-step instructions for running SonarQube analysis on this project.

## Prerequisites

Before running SonarQube scanner, ensure you have:

1. **Java Development Kit (JDK) 17 or higher** installed
2. **Gradle** (the project uses Gradle wrapper, so local installation is optional)
3. **SonarQube Server** running (either locally or remote)

## Configuration Files

### 1. build.gradle

The project is configured with the SonarQube Gradle plugin (version 7.0.1.6134 - latest as of October 2025):

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.4.1'
    id 'io.spring.dependency-management' version '1.1.7'
    id 'org.sonarqube' version '7.0.1.6134'
}
```

### 2. sonar-project.properties

The project configuration includes:

```properties
# SonarQube server URL
sonar.host.url=http://localhost:9000

# Project identification
sonar.projectKey=pseudo-project-for-static-analysis
sonar.projectName=Pseudo Project for Static Analysis
sonar.projectVersion=1.0.0

# Source code location
sonar.sources=src/main/java
sonar.tests=src/test/java

# Java binaries (compiled classes)
sonar.java.binaries=build/classes/java/main
sonar.java.test.binaries=build/classes/java/test

# Java source and target version
sonar.java.source=17
sonar.java.target=17

# Encoding
sonar.sourceEncoding=UTF-8
```

**Important**: The `sonar.host.url` is configured in the properties file, so you don't need to specify it on the command line when using the standalone scanner.

## SonarQube Server Setup

### Option 1: Using Docker (Recommended for Local Development)

1. **Pull and run SonarQube Community Edition:**

```bash
docker pull sonarqube:latest
docker run -d --name sonarqube -p 9000:9000 sonarqube:latest
```

2. **Access SonarQube UI:**
   - Open browser: http://localhost:9000
   - Default credentials: `admin` / `admin`
   - You'll be prompted to change the password on first login

3. **Generate authentication token:**
   - Login to SonarQube
   - Go to: User → My Account → Security
   - Generate a new token and save it securely

### Option 2: Using SonarQube Cloud

1. Sign up at https://sonarcloud.io
2. Create a new organization and project
3. Generate an authentication token from your account settings

## Running SonarQube Analysis

### Method 1: Using Standalone SonarQube Scanner (Recommended)

The standalone SonarQube Scanner is a command-line tool that directly analyzes your code without requiring build tool integration.

**Download SonarQube Scanner:**
- Download from: https://docs.sonarsource.com/sonarqube-server/analyzing-source-code/scanners/sonarscanner/
- Extract to a location (e.g., `~/temp/sonar-scanner/`)

**Run the scanner:**

Since `sonar.host.url` is configured in `sonar-project.properties`, you only need to provide the token:

```bash
# Option 1: Pass token as parameter
../../temp/sonar-scanner/bin/sonar-scanner -Dsonar.token=YOUR_TOKEN

# Option 2: Use environment variable (recommended)
export SONAR_TOKEN=YOUR_TOKEN
../../temp/sonar-scanner/bin/sonar-scanner
```

**Using absolute path:**

```bash
/Users/YOUR_USERNAME/temp/sonar-scanner/bin/sonar-scanner
```

**Advantages of standalone scanner:**
- No build tool dependency
- Faster execution (no compilation needed if already built)
- Works with any project structure
- Direct integration with SonarQube server

**Expected output:**
```
INFO  ANALYSIS SUCCESSFUL, you can find the results at: http://localhost:9000/dashboard?id=pseudo-project-for-static-analysis
INFO  Analysis total time: ~8-15 seconds
INFO  EXECUTION SUCCESS
```

### Method 2: Using Gradle with Command-line Parameters

**Basic scan (requires SonarQube server running):**

```bash
./gradlew build sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_SONARQUBE_TOKEN
```

**For SonarQube Cloud:**

```bash
./gradlew build sonar \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=YOUR_ORGANIZATION_KEY \
  -Dsonar.token=YOUR_SONARQUBE_CLOUD_TOKEN
```

### Method 2: Using Environment Variables

Set environment variables (more secure for CI/CD):

```bash
export SONAR_HOST_URL=http://localhost:9000
export SONAR_TOKEN=YOUR_SONARQUBE_TOKEN
```

Then run:

```bash
./gradlew build sonar
```

### Method 3: Adding Configuration to build.gradle

Add this to your `build.gradle` (not recommended for version control):

```gradle
sonar {
    properties {
        property "sonar.host.url", "http://localhost:9000"
        property "sonar.token", "YOUR_SONARQUBE_TOKEN"
    }
}
```

Then run:

```bash
./gradlew build sonar
```

## Step-by-Step Execution Guide

### Complete Workflow (Using Standalone Scanner - Recommended)

1. **Start SonarQube Server** (if using Docker):
   ```bash
   docker start sonarqube
   # Wait 30-60 seconds for server to fully start
   ```

2. **Verify SonarQube is running:**
   ```bash
   curl http://localhost:9000/api/system/status
   ```
   Should return: `{"status":"UP"}`

3. **Build the project** (optional but recommended):
   ```bash
   ./gradlew clean build
   ```
   This ensures `build/classes/java/main` is populated for better analysis.

4. **Run SonarQube analysis using standalone scanner:**
   ```bash
   # Set your token (do this once per terminal session)
   export SONAR_TOKEN=YOUR_TOKEN_HERE

   # Run the scanner (sonar.host.url is already configured)
   ../../temp/sonar-scanner/bin/sonar-scanner
   ```

5. **View results:**
   - Open browser: http://localhost:9000
   - Navigate to your project: "Pseudo Project for Static Analysis"
   - Review issues, code smells, vulnerabilities, and security hotspots

### Alternative Workflow (Using Gradle)

Follow steps 1-2 above, then:

3. **Build and analyze in one command:**
   ```bash
   ./gradlew clean build sonar \
     -Dsonar.host.url=http://localhost:9000 \
     -Dsonar.token=YOUR_TOKEN_HERE
   ```

4. **View results** at http://localhost:9000

## Expected Analysis Results

This project contains **240+ intentional defects** for testing purposes:

- **Security Vulnerabilities**: SQL injection, XSS, path traversal, etc.
- **Code Smells**: Dead code, duplicate code, long methods
- **Bugs**: Thread safety issues, resource leaks, null pointer risks
- **Maintainability Issues**: Hardcoded values, magic numbers

SonarQube should detect a significant number of these issues.

## Troubleshooting

### Issue: "Project key already exists"
**Solution**: Either use the existing project in SonarQube or change `sonar.projectKey` in `sonar-project.properties`

### Issue: "Unable to connect to SonarQube server"
**Solution**:
- Verify SonarQube is running: `docker ps | grep sonarqube`
- Check the host URL is correct
- Ensure no firewall blocking port 9000

### Issue: "Unauthorized - Please check token"
**Solution**:
- Verify token is valid
- Regenerate token in SonarQube UI if needed
- Check token has proper permissions

### Issue: "No classes found in build/classes/java/main"
**Solution**:
- For Gradle: Run `./gradlew build` before running `sonar` task
- For standalone scanner: Build the project first with `./gradlew build`
- Note: Standalone scanner will analyze source files even without compiled classes, but compiled classes provide more accurate analysis

### Issue: "sonar-scanner: command not found"
**Solution**:
- Use the full path to sonar-scanner: `../../temp/sonar-scanner/bin/sonar-scanner`
- Or add it to PATH: `export PATH=$PATH:$HOME/temp/sonar-scanner/bin`
- Verify installation: `../../temp/sonar-scanner/bin/sonar-scanner --version`

### Issue: "Communicating with SonarQube Cloud" instead of localhost
**Solution**: This happens when `sonar.host.url` is not set. Verify that `sonar-project.properties` contains:
```properties
sonar.host.url=http://localhost:9000
```
If it's missing, the scanner defaults to SonarCloud and will fail with HTTP 403 errors.

### Warning: "Dependencies/libraries were not provided for analysis"
**Solution**: This is expected for standalone scanner analysis. To provide dependencies:
- Add to `sonar-project.properties`:
  ```properties
  sonar.java.libraries=build/libs/**/*.jar,~/.gradle/caches/modules-2/files-2.1/**/*.jar
  ```
- Or ensure you run `./gradlew build` first to compile with dependencies

## Advanced Options

### Excluding Files from Analysis

Add to `sonar-project.properties`:

```properties
sonar.exclusions=**/generated/**,**/dto/**
sonar.test.exclusions=**/test/**
```

### Coverage Reports

To include code coverage:

1. Add JaCoCo plugin to `build.gradle`:
   ```gradle
   plugins {
       id 'jacoco'
   }

   jacoco {
       toolVersion = "0.8.11"
   }

   test {
       finalizedBy jacocoTestReport
   }

   jacocoTestReport {
       reports {
           xml.required = true
       }
   }
   ```

2. Uncomment in `sonar-project.properties`:
   ```properties
   sonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/test/jacocoTestReport.xml
   ```

3. Run:
   ```bash
   ./gradlew clean test jacocoTestReport sonar -Dsonar.token=YOUR_TOKEN
   ```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: SonarQube Analysis

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  sonarqube:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Build and analyze
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
        run: ./gradlew build sonar
```

## Useful Commands

```bash
# View available Gradle tasks
./gradlew tasks

# View SonarQube plugin version
./gradlew dependencies | grep sonarqube

# Run analysis with debug output
./gradlew sonar -Dsonar.verbose=true -Dsonar.token=YOUR_TOKEN

# Run analysis for specific branch
./gradlew sonar \
  -Dsonar.token=YOUR_TOKEN \
  -Dsonar.branch.name=feature/my-feature
```

## Resources

- **SonarQube Documentation**: https://docs.sonarsource.com/sonarqube-server/
- **Gradle Plugin**: https://plugins.gradle.org/plugin/org.sonarqube
- **SonarQube Rules**: https://rules.sonarsource.com/java/

## Summary

To quickly get started:

1. Start SonarQube: `docker run -d --name sonarqube -p 9000:9000 sonarqube:latest`
2. Build project: `./gradlew clean build`
3. Run scan: `./gradlew sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=YOUR_TOKEN`
4. View results: http://localhost:9000

## Verified Execution

This setup has been tested and verified to work successfully with **both methods**:

**Server**: http://localhost:9000
**Project Key**: pseudo-project-for-static-analysis
**Project Name**: Pseudo Project for Static Analysis
**SonarQube Version**: Community Build 25.11.0.114957

### ✅ Method 1: Standalone Scanner (RECOMMENDED)

**Simplest approach with environment variable:**

```bash
export SONAR_TOKEN=sqa_eac42fb237d46cfb6a58c9d68b87583a46982548
../../temp/sonar-scanner/bin/sonar-scanner
```

**Or with inline token:**

```bash
../../temp/sonar-scanner/bin/sonar-scanner -Dsonar.token=sqa_eac42fb237d46cfb6a58c9d68b87583a46982548
```

**Execution Results:**
- **Scanner Version**: SonarScanner CLI 7.3.0.5189
- **Java Version**: 17.0.13 Eclipse Adoptium (64-bit)
- **Analysis Time**: 7.102 seconds
- **Total Time**: 10.475 seconds
- **Status**: ✅ EXECUTION SUCCESS
- **Files Analyzed**: 14 source files
- **Dashboard**: http://localhost:9000/dashboard?id=pseudo-project-for-static-analysis
- **Server**: Communicating with SonarQube Community Build 25.11.0.114957

**Key Features Detected:**
- Java 17 source files analyzed
- Git SCM integration working
- Quality profile: "Sonar way" for Java
- Text and secrets analysis completed
- CPD (Copy-Paste Detection) completed
- Analysis cache loaded (incremental analysis supported)

### ✅ Method 2: Gradle Plugin

```bash
./gradlew sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqa_eac42fb237d46cfb6a58c9d68b87583a46982548
```

**Execution Results:**
- **Execution Time**: ~7 seconds
- **Status**: BUILD SUCCESSFUL
- **Tasks Executed**: 4 actionable tasks (2 executed, 2 up-to-date)

### Quick Run Commands

**Using Standalone Scanner (Simplest):**
```bash
# Set token once per session
export SONAR_TOKEN=sqa_eac42fb237d46cfb6a58c9d68b87583a46982548

# Then just run (sonar.host.url is already configured in sonar-project.properties)
../../temp/sonar-scanner/bin/sonar-scanner

# Or in one line
../../temp/sonar-scanner/bin/sonar-scanner -Dsonar.token=sqa_eac42fb237d46cfb6a58c9d68b87583a46982548
```

**Using Gradle:**
```bash
./gradlew sonar -Dsonar.token=sqa_eac42fb237d46cfb6a58c9d68b87583a46982548

# Or with environment variable
export SONAR_TOKEN=sqa_eac42fb237d46cfb6a58c9d68b87583a46982548
./gradlew sonar
```

**Note**: The token shown above is for this local instance. Keep your tokens secure and never commit them to version control.

---

**Last Updated**: November 23, 2025
**SonarQube Gradle Plugin Version**: 7.0.1.6134
**Project Version**: 1.0.0
**Analysis Status**: ✅ Successfully analyzed
