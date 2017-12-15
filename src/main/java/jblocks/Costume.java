package jblocks;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.jsfml.graphics.Image;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;
import org.w3c.dom.svg.SVGDocument;

import com.fasterxml.jackson.databind.JsonNode;

public class Costume extends Asset {
    Image image;
    Texture texture;
    Vector2f center;
    float resolution;

    public Costume(JsonNode config) {
        super(config);
        name = config.path("costumeName").textValue();
        hash = config.path("baseLayerMD5").textValue();
        center = new Vector2f(config.path("rotationCenterX").floatValue(), config.path("rotationCenterY").floatValue());
        resolution = config.path("bitmapResolution").floatValue();
        try {
            loadAsset();
        } catch (IOException e) {
            System.out.println("Could not load asset: " + e.getMessage());
        }
    }

    static final boolean CACHE = true;

    @Override
    public void loadAsset() throws IOException {
        image = new Image();
        texture = new Texture();

        System.out.println("Loading " + hash);

        InputStream stream = null;

        String url = "http://cdn.assets.scratch.mit.edu/internalapi/asset/" + hash + "/get/";
        if (CACHE) {
            File directory = new File(".cache");
            if (!directory.exists()) {
                directory.mkdir();
            }
            File f = new File(".cache", hash);
            if (!f.exists()) {
                InputStream http = new URL(url).openStream();
                byte[] buffer = new byte[1024];
                int n = -1;
                OutputStream output = new FileOutputStream(f);
                while ((n = http.read(buffer)) != -1) {
                    output.write(buffer, 0, n);
                }
                output.close();
            }
            stream = new FileInputStream(f);
        } else {
            stream = new URL(url).openStream();
        }
        if (hash.endsWith(".svg")) {
            SVGDocument document = (SVGDocument) new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument("http://scratch.mit.edu/", stream);
            BufferedImage img = new SvgImage(document).getImage();
            ImageIO.write(img, "png", new File(".cache", hash + ".png"));
            image.create(img);
        } else {
            image.create(ImageIO.read(stream));
        }

        try {
            texture.loadFromImage(image);
        } catch (TextureCreationException e) {
            System.err.println(e.getMessage());
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public Image getImage() {
        return image;
    }

    public Vector2f getCenter() {
        return center;
    }

    public float getResolution() {
        return resolution;
    }
}
