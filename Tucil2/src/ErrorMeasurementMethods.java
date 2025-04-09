public class ErrorMeasurementMethods {
    public static double computeVariance(int[][][] block){
        int height = block.length;
        int width = block[0].length;
        int N = height * width;
        double[] sum = new double[3];

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                for (int c = 0; c < 3; c++){
                    sum[c] += block[i][j][c];
                }
            }
        }

        double[] mean = new double[3];
        for (int c = 0; c < 3; c++){
            mean[c] = sum[c] / N;
        }

        double[] variance = new double[3];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                for (int c = 0; c < 3; c++){
                    variance[c] += Math.pow(block[i][j][c] - mean[c], 2);
                }
            }
        }

        for (int c = 0; c < 3; c++){
            variance[c] /= N;
        }

        double combinedVariance = (variance[0] + variance[1] + variance[2])/3;
        return combinedVariance;
    }
    
    public static double computeMAD(int[][][] block){
        int height = block.length;
        int width = block[0].length;
        int N = height * width;
        double[] sum = new double[3];

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                for (int c = 0; c < 3; c++){
                    sum[c] += block[i][j][c];
                }
            }
        }

        double[] mean = new double[3];
        for (int c = 0; c < 3; c++){
            mean[c] = sum[c] / N;
        }

        double[] mad = new double[3];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                for (int c = 0; c < 3; c++){
                    mad[c] += Math.abs(block[i][j][c] - mean[c]);
                }
            }
        }

        for (int c = 0; c < 3; c++){
            mad[c] /= N;
        }

        double combinedMAD = (mad[0] + mad[1] + mad[2])/3;
        return combinedMAD;   
    }

    public static double computeMaxPixelDiffenrece(int[][][] block){
        int height = block.length;
        int width = block[0].length;
        int[] maxVal = {0,0,0};
        int[] minVal = {255,255,255};

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                for (int c = 0; c < 3; c++){
                    if (block[i][j][c] < minVal[c]){
                        minVal[c] = block[i][j][c];
                    }
                    if (block[i][j][c] > maxVal[c]){
                        maxVal[c] = block[i][j][c];
                    }
                }
            }
        }

        double[] diff = new double[3];
        for (int c = 0; c < 3; c++){
            diff[c] = maxVal[c] - minVal[c];
        }

        double combinedDiff = (diff[0] + diff[1] + diff[2])/3;
        return combinedDiff;
    }

    public static double computeEntropy(int[][][] block){
        int height = block.length;
        int width = block[0].length;
        int N = height * width;
        double combinedEntropy = 0.0;

        for (int c = 0; c < 3; c++){
            int[] hist = new int[256];
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    int value = block[i][j][c];
                    hist[value]++;
                }
            }

            double entropy = 0.0;
            for (int k = 0; k < 256; k++){
                if (hist[k] > 0){
                    double p = (double) hist[k] / N;
                    entropy += p * (Math.log(p)/Math.log(2));
                }
            }
            entropy = -entropy;
            combinedEntropy += entropy;
        }

        combinedEntropy /= 3;
        return combinedEntropy;
    }
}
