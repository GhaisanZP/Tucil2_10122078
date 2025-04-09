import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class Bonus {
    public static class CompressionUtils{
        public static long getCompressedSize(int[][][] pixelMatrix, int imageWidth, int imageHeight, double threshold, int minBlockSize, int errorMethod, String formatName){
            QuadtreeNode root = QuadtreeCompressor.buildQuadtree(pixelMatrix, 0, 0, imageWidth, imageHeight, threshold, minBlockSize, errorMethod);
            BufferedImage compressedImage = QuadtreeCompressor.reconstructImage(root, imageWidth, imageHeight);
            try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
                ImageIO.write(compressedImage, formatName, baos);
                baos.flush();
                return baos.size();
            } catch (IOException e){
                e.printStackTrace();
                return Long.MAX_VALUE;
            }
        }

        public static double adjustThreshold(int[][][] pixelMatrix, int imageWidth, int imageHeight, double originalSize, int minBlockSize, int errorMethod, String formatName, double targetCompression){
            double lower = 0.1;
            double upper = 1000.0;
            double bestThreshold = lower;
            double epsilon = 0.1;
            int maxIterations = 30;
            for (int iter = 0; iter < maxIterations; iter++){
                double mid = (lower + upper) / 2.0;
                long compSize = getCompressedSize(pixelMatrix, imageWidth, imageHeight, mid, minBlockSize, errorMethod, formatName);
                double compPerc = 1 - ((double) compSize / originalSize);
                if (Math.abs(compPerc - targetCompression) < 0.02){
                    bestThreshold = mid;
                    break;
                } else if (compPerc < targetCompression){
                    lower = mid;
                } else {
                    upper = mid;
                }
                bestThreshold = mid;
                if (upper - lower < epsilon){
                    break;
                }
            }
            System.out.println("Threshold disesuaikan menjadi: " + bestThreshold);
            return bestThreshold;
        }

        public static double computeSSIM(BufferedImage original, BufferedImage compressed){
            if (original.getWidth() != compressed.getWidth() || original.getHeight() != compressed.getHeight()){
                throw new IllegalArgumentException("Gambar harus memiliki dimensi yang sama");
            }
            int width = original.getWidth();
            int height = original.getHeight();
            double L = 255;
            double C1 = Math.pow(0.01 * L, 2);
            double C2 = Math.pow(0.03 * L, 2);
            double[] muOrig = new double[3];
            double[] muComp = new double[3];
            double[] sigmaOrig2 = new double[3];
            double[] sigmaComp2 = new double[3];
            double[] sigmaOrigComp = new double[3];
            for (int y = 0; y < height; y++){
                for (int x = 0; x < width; x++){
                    Color cOrig = new Color(original.getRGB(x,y));
                    Color cComp = new Color(compressed.getRGB(x,y));
                    muOrig[0] += cOrig.getRed();
                    muOrig[1] += cOrig.getGreen();
                    muOrig[2] += cOrig.getBlue();
                    muComp[0] += cComp.getRed();
                    muComp[1] += cComp.getGreen();
                    muComp[2] += cComp.getBlue();
                }
            }
            int N = width * height;
            for (int c = 0; c < 3; c++){
                muOrig[c] /= N;
                muComp[c] /= N;
            }
            for (int y = 0; y < height; y++){
                for (int x = 0; x < width; x++){
                    Color cOrig = new Color(original.getRGB(x,y));
                    Color cComp = new Color(compressed.getRGB(x,y));
                    for (int c = 0; c < 3; c++){
                        int origVal = (c == 0 ? cOrig.getRed() : (c == 1 ? cOrig.getGreen() : cOrig.getBlue()));
                        int compVal = (c == 0 ? cComp.getRed() : (c == 1 ? cComp.getGreen() : cComp.getBlue()));
                        sigmaOrig2[c] += Math.pow(origVal - muOrig[c], 2);
                        sigmaComp2[c] += Math.pow(compVal - muComp[c], 2);
                        sigmaOrigComp[c] += (origVal - muOrig[c]) * (compVal - muComp[c]);
                    }
                }
            }
            for (int c = 0; c < 3; c++){
                sigmaOrig2[c] /= (N-1);
                sigmaComp2[c] /= (N-1);
                sigmaOrigComp[c] /= (N-1);
            }
            double ssimTotal = 0;
            for (int c = 0; c < 3; c++){
                double ssim = ((2 * muOrig[c] * muComp[c] + C1) * (2 * sigmaOrigComp[c] + C2)) 
                / ((muOrig[c] * muOrig[c] + muComp[c] * muComp[c] + C1)
                   * (sigmaOrig2[c] + sigmaComp2[c] + C2));  
                ssimTotal += ssim;
            }
            return ssimTotal / 3.0;
        }
    }
    public static void createCompressionGIFByLevel(QuadtreeNode root, String outputGifPath, int imageWidth, int imageHeight) {
        try {
            int maxDepth = QuadtreeCompressor.getTreeDepth(root);
            
            ImageOutputStream output = new FileImageOutputStream(new File(outputGifPath));
            int delay = 500; // delay 500 ms per frame
            GifSequenceWriter gifWriter = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, delay, true);

            for (int level = 1; level <= maxDepth; level++) {
                BufferedImage frame = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = frame.createGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, imageWidth, imageHeight);

                drawQuadtreeAtLevel(g, root, 1, level);

                g.dispose();
                gifWriter.writeToSequence(frame);
            }

            gifWriter.close();
            output.close();
            System.out.println("GIF proses kompresi berhasil disimpan di: " + outputGifPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawQuadtreeAtLevel(Graphics2D g, QuadtreeNode node, int currentLevel, int targetLevel) {
        if (node == null) return;
        
        if (node.isLeaf || currentLevel >= targetLevel) {
            Color avgColor = new Color(node.avgR, node.avgG, node.avgB);
            g.setColor(avgColor);
            g.fillRect(node.startX, node.startY, node.width, node.height);
        } else {
            if (node.children != null) {
                for (QuadtreeNode child : node.children) {
                    drawQuadtreeAtLevel(g, child, currentLevel + 1, targetLevel);
                }
            }
        }
    }

    public static class GifSequenceWriter {
        private ImageWriter gifWriter;
        private ImageWriteParam imageWriteParam;
        private IIOMetadata imageMetaData;
    
        public GifSequenceWriter(ImageOutputStream outputStream, int imageType, int timeBetweenFramesMS, boolean loopContinuously) throws IOException {
            gifWriter = getWriter();
            imageWriteParam = gifWriter.getDefaultWriteParam();
            ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
            imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
    
            String metaFormatName = imageMetaData.getNativeMetadataFormatName();
            IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
    
            IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
            graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(timeBetweenFramesMS / 10));
            graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
            graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
            graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
            graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");
    
            IIOMetadataNode appEntensionsNode = getNode(root, "ApplicationExtensions");
            IIOMetadataNode appExtensionNode = new IIOMetadataNode("ApplicationExtension");
            appExtensionNode.setAttribute("applicationID", "NETSCAPE");
            appExtensionNode.setAttribute("authenticationCode", "2.0");
    
            int loop = loopContinuously ? 0 : 1;
            appExtensionNode.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)});
            appEntensionsNode.appendChild(appExtensionNode);
    
            imageMetaData.setFromTree(metaFormatName, root);
    
            gifWriter.setOutput(outputStream);
            gifWriter.prepareWriteSequence(null);
        }
    
        public void writeToSequence(RenderedImage img) throws IOException {
            gifWriter.writeToSequence(new IIOImage(img, null, imageMetaData), imageWriteParam);
        }
    
        public void close() throws IOException {
            gifWriter.endWriteSequence();
        }
    
        private static ImageWriter getWriter() throws IOException {
            Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
            if (!iter.hasNext()){
                throw new IOException("Tidak ada Image Writer untuk format GIF");
            } else {
                return iter.next();
            }
        }
    
        private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
            int nNodes = rootNode.getLength();
            for (int i = 0; i < nNodes; i++){
                if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)){
                    return (IIOMetadataNode) rootNode.item(i);
                }
            }
            IIOMetadataNode node = new IIOMetadataNode(nodeName);
            rootNode.appendChild(node);
            return node;
        }
    }
    
}
