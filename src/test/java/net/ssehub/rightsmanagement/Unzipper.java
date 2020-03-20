package net.ssehub.rightsmanagement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Assertions;

/**
 * Unzips archives during tests to the temp folder.
 * @author El-Sharkawy
 *
 */
public class Unzipper {
    
    private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));

    /**
     * Unzips a <tt>*.tar.gz</tt> archive.<p>
     * <font color="red"><b>Attention:</b></font> The root of the arcive should be a directory that contain
     * further content.
     * @param archiveFile The <tt>*.tar.gz</tt> archive that contains a zipped directory.
     * @return The location of the unpacked directory.
     * @see <a href="https://commons.apache.org/proper/commons-compress/examples.html#Common_Extraction_Logic">
     * Apache Commons-compress Examples</a>
     */
    public static File unTarGz(File archiveFile) {
        File destFolder = null;
        
        try (InputStream fi = Files.newInputStream(archiveFile.toPath());
                InputStream bi = new BufferedInputStream(fi);
                InputStream gzi = new GzipCompressorInputStream(bi);
                ArchiveInputStream i = new TarArchiveInputStream(gzi)) {
            
            ArchiveEntry entry = null;
            while ((entry = i.getNextEntry()) != null) {
                if (!i.canReadEntryData(entry)) {
                    // log something?
                    continue;
                }
                File f = new File(TEMP_DIR, entry.getName());
                if (entry.isDirectory()) {
                    if (!f.isDirectory() && !f.mkdirs()) {
                        Assertions.fail("failed to create directory " + f);
                    }
                    if (null == destFolder) {
                        destFolder = f;
                        destFolder.deleteOnExit();
                    }
                } else {
                    File parent = f.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        Assertions.fail("failed to create directory " + parent);
                    }
                    try (OutputStream o = Files.newOutputStream(f.toPath())) {
                        IOUtils.copy(i, o);
                    }
                }
            }
        } catch (IOException e) {
            Assertions.fail("Could not unpack archive " + archiveFile.getAbsolutePath()
                + " to " + destFolder.getAbsolutePath(), e);
        }
        
        return destFolder;
    }

}
