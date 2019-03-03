/*
 * $Id$
 *
 * Copyright 2018 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.maven.plugins.artifact;

import java.io.File;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * {@link org.apache.maven.plugin.Mojo} to attach (zero or more) artifacts
 * to a project.
 *
 * @author  <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@Mojo(name = "attach", defaultPhase = LifecyclePhase.PACKAGE,
      requiresProject = true)
@NoArgsConstructor @ToString
public class ArtifactAttachMojo extends AbstractArtifactMojo {
    @Parameter(property = "artifacts", required = false)
    protected List<Artifact> artifacts = null;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (isConfigured()) {
                attach(this);
            }

            if (artifacts != null) {
                for (Artifact artifact : artifacts) {
                    if (artifact.isConfigured()) {
                        attach(artifact);
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

    /**
     * {@code <artifact/>} parameter.
     */
    @NoArgsConstructor @ToString
    public static class Artifact extends AbstractAttachedArtifact {
    }
}
