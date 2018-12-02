# gradle-svnbuildversion 1.1

This gradle plugin adds a `:generateBuildversionProps` task to your gradle project.

When `:generateBuildversionProps` task is run, the file `src/main/resources/config/buildversion.properties` is generated with the following contents:
```properties
# buildversion.properties
projectVersion.number=0.1 # Uses the value of svnbuildversion.projectVersion
built.number=9 # Number of times this file was generated. Can be useful when doing CI auto-builds
svnRevision.number=109 # The current svn revision number

```


## How to use

1. Add the plugin.

```groovy
// build.gradle
buildscript {
...
}

plugins {
    id "com.github.bytehala.gradle-svnbuildversion" version "1.1"
}

svnbuildversion.projectVersion = 1.0 // Your project's version
```

2. Run `gradle generateBuildversionProps`

## Configuring

The plugin adds the following variable which you can use in your projects build.gradle
- svnbuildversion.projectVersion

## Build-your-own
These steps guide you through building this plugin on your own and using the jar file in your project.

1. `git clone https://github.com/bytehala/gradle-svnbuildversion.git`

2. (optional) To make the unit test succeed, edit `SvnBuildVersionPluginTest.groovy` by pointing `svnbuildversion.projectRoot` to a root directory of an svn project

3. Run `gradle clean build` (or `gradle clean build -x test` if you skipped Step 2). The output will be in `build/libs/gradle-svnbuildversion-X.X.jar`

4. Copy gradle-svnbuildversion-X.X.jar into your project (e.g. myProject/libs/)

5. Configure build.gradle in your project
```groovy
buildscript {
    ...
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.0.3.RELEASE")
        classpath("org.tmatesoft.svnkit:svnkit:1.9.3") // The jar file depends on this library
        classpath("com.github.bytehala:gradle-svnbuildversion:1.1")
    }
}


svnbuildversion.projectVersion = 1.0 // Your project's version
```

6. Run `gradle generateBuildversionProps`
