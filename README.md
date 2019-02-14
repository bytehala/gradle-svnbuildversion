# gradle-svnbuildversion 1.X

This gradle plugin adds a `:generateBuildversionProps` task to your gradle project.

When `:generateBuildversionProps` task is run, the output file `src/main/resources/config/buildversion.properties` is generated with the following contents:
```properties
# buildversion.properties
# SAMPLE CONTENTS
projectVersion.number=0.1 # Uses the value of svnbuildversion.projectVersion
built.number=9 # Numer of times this revision was built. This resets to 1 for when svnRevision.number changes
svnRevision.number=109 # The current svn revision number. 'NaN' if project is not svn-managed

```


## How to use

1. Add the plugin.

```groovy
// build.gradle
buildscript {
...
}

plugins {
    id "com.github.bytehala.gradle-svnbuildversion" version "1.X"
}

svnbuildversion.projectVersion = 1.0 // Your project's version
//svnbuildversion.outputDir = 'src/main/resources/config/'
//svnbuildversion.useCommittedRevision = false
```

2. Run `gradle generateBuildversionProps`

## Configuring

The plugin adds the following variable which you can use in your projects build.gradle
- svnbuildversion.projectVersion - to pass your project's version into the plugin
- svnbuildversion.outputDir - where buildversion.properties will be generated
- svnbuildversion.useCommittedRevision - whether to use the working revision (default) or committed revision of project directory

Use the committed revision if the SVN repository hosts multiple projects and you don't want the global revision in `buildversion.properties`.

---


## Build-your-own
These steps guide you through building this plugin on your own and using the jar file in your project.

1. `git clone https://github.com/bytehala/gradle-svnbuildversion.git`

2. (optional) To test what the output file will look like for your svn-managed project, edit `SvnBuildVersionPluginTest.groovy` by pointing `svnbuildversion.projectRoot` to your project's root directory

3. Run `gradle clean build`. The binaries will be in `build/libs/gradle-svnbuildversion-X.X.jar`

4. Copy `gradle-svnbuildversion-X.X.jar` into your project (e.g. myProject/libs/)

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
        classpath("com.github.bytehala:gradle-svnbuildversion:1.X")
    }
}


svnbuildversion.projectVersion = 1.0 // Your project's version
//svnbuildversion.outputDir = 'src/main/resources/config/'
```

6. Run `gradle generateBuildversionProps`
