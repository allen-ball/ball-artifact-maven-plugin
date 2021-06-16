package ball.maven.plugins.artifact;
/*-
 * ##########################################################################
 * Artifact Attach Maven Plugin
 * %%
 * Copyright (C) 2018 - 2021 Allen D. Ball
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
import java.util.Objects;
import javax.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import static lombok.AccessLevel.PROTECTED;

/**
 * {@link org.apache.maven.plugin.Mojo}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@NoArgsConstructor(access = PROTECTED) @ToString @Slf4j
public abstract class AbstractArtifactMojo extends AbstractMojo
                                           implements AttachedArtifact {
    protected static final String JAR = "jar";

    @Inject
    private MavenProjectHelper helper = null;

    @Parameter(defaultValue = "${project}")
    private MavenProject project = null;

    @Parameter(defaultValue = "${project.build.directory}")
    private File directory = null;

    @Parameter(defaultValue = "${project.build.finalName}")
    private String name = null;

    @Parameter(property = "type")
    @Getter
    private String type = null;

    @Parameter(property = "classifier")
    @Getter
    private String classifier = null;

    @Parameter(property = "file")
    @Getter
    private File file = null;

    @Override
    public boolean isConfigured() {
        return isNotEmpty(type) || isNotEmpty(classifier) || file != null;
    }

    /**
     * Method to set the main artifact.
     *
     * @param   artifact        The {@link AttachedArtifact}.
     */
    protected void set(AttachedArtifact artifact) {
        File file = getArtifactFile(artifact);

        log.info("{} <-- {}",
                 label(project.getArtifact().getClassifier(),
                       project.getArtifact().getType()), file);
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

        log.info("{} <-- {}", label(artifact), file);
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

    private static boolean isEmpty(CharSequence sequence) {
        return Objects.isNull(sequence) || sequence.length() == 0;
    }

    private static boolean isNotEmpty(CharSequence sequence) {
        return (! isEmpty(sequence));
    }

    private static String defaultString(String string, String defaultString) {
        return (string != null) ? string : defaultString;
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
