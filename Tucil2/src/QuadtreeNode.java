public class QuadtreeNode {
    int startX, startY, width, height;
    double error;
    QuadtreeNode[] children;
    boolean isLeaf;
    int avgR, avgG, avgB;
    public boolean expanded = false;

    public QuadtreeNode(int startX, int startY, int width, int height){
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.isLeaf = false;
        this.children = null;
    }
    
}
