import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.io.File;

public class BoardGraphics extends JPanel {
  private int[][][] board;
  private static final int TOPX = 6,
                           TOPY = 86,
                           WIDTH = 504,
                           HEIGHT = 504,
                           IM_LENGTH = 26;
  private int fullWidth, fullHeight;
  private BufferedImage[] im_given = new BufferedImage[IM_LENGTH];
  private BufferedImage[] im_norm = new BufferedImage[IM_LENGTH];
  private BufferedImage block1, block2;
  
  public BoardGraphics(int w, int h) {
    super(false);
    this.fullWidth = w;
    this.fullHeight = h;
    setVisible(true);
    setBackground(new Color(0,0,0,0));
    setOpaque(false);
    try {
    block1 = ImageIO.read(new File("img/box/common/block1.png"));
    block2 = ImageIO.read(new File("img/box/common/block2.png"));
      for (int i = 0; i < IM_LENGTH; ++i) {
        im_given[i] = ImageIO.read(new File("img/box/common/given/" + i + ".png"));
        im_norm[i] = ImageIO.read(new File("img/box/common/normal/" + i + ".png"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void setBoard(int[][][] board) {
    this.board = board;
  }
  
  public Point getXY(int x, int y) {
    if (board == null || board.length == 0) return new Point(-1, -1);
    int w = board.length;
    int cellw = WIDTH / w;
    int cellh = HEIGHT / w;
    
    int rx = (x - TOPX) / cellw;
    int ry = (y - TOPY) / cellh;
    
    if (rx > w) rx = -1;
    if (ry > w) ry = -1;
    
    return new Point(rx, ry);
  }
  
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (board == null || board.length == 0) return;
    int w = board.length;
    int cellw = WIDTH / w;
    int cellh = HEIGHT / w;
    int sq = (int)Math.sqrt(w);
    for (int i = 0; i < w; ++i) {
      for (int j = 0; j < w; ++j) { //j is x coord
        int x = i * cellw + TOPX;
        int y = j * cellh + TOPY;
        boolean xeven = (int)(i / sq) % 2 == 0;
        boolean yeven = (int)(j / sq) % 2 == 0;
        
        if ((xeven && yeven) || !(xeven || yeven))
          g.drawImage(block1, x, y, cellw, cellh, null);
        else 
          g.drawImage(block2, x, y, cellw, cellh, null);
        if (board[i][j][1] == 0) //given
          g.drawImage(im_given[board[i][j][0]], x, y, cellw, cellh, null);
        else 
          g.drawImage(im_norm[board[i][j][0]], x, y, cellw, cellh, null);
      }
    }
  }
}