/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.maven.plugins.artifact;

import java.io.File;

/**
 * Attached {@code <artifact/>} parameter base interface.
 *
 * @author  <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public interface AttachedArtifact {
    public String getType();

    public String getClassifier();

    public File getFile();

    public boolean isConfigured();
}
