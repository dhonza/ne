/*
 * Java Arcade Learning Environment (A.L.E) Agent
 *  Copyright (C) 2011-2012 Marc G. Bellemare <mgbellemare@ualberta.ca>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hyper.experiments.ale.movie;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.NumberFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A class for exporting screen images to PNG files. Now packed in zip file.
 *
 * @author Marc G. Bellemare <mgbellemare@ualberta.ca>, Drchal
 */
public class MovieGenerator {
    private final String baseFilename;
    private ZipOutputStream zos;

    /**
     * The current frame index (used to obtain the PNG filename)
     */
    private int pngIndex = 0;

    /**
     * How many digits to use in generating the filename
     */
    private final int indexDigits = 6;

    public MovieGenerator(File zipFile, String baseFilename) {
        // Create the relevant directory if necessary
        this.baseFilename = baseFilename;

        try {
            this.zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * This method saves the given image to zip as the next frame. It then
     * increments pngIndex.
     *
     * @param image
     */
    public void record(BufferedImage image) {
        // Create a formatter to generate 6-digit indices
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMinimumIntegerDigits(indexDigits);
        formatter.setGroupingUsed(false);

        // Obtain a 6-digit character representation of pngIndex
        String indexString = formatter.format(pngIndex);

        // Create the full filename
        String filename = baseFilename + indexString + ".png";

        // Save the image to zip
        try {
            ZipEntry zipEntry = new ZipEntry(filename);
            zos.putNextEntry(zipEntry);
            ImageIO.write(image, "png", zos);
            zos.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Increment pngIndex so that the next frame has a different filename
        pngIndex++;
    }

    public void close() {
        try {
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
