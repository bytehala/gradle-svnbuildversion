package com.github.bytehala;

import org.tmatesoft.svn.core.wc.*
import java.nio.file.attribute.PosixFilePermissions;
import org.gradle.api.Project;
import org.gradle.api.Plugin;



class SvnBuildVersionPlugin implements Plugin<Project> {



    int getSvnRevision(String projectRoot) {
        def projectDir = new File(projectRoot);
        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager clientManager = SVNClientManager.newInstance(options);
        SVNStatusClient statusClient = clientManager.getStatusClient();
        SVNStatus status = statusClient.doStatus(projectDir, false);
        SVNRevision revision = status.getRevision();
        return revision.getNumber();
    }

    void write(String projectVersion, int svnRevision) {
        def propertyFile = new File('src/main/resources/config/buildversion.properties')

        def built = 1

        def Properties versionProperties = new Properties()
        if(propertyFile.canRead()) {
            versionProperties.load(new FileInputStream(propertyFile))
            if(versionProperties['built.number'] != null) {
                built = versionProperties['built.number'].toInteger() + 1
            }
        }

        println 'version: ' + projectVersion + '.' + svnRevision + '.' + built

        versionProperties.setProperty("built.number", "" + built)
        versionProperties.setProperty("projectVersion.number", "" + projectVersion)
        versionProperties.setProperty("svnRevision.number", "" + svnRevision)

        File f = new File('src/main/resources/config')
        f.mkdirs();
        versionProperties.store(new FileOutputStream(propertyFile), "Auto-generated by svnbuildversion gradle plugin");
        
    }



    void apply(Project project) {

        def extension = project.extensions.create('svnbuildversion', ProjectVersionPluginExt)

        def root = extension.projectRoot ?: '.'

        println 'root: ' + extension.projectRoot + ', projectVersion: ' + extension.projectVersion;

        project.task('generateBuildversionProps') {
            doLast {
                write(extension.projectVersion, getSvnRevision(extension.projectRoot));
            }
        }
    }
}
