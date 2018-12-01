package com.praqma.demo.greeting

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.*
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.*

/**
 * Contains functional tests that use the GradleRunner to run the plugin's task in a controlled environment.
 * Reference:
 * https://docs.gradle.org/4.6/userguide/test_kit.html#sec:functional_testing_with_the_gradle_runner
 */
class SvnBuildVersionPluginTest {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();

    private File build_gradle

    @Before
    public void setup() {
        // Prepare build.gradle
        build_gradle = testProjectDir.newFile('build.gradle')
        build_gradle << 'plugins { id "com.github.bytehala.SvnBuildVersionPlugin" }\n'
    }

    /**
     * Helper method that runs a Gradle task in the testProjectDir
     * @param arguments the task arguments to execute
     * @param isSuccessExpected boolean representing whether or not the build is supposed to fail
     * @return the task's BuildResult
     */
    private BuildResult gradle(boolean isSuccessExpected, String[] arguments = ['tasks']) {
        arguments += '--stacktrace'
        def runner = GradleRunner.create()
                .withArguments(arguments)
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withDebug(true)
        return isSuccessExpected ? runner.build() : runner.buildAndFail()
    }

    private BuildResult gradle(String[] arguments = ['tasks']) {
        gradle(true, arguments)
    }

    @Test
    public void generateBuildversionPropFileTest() {
        build_gradle << """
                            svnbuildversion.projectVersion = "1.00"
                            svnbuildversion.projectRoot = "Add an svn-managed project root here"
                        """
        def result = gradle('generateBuildversionProps')
        assert result.task(":generateBuildversionProps").outcome == SUCCESS
        println result.output
        assert result.output.contains("version:")
    }

   
}
