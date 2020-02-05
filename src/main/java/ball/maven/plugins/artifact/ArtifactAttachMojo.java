package ball.maven.plugins.artifact;
/*-
 * ##########################################################################
 * Artifact Attach Maven Plugin
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2018 - 2020 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import java.io.File;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PACKAGE;

/**
 * {@link org.apache.maven.plugin.Mojo} to attach (zero or more) artifacts
 * to a project.
 *
 * {@maven.plugin}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@Mojo(name = "attach", defaultPhase = PACKAGE, requiresProject = true)
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
