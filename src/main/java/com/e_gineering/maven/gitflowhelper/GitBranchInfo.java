package com.e_gineering.maven.gitflowhelper;


import org.apache.commons.lang.StringUtils;

import java.util.Optional;

/**
 * Data holder defining the resolved branch name and type of branch.
 */
public class GitBranchInfo {

    private final String name;

    private final GitBranchType type;

    private final String pattern;

    /** SHA-1 of the GIT commit (can be <code>null</code>) */
    private final String commitId;

    /**
     * Constructs a GitBranchInfo object for the given name and type.
     *
     * @param name must not be null. (empty string OK)
     * @param type must not be null. (use OTHER)
     * @param pattern may be null
     * @param commitId The SHA-1 of the GIT commit (can be <code>null</code>)
     * @throws IllegalArgumentException if name or type are null
     */
    GitBranchInfo(final String name, final GitBranchType type, final String pattern, String commitId) {
        if(name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        this.name = name;
        this.type = type;
        this.pattern = pattern;
        this.commitId = StringUtils.trimToNull(commitId);
    }



    public String getName() {
        return name;
    }

    public GitBranchType getType() {
        return type;
    }

    public boolean isSnapshot() {
        return GitBranchType.SNAPSHOT_TYPES.contains(type);
    }

    public boolean isVersioned() {
        return GitBranchType.VERSIONED_TYPES.contains(type);
    }

    public String getPattern() {
        return pattern;
    }

    /**
     * Get the SHA-1 of the commit of the HEAD of the branch
     * @return The SHA of the commit (never <code>null</code>, when empty then the commit could not be evaluated)
     */
    public Optional<String> getCommitId() {
        return Optional.ofNullable(commitId);
    }

    /**
     * Get the short SHA-1 of the commit of the HEAD of the branch
     * @param length Length of the short SHA
     * @return The short SHA of the commit (never <code>null</code>, when empty then the commit could not be evaluated)
     */
    public Optional<String> getCommitIdShort(int length) {
        return getCommitId().map(sha -> StringUtils.substring(sha,0,length));
    }

    @Override
    public String toString() {
        return "GitBranchInfo: [" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", pattern='" + pattern + '\'' +
                ", sha-1='" + commitId + "']";
    }
}
