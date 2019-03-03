/*
 * $Id$
 *
 * Copyright 2018, 2019 Allen D. Ball.  All rights reserved.
 */
package ball.maven.plugins.artifact;

import java.io.File;
import java.net.URI;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.repository.Repository;

import static java.util.Locale.ENGLISH;
import static org.codehaus.plexus.util.StringUtils.defaultString;

/**
 * {@link org.apache.maven.plugin.Mojo} to download and attach (zero or
 * more) artifacts to a project.
 *
 * @author  <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@Mojo(name = "download", defaultPhase = LifecyclePhase.GENERATE_SOURCES,
      requiresProject = true)
@NoArgsConstructor @ToString
public class ArtifactDownloadMojo extends AbstractArtifactMojo {
    @Parameter(property = "attach", required = false, defaultValue = "true")
    protected boolean attach = true;

    @Parameter(property = "uri", required = false)
    protected URI uri = null;

    @Parameter(property = "artifacts", required = false)
    protected List<Artifact> artifacts = null;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (isConfigured() || uri != null) {
                download(this, attach, uri);
            }

            if (artifacts != null) {
                for (Artifact artifact : artifacts) {
                    if (artifact.isConfigured()) {
                        download(artifact,
                                 attach && artifact.getAttach(),
                                 artifact.getUri());
                    }
                }
            }
        } catch (Exception exception) {
            if (exception instanceof MojoExecutionException) {
                throw (MojoExecutionException) exception;
            } else if (exception instanceof MojoFailureException) {
                throw (MojoFailureException) exception;
            } else {
                String message = exception.getMessage();

                throw new MojoFailureException(exception, message, message);
            }
        }
    }

    private void download(AttachedArtifact artifact,
                          boolean attach, URI uri) throws Exception {
        if (uri != null) {
            File local =
                getLocalRepositoryFile(artifact.getType(),
                                       artifact.getClassifier());

            if (! local.exists()) {
                File file = getArtifactFile(artifact);
                String protocol =
                    defaultString(uri.getScheme(), "file")
                    .toLowerCase(ENGLISH);
                Wagon wagon = null;

                try {
                    wagon = container.lookup(Wagon.class, protocol);
                } catch (Exception exception) {
                    throw new UnsupportedProtocolException("Cannot find wagon for: "
                                                           + protocol,
                                                           exception);
                }

                getLog().info(file.getAbsolutePath());
                getLog().info("<-- " + uri.toASCIIString());

                wagon.connect(new Repository(uri.getHost(),
                                             uri.resolve("/").toString()));
                wagon.get(uri.getPath(), file);
                wagon.disconnect();

                if (attach) {
                    attach(artifact);
                }
            } else {
                getLog().info(local.getAbsolutePath()
                              + " is already installed; skipping");
            }
        } else {
            throw new MojoExecutionException("`uri' must be specified.");
        }
    }

    /**
     * {@code <artifact/>} parameter.
     */
    @NoArgsConstructor @ToString
    public static class Artifact extends AbstractAttachedArtifact {
        private boolean attach = true;
        private URI uri = null;

        public Boolean getAttach() { return attach; }
        public void setAttach(boolean attach) { this.attach = attach; }

        public URI getUri() { return uri; }
        public void setUri(URI uri) { this.uri = uri; }
    }
}
