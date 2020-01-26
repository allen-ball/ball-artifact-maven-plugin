/*
 * $Id$
 *
 * Copyright 2018 - 2020 Allen D. Ball.  All rights reserved.
 */
package ball.maven.plugins.artifact;

import java.io.File;

/**
 * Attached {@code <artifact/>} parameter base interface.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface AttachedArtifact {
    public String getType();

    public String getClassifier();

    public File getFile();

    public boolean isConfigured();
}
