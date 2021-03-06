package com.github.bytehala

import org.tmatesoft.svn.core.wc.*
import org.tmatesoft.svn.core.SVNException
import org.gradle.api.Project
import org.gradle.api.Plugin



class SvnBuildVersionPlugin implements Plugin<Project> {



    String getSvnRevision(String projectRoot, boolean useCommittedRevision) {
        try {
            def projectDir = new File(projectRoot)
            ISVNOptions options = SVNWCUtil.createDefaultOptions(true)
            SVNClientManager clientManager = SVNClientManager.newInstance(options)
            SVNStatusClient statusClient = clientManager.getStatusClient()
            SVNStatus status = statusClient.doStatus(projectDir, false)
            SVNRevision revision = useCommittedRevision ? status.getCommittedRevision() : status.getRevision()
            return '' + revision.getNumber()
        } catch (SVNException e) {
            return 'NaN'
        }
    }

    void write(String projectVersion, String svnRevision, String outputDir) {
        def propertyFile = new File(outputDir + '/buildversion.properties')

        def built = 1

        Properties versionProperties = new Properties()
        if(propertyFile.canRead()) {
            new FileInputStream(propertyFile).withCloseable() {
                versionProperties.load(it)
            }

            // If svnRevision.number is the same, increment built.number; default is already 1
            def oldRevNum = versionProperties['svnRevision.number']
            if(oldRevNum.equals(svnRevision) && versionProperties['built.number'] != null) {
                built = versionProperties['built.number'].toInteger() + 1
            }
        }

        println 'version: ' + projectVersion + '.' + svnRevision + '.' + built

        versionProperties.setProperty("built.number", "" + built)
        versionProperties.setProperty("projectVersion.number", "" + projectVersion)
        versionProperties.setProperty("svnRevision.number", "" + svnRevision)

        File f = new File(outputDir)
        f.mkdirs()

        new FileOutputStream(propertyFile).withCloseable {
            versionProperties.store(it, "Auto-generated by svnbuildversion gradle plugin")
        }
    }



    void apply(Project project) {

        def extension = project.extensions.create('svnbuildversion', ProjectVersionPluginExt)

        def root = extension.projectRoot ?: '.'

        // println 'root: ' + extension.projectRoot + ', projectVersion: ' + extension.projectVersion;

        project.task('generateBuildversionProps') {
            doLast {
                write(extension.projectVersion, getSvnRevision(extension.projectRoot, extension.useCommittedRevision), extension.outputDir)
            }
        }
    }
}
