package com.e_gineering.maven.gitflowhelper;

import com.e_gineering.maven.gitflowhelper.properties.ExpansionBuffer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmTagParameters;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.repository.ScmRepository;

/**
 * Invokes configures the builds SCM settings based on environment variables from a CI Server, and does an scm:tag for builds from Master.
 */
@Mojo(name = "tag-master", defaultPhase = LifecyclePhase.INSTALL)
public class TagMasterMojo extends AbstractGitflowBranchMojo {

    // @Parameter tag causes property resolution to fail for patterns containing ${env.}. Default value is resolved in execute()
    @Parameter(property = "gitURLExpression")
    private String gitURLExpression;

    @Parameter(defaultValue = "${project.version}", property = "tag", required = true)
    private String tag;

    @Override
    protected void execute(final GitBranchType type, final String gitBranch, final String branchPattern) throws MojoExecutionException, MojoFailureException {
        if (project.isExecutionRoot() && type.equals(GitBranchType.MASTER)) {
            if (gitURLExpression == null) {
                gitURLExpression = ScmUtils.resolveUrlOrExpression(scmManager, project, getLog());
            }
            String gitURL = resolveExpression(gitURLExpression);
            getLog().debug("gitURLExpression: '" + gitURLExpression + "' resolved to: '" + gitURL + "'");
            ExpansionBuffer eb = new ExpansionBuffer(gitURL);
            if (!eb.hasMoreLegalPlaceholders()) {

                getLog().info("Tagging SCM for CI build matching branchPattern: [" + branchPattern + "]");

                try {
                    ScmRepository repository = scmManager.makeScmRepository(gitURL);
                    ScmProvider provider = scmManager.getProviderByRepository(repository);

                    String sanitizedTag = provider.sanitizeTagName(tag);
                    getLog().info("Sanitized tag: '" + sanitizedTag + "'");

                    ScmTagParameters tagParams = new ScmTagParameters("Release tag [" + sanitizedTag + "] generated by gitflow-helper-maven-plugin.");
                    tagParams.setRemoteTagging(true);

                    provider.tag(repository, new ScmFileSet(project.getBasedir()), sanitizedTag, tagParams);
                } catch (ScmException scme) {
                    throw new MojoFailureException("Unable to tag master branch.", scme);
                }
            } else {
                throw new MojoFailureException("Unable to resolve gitURLExpression: " + gitURLExpression + ". Leaving build configuration unaltered.");
            }
        }
    }
}
