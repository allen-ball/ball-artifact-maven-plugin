/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.maven.plugins.artifact;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * {@link org.apache.maven.plugin.Mojo} to set project main artifact.
 *
 * @author  <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@Mojo(name = "main", defaultPhase = LifecyclePhase.PACKAGE,
      requiresProject = true)
public class ArtifactMainMojo extends AbstractArtifactMojo {

    /**
     * Sole constructor.
     */
    public ArtifactMainMojo() { super(); }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (isConfigured()) {
                set(this);
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
}
