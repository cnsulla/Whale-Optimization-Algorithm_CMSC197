public class ArrayUtil {
  public static int[][][] copy(int[][][] ar) {
    int[][][] out = new int[ar.length][][];
    for (int i = 0; i < ar.length; ++i) {
      out[i] = new int[ar[i].length][];
      for (int j = 0;j < ar[i].length; ++j) {
        out[i][j] = new int[ar[i][j].length];
        for (int k = 0; k < ar[i][j].length; ++k) {
          out[i][j][k] = ar[i][j][k];
        }
      }
    }
    
    return out;
  }
}