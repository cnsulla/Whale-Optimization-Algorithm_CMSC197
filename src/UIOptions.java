import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Font;

public class UIOptions extends generalPanel{
	private JPanel panel[];
	private JLabel bg;
  public static final int FILL_MAX = 19,
                          FILL_MIN = 0,
                          FILL_STEPS = 1;
  private static final int FILL_DEFAULT = 2;

	private String size[]={"img\\bee2\\9x9.png","img\\bee2\\16x16.png","img\\bee2\\25x25.png"};
	private String sound[]={"img\\exit\\sound\\on.png","img\\exit\\sound\\off.png"};
	private String penalty[]={"img\\bee2\\missingno.png", "img\\bee2\\sumproduct.png", "img\\bee2\\sumproductns.png"};

	protected JLabel sizeLabel, levelLabel, soundLabel, penLabel;
	protected JButton exit, no;
	protected JButton left[]=new JButton[3];
	protected JButton right[]=new JButton[3];
  private JSlider fillSize;
  private JLabel fillLabel;
  private JCheckBox startEmpty;
  private int fillPercent = 10;
  
	protected int sz, lvl, snd, num, pen;
	UIOptions(JPanel panel[]){
		this.panel=panel;
		panel[1].setOpaque(true);
		exit=addButton(panel[1], "img/exit/okay.png", "img/exit/h_okay.png",225, 480);

		for(int ctr=0; ctr<3; ctr++){
			left[ctr]=addButton(panel[1], "img/exit/left.png", "img/exit/h_left.png",356,185+70*ctr);
			right[ctr]=addButton(panel[1], "img/exit/right.png", "img/exit/h_right.png",568,185+70*ctr);
			}
		sizeLabel=addLabel(panel[1], size[0], 389,187);
		soundLabel=addLabel(panel[1], sound[0], 389,258);
		penLabel=addLabel(panel[1], penalty[0], 389,329);
    
    fillSize = new JSlider(FILL_MIN, FILL_MAX, FILL_DEFAULT);
    fillSize.setMinorTickSpacing(FILL_STEPS);
    fillSize.setPaintTicks(true);
    fillSize.setSnapToTicks(true);
    panel[1].add(fillSize);
    fillSize.setBounds(389, 400, 141, 40);
    
    fillLabel = new JLabel("10%");
    fillLabel.setFont(new Font(fillLabel.getFont().getName(), Font.PLAIN, 30));
    fillLabel.setBounds(535, 400, 80, 40);
    fillLabel.setOpaque(false);
    panel[1].add(fillLabel);
    fillSize.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        fillPercent = fillSize.getValue() * 5;
        fillLabel.setText(fillPercent + "%");
      }
    });
    
    startEmpty = new JCheckBox("");
    startEmpty.setSelected(true);
    startEmpty.setBounds(450, 480, 40, 25);
    startEmpty.setOpaque(false);
    panel[1].add(startEmpty);
    
    fillSize.setOpaque(false);
    
		bg=addLabel(panel[1],"img/bee2/options.png",100,49);
		sz=0;
    pen = 0;
		num=lvl=snd=0;
		//panel[1].setVisible(true);
		}
	protected void setSize(boolean isRight){
		if(isRight){
			sz++;
			if(sz==3)
				sz=0;
			}
		else{
			sz--;
			if(sz==-1)
				sz=2;
			}
    if (sz != 0 && pen != 0) {
      pen = 0;
      changePicture(penLabel, penalty[pen]);
    }
		changePicture(sizeLabel, size[sz]);
		}
	protected void setSound(boolean isRight){
		if(isRight){
			snd++;
			if(snd==2)
				snd=0;
			}
		else{
			snd--;
			if(snd==-1)
				snd=1;
			}
		changePicture(soundLabel, sound[snd]);
		}
  
  protected void setPenalty(boolean right) {
		if(right){
			pen++;
			if(pen>2)
				pen=0;
			}
		else{
			pen--;
			if(pen<0)
				pen=2;
	  }
    if (sz != 0) pen = 0;
		changePicture(penLabel, penalty[pen]);
  }
  
  public int getFillPercent() {
    return fillPercent;
  }
  
  public boolean startEmpty() {
    return startEmpty.isSelected();
  }
  
  public int getPenaltyType() {
    return pen;
  }
  
	protected void setVisible(boolean isVisible, int num){
		this.num=num;
		panel[1].setVisible(isVisible);
		}
	protected void decompose(){
		panel[1].removeAll();
		bg=sizeLabel=levelLabel=soundLabel=null;
		exit=no=null;
		left[0]=right[0]=left[1]=right[1]=null;
		}
	}