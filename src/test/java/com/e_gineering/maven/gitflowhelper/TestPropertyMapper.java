package com.e_gineering.maven.gitflowhelper;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;

/**
 * Unit-Test for the class {@link PropertyMapper}
 */
public class TestPropertyMapper
{
    /**
     * Tests the methode {@link PropertyMapper#map(GitBranchInfo)} with a Java Mapper
     * @throws MojoExecutionException on error
     */
    @Test
    public void testPropertyMapperWithJava() throws MojoExecutionException
    {
        PropertyMapper mapper = new PropertyMapper();
        mapper.setPropertyName("prop");
        mapper.setLanguage("java");
        mapper.setMapper(ToLowerCaseMapper.class.getName());

        GitBranchInfo info = new GitBranchInfo("FOO", GitBranchType.DEVELOPMENT, "bar", "sha");

        Assert.assertEquals("foo", mapper.map(info));
    }

    /**
     * Tests the methode {@link PropertyMapper#map(GitBranchInfo)} with a Groovy Mapper
     * @throws MojoExecutionException on error
     */
    @Test
    public void testPropertyMapperWithGroovy() throws MojoExecutionException
    {
        PropertyMapper mapper = new PropertyMapper();
        mapper.setPropertyName("prop");
        mapper.setLanguage("groovy");
        mapper.setMapper(""
            + "def map(branchName, branchType) {\n"
            +"     return branchName.toLowerCase();\n"
            + "}"
        );

        GitBranchInfo info = new GitBranchInfo("FOO", GitBranchType.DEVELOPMENT, "bar", "sha");

        Assert.assertEquals("foo", mapper.map(info));

    }

    /**
     * Tests the conversion of a branch name to a valid Docker image name
     * @throws Exception on error
     */
    @Test
    public void testPropertyMapperWithGroovBranchNameToDockerImageNameDevelopBranch() throws Exception {

        PropertyMapper mapper = new PropertyMapper();
        mapper.setPropertyName("prop");
        mapper.setLanguage("groovy");

        mapper.setMapper(IOUtils.toString(getClass().getResource("PropertyMapperBranchNameToDockerName.groovy"), StandardCharsets.UTF_8));

        Assert.assertEquals("", mapper.map(new GitBranchInfo("", GitBranchType.OTHER, "", "sha")));

        Assert.assertEquals("foo", mapper.map(new GitBranchInfo("FOO", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("f_f", mapper.map(new GitBranchInfo("F_f", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("f_ae", mapper.map(new GitBranchInfo("F_ä", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("f_ae", mapper.map(new GitBranchInfo("F_Ä", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("f_oe", mapper.map(new GitBranchInfo("F_ö", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("f_oe", mapper.map(new GitBranchInfo("F_Ö", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("f_ue", mapper.map(new GitBranchInfo("F_ü", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("f_ue", mapper.map(new GitBranchInfo("F_Ü", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("f_ss", mapper.map(new GitBranchInfo("F_ß", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("f_ss", mapper.map(new GitBranchInfo("F_ß", GitBranchType.OTHER, "", "sha")));

        Assert.assertEquals("f__", mapper.map(new GitBranchInfo("F_!", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("f__a_", mapper.map(new GitBranchInfo("F__!", GitBranchType.OTHER, "", "sha")));

        Assert.assertEquals("a_f", mapper.map(new GitBranchInfo("_f", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("a__a_", mapper.map(new GitBranchInfo("___", GitBranchType.OTHER, "", "sha")));

        Assert.assertEquals("a.a", mapper.map(new GitBranchInfo(".a", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("a-a", mapper.map(new GitBranchInfo("-a", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("a_a", mapper.map(new GitBranchInfo("_a", GitBranchType.OTHER, "", "sha")));

        Assert.assertEquals("a__a", mapper.map(new GitBranchInfo("__a", GitBranchType.OTHER, "", "sha")));
        Assert.assertEquals("a---a", mapper.map(new GitBranchInfo("---a", GitBranchType.OTHER, "", "sha")));
    }


    public static class ToLowerCaseMapper implements BiFunction<String,String,String>
    {
        @Override
        public String apply(String branchName, String branchType)
        {
            return branchName.toLowerCase();
        }
    }
}