/*
 * $Id$
 *
 * Copyright 2018 - 2020 Allen D. Ball.  All rights reserved.
 */
package ball.maven.plugins.artifact;

import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PACKAGE;

/**
 * {@link org.apache.maven.plugin.Mojo} to set project main artifact.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@Mojo(name = "main", defaultPhase = PACKAGE, requiresProject = true)
@NoArgsConstructor @ToString
public class ArtifactMainMojo extends AbstractArtifactMojo {
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
