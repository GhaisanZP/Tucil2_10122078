import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        String folderPath = "test/";
        System.out.print("Masukkan nama file gambar yang akan dikompresi: ");
        String inputFileName = scanner.nextLine();
        String inputImagePath = folderPath + inputFileName;
        System.out.println("Nama File: " + inputImagePath);
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(inputImagePath));
            if (image == null){
                System.out.println("Format gambar tidak didukung atau file tidak ditemukan");
                System.exit(1);
            }
            
        } catch (IOException e) {
            System.out.println("Gagal membaca gambar: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Gambar berhasil dimuat dengan ukuran: " +image.getWidth() + "x" + image.getHeight());

        String extension = "";
        int dotIndex = inputFileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < inputFileName.length()-1){
            extension = inputFileName.substring(dotIndex + 1);
        } else {
            System.out.println("Nama file input tidak valid, tidak terdapat ekstensi");
            System.exit(1);
        }

        System.out.println("Pilih metode perhitungan variansi");
        System.out.println("1.Variance");        
        System.out.println("2.Mean Absolute Deviation (MAD)");        
        System.out.println("3.Max Pixel Difference");        
        System.out.println("4.Entropy");        
        System.out.print("Masukkan pilihan (nomor): ");
        int errorMethod = scanner.nextInt();
        
        System.out.print("Masukkan nilai ambang batas (threshold): ");
        double threshold = scanner.nextDouble();

        System.out.print("Masukkan ukuran blok minimum: ");
        int minBlockSize = scanner.nextInt();

        System.out.print("Masukkan target persentase kompresi (nilai dari 0 sampai 1): ");
        double targetCompression = scanner.nextDouble();

        scanner.nextLine();

        System.out.print("Masukkan nama file untuk gambar hasil kompresi: ");
        String outputFileName = scanner.nextLine();
        String outputImagePath = folderPath + outputFileName;

        System.out.print("Masukkan nama file untuk file GIF (tekan Enter untuk melewati): ");
        String gifFileName = scanner.nextLine();
        String outputGifPath = "";
        if (!gifFileName.isEmpty()){
            outputGifPath = folderPath + gifFileName;
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int[][][] pixelMatrix = new int[height][width][3];
        
        for (int y=0; y < height; y++){
            for (int x=0; x < width; x++){
                int pixel = image.getRGB(x,y);
                int red = (pixel >> 16) &0xff;
                int green = (pixel >> 8) &0xff;
                int blue = pixel &0xff;
                pixelMatrix[y][x][0] = red;
                pixelMatrix[y][x][1] = green;
                pixelMatrix[y][x][2] = blue;
            }
        }
        long originalSize = new File(inputImagePath).length();
        long startTime = System.currentTimeMillis();
        
        if (targetCompression != 0){
            threshold = Bonus.CompressionUtils.adjustThreshold(pixelMatrix, width, height, originalSize, minBlockSize, errorMethod, extension, targetCompression);
        }

        QuadtreeNode root = QuadtreeCompressor.buildQuadtree(pixelMatrix, 0, 0, width, height, threshold, minBlockSize, errorMethod);
        System.out.println("Quadtree telah berhasil dibangun");

        BufferedImage compressedImage = QuadtreeCompressor.reconstructImage(root, width, height);

        try {
            File outputFile = new File(outputImagePath);
            ImageIO.write(compressedImage, extension, outputFile);
            System.out.println("Gambar hasil kompresi berhasil disimpan di: " + outputImagePath);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan gambar hasil kompresi: " + e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        long compressedSize = new File(outputImagePath).length();
        double compressionPercentage = (1-((double) compressedSize / originalSize)) * 100;

        int treeDepth = QuadtreeCompressor.getTreeDepth(root);
        int totalNodes = QuadtreeCompressor.countNodes(root);

        System.out.println("Waktu eksekusi: " + executionTime);
        System.out.println("Ukuran gambar sebelum kompresi: " + originalSize);
        System.out.println("Ukuran gambar setelah kompresi: " + compressedSize);
        System.out.println("Persentase kompresi: " + compressionPercentage);
        System.out.println("Kedalaman pohon: " + treeDepth);
        System.out.println("Banyak simpul pada pohon: " + totalNodes);

        double ssim = Bonus.CompressionUtils.computeSSIM(image, compressedImage);
        System.out.printf("Nilai SSIM: %.4f\n", ssim);

        if (!outputGifPath.isEmpty()){
            Bonus.createCompressionGIFByLevel(root, outputGifPath, width, height);
        }

        scanner.close();
    }
}

