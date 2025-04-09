
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class QuadtreeCompressor {
    public static QuadtreeNode buildQuadtree(int[][][] pixels, int startX, int startY, int width, int height, double threshold, int minBlockSize, int errorMethod){
        int[][][] block = new int[height][width][3];
        for (int i=0; i < height; i++){
            for (int j=0; j < width; j++){
                block[i][j][0] = pixels[startY + i][startX + j][0];
                block[i][j][1] = pixels[startY + i][startX + j][1];
                block[i][j][2] = pixels[startY + i][startX + j][2];
            }
        }

        double error;
        switch (errorMethod) {
            case 1:
                error = ErrorMeasurementMethods.computeVariance(block);
                break;
                case 2:
                error = ErrorMeasurementMethods.computeMAD(block);
                break;
                case 3:
                error = ErrorMeasurementMethods.computeMaxPixelDiffenrece(block);
                break;
                case 4:
                error = ErrorMeasurementMethods.computeEntropy(block);
                break;
                default:
                error = ErrorMeasurementMethods.computeVariance(block);
                break;
        }

        QuadtreeNode node = new QuadtreeNode(startX, startY, width, height);
        node.error = error;

        if (error > threshold && width > minBlockSize && height > minBlockSize &&
        (width / 2) >= minBlockSize && (height / 2) >= minBlockSize) {
        
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        // Menangani sisa piksel untuk dimensi ganjil
        int width2 = width - halfWidth;
        int height2 = height - halfHeight;
        
        node.children = new QuadtreeNode[4];
        node.children[0] = buildQuadtree(pixels, startX, startY, halfWidth, halfHeight, threshold, minBlockSize, errorMethod);
        node.children[1] = buildQuadtree(pixels, startX + halfWidth, startY, width2, halfHeight, threshold, minBlockSize, errorMethod);
        node.children[2] = buildQuadtree(pixels, startX, startY + halfHeight, halfWidth, height2, threshold, minBlockSize, errorMethod);
        node.children[3] = buildQuadtree(pixels, startX + halfWidth, startY + halfHeight, width2, height2, threshold, minBlockSize, errorMethod);
        
        node.isLeaf = false;
    } else {
        int[] avgColor = computeAverageColor(block);
        node.avgR = avgColor[0];
        node.avgG = avgColor[1];
        node.avgB = avgColor[2];
        node.isLeaf = true;
    }
        return node;
    }

    public static int[] computeAverageColor(int[][][] block){
        int height = block.length;
        int width = block[0].length;
        long sumR = 0, sumG = 0, sumB =0;
        int totalPixels = height * width;

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                sumR += block[i][j][0];
                sumG += block[i][j][1];
                sumB += block[i][j][2];
            }
        }
        
        int avgR = (int) (sumR/totalPixels);
        int avgG = (int) (sumG/totalPixels);
        int avgB = (int) (sumB/totalPixels);
        return new int[]{avgR, avgG, avgB};
    }

    public static BufferedImage reconstructImage(QuadtreeNode root, int imageWidth, int imageHeight){
        BufferedImage reconstructed = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = reconstructed.createGraphics();
        drawNode(g, root);
        g.dispose();
        return reconstructed;
    }

    public static void drawNode(Graphics2D g, QuadtreeNode node){
        if (node.isLeaf){
            Color avgColor = new Color(node.avgR, node.avgG, node.avgB);
            g.setColor(avgColor);
            g.fillRect(node.startX, node.startY, node.width, node.height);
        }else {
            for (QuadtreeNode child : node.children){
                drawNode(g, child);
            }
        }
    }
    
    public static int getTreeDepth(QuadtreeNode node){
        if (node.isLeaf){
            return 1;
        } else {
            int maxDepth = 0;
            for (QuadtreeNode child : node.children){
                int childDepth = getTreeDepth(child);
                if (childDepth > maxDepth){
                    maxDepth = childDepth;
                }
            }
            return maxDepth + 1;
        }
    }

    public static int countNodes(QuadtreeNode node){
        if (node.isLeaf){
            return 1;
        } else {
            int count = 1;
            for (QuadtreeNode child : node.children){
                count += countNodes(child);
            }
            return count;
        }
    }
}
