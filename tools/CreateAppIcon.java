import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/** Creates the Sabatha School desktop shortcut icon. */
public final class CreateAppIcon {
    private static final int[] SIZES = {256, 128, 64, 48, 32, 16};

    private CreateAppIcon() { }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Supply the output .ico file path.");
        }
        Path destination = Paths.get(args[0]);
        Files.createDirectories(destination.toAbsolutePath().getParent());
        List<byte[]> pngs = new ArrayList<>();
        for (int size : SIZES) {
            ByteArrayOutputStream png = new ByteArrayOutputStream();
            ImageIO.write(draw(size), "png", png);
            pngs.add(png.toByteArray());
        }
        writeIco(destination, pngs);
        ImageIO.write(draw(512), "png", destination.resolveSibling("SabathaSchool-preview.png").toFile());
    }

    private static BufferedImage draw(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        float s = size;
        float inset = s * 0.025f;
        RoundRectangle2D.Float tile = new RoundRectangle2D.Float(
                inset, inset, s - inset * 2, s - inset * 2, s * 0.25f, s * 0.25f);
        g.setPaint(new GradientPaint(0, 0, new Color(10, 20, 40), s, s, new Color(24, 57, 99)));
        g.fill(tile);
        g.setColor(new Color(91, 162, 255, 190));
        g.setStroke(new BasicStroke(Math.max(1f, s * 0.012f)));
        g.draw(tile);

        Path2D.Float cover = new Path2D.Float();
        cover.moveTo(s * 0.17, s * 0.62);
        cover.curveTo(s * 0.30, s * 0.60, s * 0.42, s * 0.65, s * 0.50, s * 0.72);
        cover.curveTo(s * 0.58, s * 0.65, s * 0.70, s * 0.60, s * 0.83, s * 0.62);
        cover.lineTo(s * 0.83, s * 0.72);
        cover.curveTo(s * 0.70, s * 0.71, s * 0.58, s * 0.77, s * 0.50, s * 0.83);
        cover.curveTo(s * 0.42, s * 0.77, s * 0.30, s * 0.71, s * 0.17, s * 0.72);
        cover.closePath();
        g.setPaint(new GradientPaint(0, s * 0.62f, new Color(52, 132, 247), s, s * 0.82f,
                new Color(32, 91, 197)));
        g.fill(cover);

        Path2D.Float leftPage = new Path2D.Float();
        leftPage.moveTo(s * 0.19, s * 0.30);
        leftPage.curveTo(s * 0.31, s * 0.28, s * 0.42, s * 0.32, s * 0.50, s * 0.39);
        leftPage.lineTo(s * 0.50, s * 0.70);
        leftPage.curveTo(s * 0.40, s * 0.63, s * 0.29, s * 0.59, s * 0.19, s * 0.62);
        leftPage.closePath();
        Path2D.Float rightPage = new Path2D.Float();
        rightPage.moveTo(s * 0.50, s * 0.39);
        rightPage.curveTo(s * 0.58, s * 0.32, s * 0.69, s * 0.28, s * 0.81, s * 0.30);
        rightPage.lineTo(s * 0.81, s * 0.62);
        rightPage.curveTo(s * 0.71, s * 0.59, s * 0.60, s * 0.63, s * 0.50, s * 0.70);
        rightPage.closePath();

        g.setPaint(new GradientPaint(0, s * 0.28f, new Color(255, 255, 255), s, s * 0.70f,
                new Color(218, 232, 250)));
        g.fill(leftPage);
        g.setPaint(new GradientPaint(0, s * 0.28f, new Color(239, 247, 255), s, s * 0.70f,
                new Color(205, 223, 246)));
        g.fill(rightPage);

        g.setColor(new Color(98, 126, 163));
        g.setStroke(new BasicStroke(Math.max(1f, s * 0.012f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int row = 0; row < 3; row++) {
            float y = s * (0.45f + row * 0.075f);
            g.drawLine(Math.round(s * 0.27f), Math.round(y), Math.round(s * (row == 2 ? 0.40f : 0.43f)), Math.round(y));
            g.drawLine(Math.round(s * 0.57f), Math.round(y), Math.round(s * (row == 2 ? 0.73f : 0.70f)), Math.round(y));
        }
        g.setColor(new Color(59, 130, 246));
        g.setStroke(new BasicStroke(Math.max(1.2f, s * 0.018f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(Math.round(s * 0.50f), Math.round(s * 0.39f), Math.round(s * 0.50f), Math.round(s * 0.72f));

        Path2D.Float spark = new Path2D.Float();
        spark.moveTo(s * 0.75, s * 0.16);
        spark.lineTo(s * 0.78, s * 0.22);
        spark.lineTo(s * 0.84, s * 0.25);
        spark.lineTo(s * 0.78, s * 0.28);
        spark.lineTo(s * 0.75, s * 0.34);
        spark.lineTo(s * 0.72, s * 0.28);
        spark.lineTo(s * 0.66, s * 0.25);
        spark.lineTo(s * 0.72, s * 0.22);
        spark.closePath();
        g.setColor(new Color(255, 201, 92));
        g.fill(spark);
        g.dispose();
        return image;
    }

    private static void writeIco(Path destination, List<byte[]> pngs) throws IOException {
        try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(destination))) {
            writeShortLE(out, 0);
            writeShortLE(out, 1);
            writeShortLE(out, pngs.size());
            int offset = 6 + 16 * pngs.size();
            for (int i = 0; i < pngs.size(); i++) {
                int size = SIZES[i];
                out.writeByte(size == 256 ? 0 : size);
                out.writeByte(size == 256 ? 0 : size);
                out.writeByte(0);
                out.writeByte(0);
                writeShortLE(out, 1);
                writeShortLE(out, 32);
                writeIntLE(out, pngs.get(i).length);
                writeIntLE(out, offset);
                offset += pngs.get(i).length;
            }
            for (byte[] png : pngs) out.write(png);
        }
    }

    private static void writeShortLE(DataOutputStream out, int value) throws IOException {
        out.writeByte(value & 0xff);
        out.writeByte((value >>> 8) & 0xff);
    }

    private static void writeIntLE(DataOutputStream out, int value) throws IOException {
        out.writeByte(value & 0xff);
        out.writeByte((value >>> 8) & 0xff);
        out.writeByte((value >>> 16) & 0xff);
        out.writeByte((value >>> 24) & 0xff);
    }
}
