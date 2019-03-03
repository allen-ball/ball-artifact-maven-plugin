/*
 * $Id$
 *
 * Copyright 2018, 2019 Allen D. Ball.  All rights reserved.
 */
package ball.maven.plugins.artifact;

import java.io.File;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import static lombok.AccessLevel.PROTECTED;
import static org.codehaus.plexus.util.StringUtils.defaultString;
import static org.codehaus.plexus.util.StringUtils.isNotEmpty;

/**
 * {@link org.apache.maven.plugin.Mojo}
 *
 * @author  <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@NoArgsConstructor @ToString
public abstract class AbstractArtifactMojo extends AbstractMojo
                                           implements AttachedArtifact,
                                                      Contextualizable {
    protected static final String JAR = "jar";

    protected PlexusContainer container = null;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected MavenSession session = null;

    @Parameter(defaultValue = "${localRepository}",
               readonly = true, required = true)
    protected ArtifactRepository local = null;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}",
               readonly = true, required = true)
    protected List<ArtifactRepository> remote = null;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project = null;

    @Parameter(defaultValue = "${mojoExecution}",
               readonly = true, required = true)
    protected MojoExecution mojo = null;

    @Parameter(defaultValue = "${plugin}", readonly = true, required = true)
    protected PluginDescriptor plugin = null;

    @Parameter(defaultValue = "${settings}", readonly = true, required = true)
    protected Settings settings = null;

    @Component(role = MavenProjectHelper.class)
    protected MavenProjectHelper helper = null;

    @Parameter(defaultValue = "${project.build.directory}")
    protected File directory = null;

    @Parameter(defaultValue = "${project.build.finalName}")
    protected String name;

    @Parameter(property = "type", required = false)
    protected String type = null;

    @Parameter(property = "classifier", required = false)
    protected String classifier = null;

    @Parameter(property = "file", required = false)
    protected File file = null;

    @Override
    public String getType() { return type; }

    @Override
    public String getClassifier() { return classifier; }

    @Override
    public File getFile() { return file; }

    @Override
    public boolean isConfigured() {
        return isNotEmpty(type) || isNotEmpty(classifier) || file != null;
    }

    @Override
    public void contextualize(Context context) throws ContextException {
        container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }

    /**
     * Method to set the main artifact.
     *
     * @param   artifact        The {@link AttachedArtifact}.
     */
    protected void set(AttachedArtifact artifact) {
        File file = getArtifactFile(artifact);

        getLog().info(label(project.getArtifact().getClassifier(),
                            project.getArtifact().getType())
                      + " <-- " + file);
        project.getArtifact().setFile(file);
    }

    /**
     * Method to attach an artifact.  See
     * {@link MavenProjectHelper#attachArtifact(MavenProject,String,String,File)}.
     *
     * @param   artifact        The {@link AttachedArtifact}.
     */
    protected void attach(AttachedArtifact artifact) {
        File file = getArtifactFile(artifact);

        getLog().info(label(artifact) + " <-- " + file);
        helper.attachArtifact(project,
                              defaultString(artifact.getType(), JAR),
                              artifact.getClassifier(),
                              file);
    }

    /**
     * Method to get the default build {@link File}.
     *
     * @param   artifact        The {@link AttachedArtifact}.
     *
     * @return  The argument {@link File} if non-{@code null}, the default
     *          {@link File} otherwise.
     */
    protected File getArtifactFile(AttachedArtifact artifact) {
        String type = defaultString(artifact.getType(), JAR);
        String classifier = artifact.getClassifier();
        File file = artifact.getFile();

        if (file == null) {
            file =
                new File(directory,
                         name
                         + (isNotEmpty(classifier) ? ("-" + classifier) : "")
                         + "." + type);
        }

        return file;
    }

    private String label(AttachedArtifact artifact) {
        return label(artifact.getType(), artifact.getClassifier());
    }

    private String label(String type, String classifier) {
        return (defaultString(classifier, "")
                + ((isNotEmpty(classifier) && isNotEmpty(type)) ? ":" : "")
                + defaultString(type, ""));
    }

    /**
     * See
     * {@link org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout#pathOf(Artifact)}.
     *
     * @param   type            The {@link String} type.
     * @param   classifier      The {@link String} classifier.
     *
     * @return  The {@link File} of the local repository (may or may not
     *          exist).
     */
    protected File getLocalRepositoryFile(String type, String classifier) {
        File file = new File(local.getBasedir());

        for (String string : project.getArtifact().getGroupId().split("[.]")) {
            file = new File(file, string);
        }

        file = new File(file, project.getArtifact().getArtifactId());
        file = new File(file, project.getArtifact().getBaseVersion());

        String name = project.getArtifactId() + "-" + project.getVersion();

        if (isNotEmpty(classifier)) {
            name += "-" + classifier;
        }

        name += "." + defaultString(type, JAR);

        file = new File(file, name);

        return file;
    }

    /**
     * {@code <artifact/>} parameter abstract base class.
     */
    @NoArgsConstructor(access = PROTECTED) @ToString
    protected static class AbstractAttachedArtifact
                           implements AttachedArtifact {
        @Getter @Setter private String type = null;
        @Getter @Setter private String classifier = null;
        @Getter @Setter private File file = null;

        @Override
        public boolean isConfigured() {
            return isNotEmpty(type) || isNotEmpty(classifier) || file != null;
        }
    }
}
