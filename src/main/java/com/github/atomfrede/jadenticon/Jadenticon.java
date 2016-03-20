package com.github.atomfrede.jadenticon;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

public class Jadenticon {

    private enum FileType {
        SVG, PNG
    }

    final String hash;
    private JdenticonWrapper jdenticonWrapper;
    int size;
    double padding;

    public Jadenticon(String hash) {
        jdenticonWrapper = new JdenticonWrapper();
        this.hash = hash;
        this.size = 300;
        this.padding = 0.08;
    }

    public static Jadenticon from(String text) {

        try {
            return new Jadenticon(Hash.generateHash(text));
        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException("Unable to generate hash.", e);
        }
    }

    public Jadenticon withSize(int size) {

        if (size <= 30)
            throw new IllegalArgumentException("Size must be greater than 30");

        this.size = size;
        return this;
    }

    public Jadenticon withPadding(double padding) {

        if (padding < 0.0)
            throw new IllegalArgumentException("Padding must be greater than 0.0");

        if (padding > 0.5)
            throw new IllegalArgumentException("Padding must be smaller than 0.5");

        this.padding = padding;
        return this;
    }

    public String toSvg() {

        return jdenticonWrapper.getSvg(this);
    }

    public File file() throws IOException {

        File f = createTempFile(FileType.SVG);
        FileUtils.writeStringToFile(f, jdenticonWrapper.getSvg(this));
        return f;
    }

    public File file(String fileName) throws IOException {

        File f = createTempFile(fileName, FileType.SVG);
        File result = new File(f.getParentFile(), fileName + ".svg");
        FileUtils.writeStringToFile(f, jdenticonWrapper.getSvg(this));
        FileUtils.moveFile(f, result);
        return result;
    }

    public File png() throws IOException, TranscoderException {

        PNGTranscoder pngTranscoder = new PNGTranscoder();

        TranscoderInput input = new TranscoderInput(this.file().toURI().toString());

        File outputfile = createTempFile("jdenticon-" + this.hash, FileType.PNG);

        OutputStream ostream = new FileOutputStream(outputfile);
        TranscoderOutput output = new TranscoderOutput(ostream);

        pngTranscoder.transcode(input, output);

        ostream.flush();
        ostream.close();

        return outputfile;
    }

    public File png(String filename) throws IOException, TranscoderException {

        PNGTranscoder pngTranscoder = new PNGTranscoder();

        TranscoderInput input = new TranscoderInput(this.file().toURI().toString());

        File outputfile = createTempFile(filename, FileType.PNG);

        OutputStream ostream = new FileOutputStream(outputfile);
        TranscoderOutput output = new TranscoderOutput(ostream);

        pngTranscoder.transcode(input, output);

        ostream.flush();
        ostream.close();

        return outputfile;
    }

    private File createTempFile(FileType fileType) throws IOException {
        File file = createTempFile("jdenticon-" + this.hash, fileType);
        file.deleteOnExit();
        return file;
    }

    private File createTempFile(String name, FileType fileType) throws IOException {

        File file;

        switch (fileType) {
            case SVG:
                file = File.createTempFile(name, ".svg");
                break;
            case PNG:
                file = File.createTempFile(name, ".png");
                break;
            default:
                file = File.createTempFile(name, ".svg");
                break;
        }

        file.deleteOnExit();
        return file;
    }

}
