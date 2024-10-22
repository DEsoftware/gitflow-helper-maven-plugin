package com.e_gineering.maven.gitflowhelper;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

import java.util.Optional;

/**
 * Stores the branch type and the git branch in Maven properties
 */
@Mojo(name = "set-git-properties", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true)
public class SetGitPropertiesMojo extends AbstractGitflowBranchMojo {
    /**
     * Defines the name of the property where the branch type is stored
     */
    @Parameter(property = "branchTypeProperty", defaultValue = "branchType")
    private final String branchTypeProperty = "branchType";

    /**
     * Defines the name of the property where the Git branch name is stored.
     */
    @Parameter(property = "branchNameProperty", defaultValue = "gitBranchName")
    private final String branchNameProperty = "gitBranchName";

    /**
     * Der branchNamePropertyMapper allows to store the Git branch name
     * into additional properties.<br>
     * The branchName can be mapped by a java class, or an JSR223 scripting language
     */
    @Parameter(property = "branchNamePropertyMappers")
    private PropertyMapper[] branchNamePropertyMappers;

    /**
     * Name of the property where the short SHA-1 of the commit of the HEAD of the branch is stored
     */
    @Parameter(defaultValue = "commitId")
    private String commitIdProperty;

    /**
     * Name of the property where the short SHA-1 of the commit of the HEAD of the branch is stored
     */
    @Parameter(defaultValue = "commitIdShort")
    private String commitIdShortProperty;

    /**
     * Length of the short SHA-1
     */
    @Parameter(defaultValue = "8")
    private int commitIdShortLength;

    @Override
    protected void execute(final GitBranchInfo gitBranchInfo) throws MojoExecutionException, MojoFailureException {
        if (!StringUtils.isBlank(branchTypeProperty)) {
            project.getProperties().setProperty(branchTypeProperty, gitBranchInfo.getType().name());
        }
        if (!StringUtils.isBlank(branchNameProperty)) {
            project.getProperties().setProperty(branchNameProperty, gitBranchInfo.getName());
        }

        if (!StringUtils.isBlank(commitIdShortProperty)) {
            Optional<String> shortSha = gitBranchInfo.getCommitIdShort(commitIdShortLength);
            if (shortSha.isPresent()) {
                project.getProperties().setProperty(commitIdShortProperty, shortSha.get());
                getLog().info("Set property [" + commitIdShortProperty + "] to [" + shortSha.get() + "]");
            } else {
                getLog().warn("Could not set property [" + commitIdShortProperty + "], because SHA-1 of commit is unknown");
            }
        }

        if (branchNamePropertyMappers != null) {
            for (PropertyMapper pm : branchNamePropertyMappers) {
                String mappedValue = pm.map(gitBranchInfo);
                getLog().info("Mapped Git branch name [" + gitBranchInfo.getName() + "] for property [" + pm.getPropertyName() + "] to [" + mappedValue + "]");
                project.getProperties().setProperty(pm.getPropertyName(), mappedValue);
            }
        }
    }
}
