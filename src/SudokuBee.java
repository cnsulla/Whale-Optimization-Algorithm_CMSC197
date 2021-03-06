import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.*;
import java.awt.Container;
import java.awt.Point;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import fitnessfunc.*;

public class SudokuBee extends Thread{
  private generalPanel GP;
  private UIGame game;
  private UIExit exit;
  private UIBoard board;
  private UIStatus status;
  private UIPop pop;
  private UIOptions options;
  private UISave save;
  private UISolve solve;
  private UILoad load;
  private UIHelp help;
  private int btnX, btnY;
  private int numOnlook, numEmp, numCycle;
  private boolean isAns=false; 
  private boolean generate=true; 
  private boolean start=false;
  private boolean gameMode=true;
  private boolean isSolved=false;
  private Tunog snd, error;
  private JFrame frame=new JFrame();
  private Container container=frame.getContentPane();
  private String saveFileName="";
  private boolean ohwhatasweetfilling = false;
  private MissingValues MissingNumbersFunction;
  
  public SudokuBee(){
    frame.setTitle(" Sudoku Bee");
    snd=new Tunog("snd/1.mid");
    error=new Tunog("snd/error.wav");
    snd.loop();
    menu();
    options();
    frame.setVisible(true);
    frame.setSize(800,625);
    frame.setLocationRelativeTo(null);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    MissingNumbersFunction = new MissingValues();
  }
  
  private void menu(){
    GP=new generalPanel(container);
    GP.play.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
      mainGame();
      status("");
      isAns=true;
        //0 = 9
        //1 = 16
        //2 = 25
				//int size=(options.sz+2)*3;
        int size = options.sz == 0 ? 9 : (options.sz == 1 ? 16 : 25);
        board(new int[size][size][2], true);
        numEmp=100;
        numOnlook=200;
        numCycle=100000;
        generate=true;
        gameMode=true;
        isSolved=false;
        if (!options.startEmpty()) {
          JOptionPane.showMessageDialog(frame, "STARTING FROM AN EMPTY GRID \n" +
                  "Press \"Solve\" after inputting start squares");
          board.decompose();
          board=null;
          board(new int[size][size][2], true);
          ohwhatasweetfilling = true;
        }
        else {
          ohwhatasweetfilling = false;
          try{
            Thread t = new Thread(SudokuBee.this);
            t.start();
          } catch(Exception ee){
            start=true;
          }
        }
        popUp(size);
      } 
    });
    
    GP.open.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        GP.setVisibleButton(false);
        int size = options.sz == 0 ? 9 : (options.sz == 1 ? 16 : 25);
        isSolved=false;
        board(new int[size][size][2], true);
        loadSudoku(7);
      }
    });
    GP.create.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        mainGame();
        isAns=false;
        int size = options.sz == 0 ? 9 : (options.sz == 1 ? 16 : 25);
        isSolved=false;
        board(new int[size][size][2], true);
        game.setVisible(false);
        status("create");
        popUp(size);
      }
    });
    GP.options.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        GP.setVisibleButton(false);
        options.setVisible(true, 0);
      }
    });
    GP.help.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        help(7);
      }
    });
    GP.exit.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        GP.setVisibleButton(false);
        exit(0);
      }
    });
  }
  
  private void loadSudoku(int num){
    GP.setVisible(num);
    load=new UILoad(GP.solve);
    load.lists.grabFocus();
    final int number=num;
    try{
      status.setVisible(false);
    } catch(Exception e) {
      //nothing
    }
    load.cancel.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        try{
          game.setVisible(true);
          status.setVisible(true);
        } catch(Exception ee){
          //nothing
        }
        GP.setVisibleButton(true);
        load.decompose();
        load=null;
        GP.setVisible(number);
      }
    });
    load.load.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        open(load.lists.getSelectedValue()+"");
        load.decompose();
        load=null;
      }
    });
  }
  
  private void open(String str){
    LoadSudoku sod=new LoadSudoku("save/"+str+".sav");
    if(sod.getStatus()){
      board.decompose();
      try{
        game.decompose();
      } catch(Exception e){
        //nothing
      }
      mainGame();
      status("");
      isAns=true;
      board=null;
      board(sod.getArray(), false);
      popUp(sod.getSize());
    } else{
      exit(3);
    }
    sod=null;
  }
  
  private void board(int sudokuArray[][][], boolean isNull){
    GP.setVisible(5);
    board=new UIBoard(sudokuArray, isNull, GP.panel[5]);
    int size=board.getSize();
    board.getBoardGraphics().addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (!isSolved && e.getModifiers() == 4) {
          Point p = board.getBoardGraphics().getXY(e.getX(), e.getY());
          if (p.x >= 0 && p.y >= 0) {
            pop.setVisible(true, p.x, p.y, board.getValue(p.x, p.y));
            status.setVisible(false);
            game.setVisible(false);
          }
        }
      }
    });
    /*
		for(btnX=0; btnX<size; btnX++){
			for(btnY=0; btnY<size; btnY++){
				if(board.getStatus(btnX, btnY)!=0){
					final int x=btnX;
					final int y=btnY;
					board.btn[btnX][btnY].addMouseListener(new MouseAdapter(){
						public void mouseClicked(MouseEvent e){
							if(!isSolved && e.getModifiers()==4){
								pop.setVisible(true, x, y, board.getValue(x, y));
								status.setVisible(false);
								game.setVisible(false);
								}
							}
						});
					}
				}
			}
      */
  }

  private void popUp(int size){
    try{
      pop.decompose();
      pop=null;
    } catch(Exception e){
      //nothing
    }
    pop=new UIPop(size, GP.panel[3]);
    for(int ctr=0; ctr<size; ctr++){
      final int popCounter = ctr+1;
      pop.btn[ctr].addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          int size=pop.size;
          //GP.changePicture(board.btn[pop.btnX][pop.btnY],"img/box/"+size+"x"+size+"/normal/"+popCounter+".png");
          if (ohwhatasweetfilling)
            board.setSudokuArray(popCounter, false,pop.btnX, pop.btnY);
          else
            board.setSudokuArray(popCounter, isAns,pop.btnX, pop.btnY);
          pop.field.setForeground(java.awt.Color.black);
          pop.setVisible(false,0,0,0);
          status.setVisible(true);
          game.setVisible(isAns);
          if(isAns && board.getAns()==size*size){
            int sudoku[][][]=board.getSudokuArray();
            Subgrid subgrid[]=new Subgrid[sudoku.length];
            int subDimY=(int)Math.sqrt(sudoku.length);
            int subDimX=sudoku.length/subDimY;
            for(int ctr=0, xCount=0; ctr<sudoku.length; ctr++, xCount++){
              subgrid[ctr]=new Subgrid(xCount * subDimX, 
                      ((ctr/subDimY)*subDimY), subDimX, subDimY);
              if((ctr+1)%subDimY==0 && ctr>0)
                xCount=-1;
            }
            if(new Validator(sudoku, subgrid).checkAnswer())
              exit(5);
          }
        }
      });
    }
    pop.field.addKeyListener(new KeyListener(){
      public void keyReleased(KeyEvent eee){
        String str=pop.field.getText();
        if(str.length()>2 || !(eee.getKeyCode()>47 && eee.getKeyCode()<58 || eee.getKeyCode()>95 && eee.getKeyCode()<106 || eee.getKeyCode()==KeyEvent.VK_BACK_SPACE || eee.getKeyCode()==KeyEvent.VK_ENTER) ){
          try{
            pop.field.setText(str.substring(0,str.length()-1));
          } catch(Exception ee){
            //nothing
          }
        }
        else if(eee.getKeyCode()>47 && eee.getKeyCode()<58 || 
                eee.getKeyCode()>95 && eee.getKeyCode()<106 || 
                eee.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
          try{
            if(Integer.parseInt(str)>pop.size)
              pop.field.setForeground(java.awt.Color.red);
            else
              pop.field.setForeground(java.awt.Color.black);
          }
          catch(Exception e){
            //nothing
          }
        }
      }
      public void keyTyped(KeyEvent eee){
        
      }
      public void keyPressed(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
        String str=pop.field.getText();
          if(str.length()==0){
            //GP.changePicture(board.btn[pop.btnX][pop.btnY],"img/box/"+pop.size+"x"+pop.size+"/normal/0.png");
            board.setSudokuArray(0, false,pop.btnX, pop.btnY);
            pop.setVisible(false,0,0,0);
            status.setVisible(true);
            game.setVisible(isAns);
          } else{
            try{
              int size=pop.size, num=Integer.parseInt(str);
              if(num<=size && num>=1) {
                //GP.changePicture(board.btn[pop.btnX][pop.btnY],"img/box/"+size+"x"+size+"/normal/"+num+".png");
                if (ohwhatasweetfilling)
                  board.setSudokuArray(num, false,pop.btnX, pop.btnY);
                else
                  board.setSudokuArray(num, isAns,pop.btnX, pop.btnY);
                pop.setVisible(false,0,0,0);
                status.setVisible(true);
                game.setVisible(isAns);
                pop.field.setForeground(java.awt.Color.black);
                if(isAns && board.getAns()==size*size){
                  int sudoku[][][]=board.getSudokuArray();
                  Subgrid subgrid[]=new Subgrid[sudoku.length];
                  int subDimY=(int)Math.sqrt(sudoku.length);
                  int subDimX=sudoku.length/subDimY;
                  for(int ctr=0, xCount=0; ctr<sudoku.length; ctr++, xCount++){
                    subgrid[ctr]=new Subgrid(xCount*subDimX,
                            ((ctr/subDimY)*subDimY), subDimX, subDimY);
                    if((ctr+1)%subDimY==0 && ctr>0)
                      xCount=-1;
                  }
                  if(new Validator(sudoku, subgrid).checkAnswer())
                    exit(5);
                }
              } else {
                throw new Exception();
              }
            } catch(Exception eee){
              error.play();
            }
          }
        }
      }
    });
    pop.erase.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        //GP.changePicture(board.btn[pop.btnX][pop.btnY],"img/box/"+pop.size+"x"+pop.size+"/normal/0.png");
        board.setSudokuArray(0, false,pop.btnX, pop.btnY);
        pop.setVisible(false,0,0,0);
        status.setVisible(true);
        game.setVisible(isAns);
      }
    });
    pop.cancel.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        pop.setVisible(false,0,0,0);
        status.setVisible(true);
        game.setVisible(isAns);
      }
    });
  }
    
  private void mainGame(){
    GP.setVisible(6);
    game=new UIGame(GP.panel[6]);
    game.newGame.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
          if (options.startEmpty()) {
            game.setVisible(false);
            status.setVisible(false);
            isSolved=false;
            ohwhatasweetfilling = false;
            exit(2);
          }
          else {
            JOptionPane.showMessageDialog(frame, "STARTING FROM AN EMPTY GRID \n" +
                            "Press \"Solve\" after inputting start squares");
            board.decompose();
            board=null;
            int size = options.sz == 0 ? 9 : options.sz == 1 ? 16 : 25;
            board(new int[size][size][2], true);
            ohwhatasweetfilling = true;
            popUp(size);
          }
      }
    });
    game.exit.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        game.setVisible(false);
        status.setVisible(false);
        exit(1);
      }
    });
    game.options.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        game.setVisible(false);
        status.setVisible(false);
        options.setVisible(true,1);
      }
    });
    game.solve.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        int ccc = SudokuChecker.check(board.getSudokuArray(), options.getFillPercent());
        if (ohwhatasweetfilling && ccc > 0) {
          if (ccc == 1)
            JOptionPane.showMessageDialog(frame, "Invalid Sudoku, Please enter a valid Sudoku");
          else {
            int ln = board.getSudokuArray().length;
            ln *= ln;
            ln = (int)(ln * (options.getFillPercent() * 0.01));
            JOptionPane.showMessageDialog(frame, "Too many start squares!" +
                                  "\nOnly " + ln + " start squares allowed");
          }
        }
        else {
          ///hahahahahaahahahahahahah
          //watashi shotai fumei lady
          //down town utatteru
          //shalalalalala
          //dare mo kidzukanai
          if (ohwhatasweetfilling) 
            board.setSudoku(SudokuChecker.prepareForTheThingToHappen(board.getSudokuArray()));
          game.setVisible(false);
          status.setVisible(false);
          game.solve.setEnabled(false);
          solve();
          game.solve.setEnabled(true);
        }
      }
    });
    game.help.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        help(5);
      }
    });
  }
  
  private void help(int num){
    GP.setVisible(0);
    help=new UIHelp(GP.panel[0], num);
    help.next.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        help.increase();
      }
    });
    help.back.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        help.decrease();
      }
    });
    help.cancel.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        help.decompose();
        GP.setVisible(help.panelNum);
        help=null;
      }
    });
  }
  
  private void solve(){
    solve=new UISolve(GP.solve);
    solve.cancel.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        status.setVisible(true);
        game.setVisible(true);
        solve.decompose();
        solve=null;
        GP.setVisible(5);
      }
    });
    solve.mode.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        solve.changeMode();
      }
    });
    solve.solve.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        status.setVisible(false);
        try{
          numEmp=Integer.parseInt(solve.numEmployed.getText());
          numOnlook=Integer.parseInt(solve.numOnlook.getText());
          numCycle=Integer.parseInt(solve.numCycles.getText());
          generate=false;
          status.setVisible(false);
          if(numEmp>=numOnlook || numEmp<2)
            throw new Exception();
          if(solve.modeNum==0)
            gameMode=true;
          else
            gameMode=false;
          try{
            //change
            Thread t = new Thread(SudokuBee.this);
            t.start();
          } catch(Exception ee){
            start=true;
          }
        } catch(Exception ee){
          solve.decompose();
          solve=null;
          GP.setVisible(5);
          exit(7);
        }
      }
    });
  }
  
  public void run(){
    while(true){
      try{
        solve.decompose();
        solve=null;
      } catch(Exception e){
        //nothing
      }
      game.setVisible(1);
      if(gameMode){
        status.setVisible(false);
        PrintResult printer=new PrintResult("results/.xls");
        int sudoku[][][]=board.getSudokuArray();
        //System.out.println("whats hah");
        int width = sudoku.length;
        int objFunct = options.getPenaltyType();        
        ObjectiveFunction func;
        if(objFunct == 0)
          func = new MissingValues();
        else if(objFunct == 1)
          func = new SumProduct();
        else
          func = new SumProductNoConstraint();
        WOA woa = new WOA(sudoku, func, width, numEmp, numCycle);
        /*
         * WOA woa = new WOA(printer, sudoku, numEmp,
         *                 numOnlook, numCycle, options.getPenaltyType());
         */
        Animation animate=new Animation(sudoku, GP.special);
        board.decompose();
        board=null;
        Thread Twoa = new Thread(woa);
        Twoa.start();
        delay(100);
        while(!woa.isDone()){
          delay(100);
          /*
           * System.out.println(woa.getCycles());
           * System.out.println("a " + woa.getAVal());
           * System.out.println("fit " + woa.getFitness());
           */
          animate.changePic(woa.getBestSolution());
        }
        animate.decompose();
        animate=null;
        if(generate){
          GenerateSudoku gen = new GenerateSudoku(woa.getBestSolution(), 
                                      options.getFillPercent());
          board(gen.getSudoku(), false);
          gen=null;
          isSolved=false;
          woa=null;
        }
        else if (ohwhatasweetfilling) {
            ohwhatasweetfilling = false;
            int[][][] ns = SudokuChecker.dotheThing(woa.getBestSolution(), options.getFillPercent());
            board(ns, false);
            isSolved=false;
            woa=null; //why tho
            JOptionPane.showMessageDialog(frame, "Sudoku Generated");
        }
        else{
          if(woa.getFitness()==1){
            exit(8);
            board=new UIBoard(woa.getBestSolution(), GP.panel[5]);
          }
          else{
            board(woa.getBestSolution(), false);
            isSolved=false;
          }
          woa=null;
        }
        printer.close();
        printer.delete();
        printer=null;
        status.setVisible(true);
      }
      else{
        String file="results/result.xls";
        PrintResult printer=new PrintResult(file);
        status.setVisible(false);
        String cycle="", time="";
        /*
         * ABC abc=new ABC(printer, board.getSudokuArray(),
         *      numEmp,numOnlook, numCycle, options.getPenaltyType());
         */
        int width = board.getSudokuArray().length;
        int objFunct = options.getPenaltyType();        
        ObjectiveFunction func;
        if(objFunct == 0)
          func = new MissingValues();
        else if(objFunct == 1)
          func = new SumProduct();
        else
          func = new SumProductNoConstraint();
        
        WOA woa = new WOA(board.getSudokuArray(), func, width, numEmp, numCycle);
        Thread Twoa = new Thread(woa);
        Twoa.start();
        double startTime=printer.getTime();
        while(!woa.isDone()) {
          //wait
        }
        double end=(printer.getTime());
        double seconds=((end-startTime)/1000);
        printer.print("\nCycles:\t "+woa.getCycles()+"\nTime:\t"+seconds);
        printer.close();
        printer=null;
        game.setVisible(0);
        Runtime rt=Runtime.getRuntime();
        try{
          rt.exec("rundll32 SHELL32.DLL,ShellExec_RunDLL "+file);
        } catch (Exception ee){
          //nothing
        }
        board.decompose();
        board=null;
        if(woa.getFitness()==1){
          exit(8);
          board=new UIBoard(woa.getBestSolution(), GP.panel[5]);
        }
        else{
          board(woa.getBestSolution(), false);
          isSolved=false;
        }
        woa.decompose();
        woa=null;
        rt=null;
        status.setVisible(true);
      }
      game.setVisible(0);
      start=false;
      while(!start);
    }
  }
  
  protected void delay(int newDelay){
    try{
      sleep(newDelay);
    } catch(InterruptedException err){
      //nothign
    }
  }
  
  private void status(String str){
    status=new UIStatus(str, GP.panel[4]);
    status.yes.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        int sudoku[][][]=board.getSudokuArray();
        Subgrid subgrid[]=new Subgrid[sudoku.length];
        int subDimY=(int)Math.sqrt(sudoku.length);
        int subDimX=sudoku.length/subDimY;
        for(int ctr=0, xCount=0; ctr<sudoku.length; ctr++, xCount++){
          subgrid[ctr]=new Subgrid(xCount*subDimX, ((ctr/subDimY)*subDimY),
                  subDimX, subDimY);
          if((ctr+1)%subDimY==0 && ctr>0)
            xCount=-1;
        }
        Validator val=new Validator(sudoku, subgrid);
        if(val.checkValidity()){
          isAns=true;
          status.decompose();
          status=null;
          pop.decompose();
          pop=null;
          board.decompose();
          board=null;
          board(sudoku,false);
          game.setVisible(true);
          popUp(sudoku.length);
          status("");
        }
        else {
          exit(4);
        }
        val=null;
        isSolved=false;
      }
    });
    status.no.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        exit(1);
      }
    });
    status.open.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        game.setVisible(false);
        status.setVisible(false);
        isSolved=false;
        loadSudoku(5);
      }
    });
    status.save.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        game.setVisible(false);
        status.setVisible(false);
        save();
      }
    });
    status.reset.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        game.setVisible(false);
        status.setVisible(false);
        exit(10);
      }
    });
  }
  
  private void save(){
    save=new UISave(GP.panel[2]);
    save.field.grabFocus();
    status.setVisible(false);
    game.setVisible(false);
    save.cancel.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        game.setVisible(true);
        status.setVisible(true);
        save.decompose();
        GP.setVisible(5);
      }
    });
    save.save.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        saveFileName=save.field.getText();
        SaveSudoku saving=new SaveSudoku();
        int num=saving.save(saveFileName, board.getSudokuArray());
        save.decompose();
        GP.setVisible(5);
        if(saveFileName.length()>0 && !(saveFileName.contains("/") ||
                saveFileName.contains("\\") || saveFileName.contains(":") || 
                saveFileName.contains("*") || saveFileName.contains("?") || 
                saveFileName.contains("\"") || saveFileName.contains("<") || 
                saveFileName.contains(">"))&& num==0){
          saveFileName="";
          game.setVisible(true);
          status.setVisible(true);
        }
        else if(saveFileName.length()==0 && (saveFileName.contains("/") || saveFileName.contains("\\") || saveFileName.contains(":")  || saveFileName.contains("*") || saveFileName.contains("?")  || saveFileName.contains("\"") || saveFileName.contains("<") || saveFileName.contains(">")) ||  num==1){
          exit(6);
          status.setVisible(false);
          game.setVisible(false);
          saveFileName="";
        }
        else{
          status.setVisible(false);
          game.setVisible(false);
          exit(9);
        }
      }
    });
    save.field.addKeyListener(new KeyListener(){
      public void keyReleased(KeyEvent ee){}
      public void keyTyped(KeyEvent eee){}
      public void keyPressed(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
          saveFileName=save.field.getText();
          SaveSudoku saving=new SaveSudoku();
          int num=saving.save(saveFileName, board.getSudokuArray());
          save.decompose();
          GP.setVisible(5);
          if(saveFileName.length()>0 && !(saveFileName.contains("/") ||
                  saveFileName.contains("\\") || saveFileName.contains(":") ||
                  saveFileName.contains("*") || saveFileName.contains("?") ||
                  saveFileName.contains("\"") || saveFileName.contains("<") || 
                  saveFileName.contains(">"))&& num==0){
            game.setVisible(true);
            status.setVisible(true);
            saveFileName="";
          }
          else if(saveFileName.length()==0 && 
                  (saveFileName.contains("/") || 
                          saveFileName.contains("\\") || 
                          saveFileName.contains(":")  || 
                          saveFileName.contains("*") || 
                          saveFileName.contains("?")  || 
                          saveFileName.contains("\"") || 
                          saveFileName.contains("<") || 
                          saveFileName.contains(">")) || 
                          num==1){
            exit(6);
            status.setVisible(false);
            game.setVisible(false);
            saveFileName="";
          }
          else{
            status.setVisible(false);
            game.setVisible(false);
            exit(9);
          }
        }
      }
    });
  }
  
  private void exit(int num){
    if(exit==null){
      exit=new UIExit(GP.panel[0], num);
      exit.yes.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          GP.setVisibleButton(true);
          try{
            game.setVisible(true);
            status.setVisible(true);
          } catch(Exception ee){
            //nothing
          }
          exit.decompose();
          if(exit.num==0)
          System.exit(0);
          else if(exit.num==1){
            board.decompose();
            board=null;
            game.decompose();
            game=null;
            status.decompose();
            status=null;
            pop.decompose();
            pop=null;
            GP.setVisible(7);
          }
          else if(exit.num==2){
            board.decompose();
            board=null;
            game.decompose();
            game=null;
            status.decompose();
            status=null;
            pop.decompose();
            pop=null;
            mainGame();
            status("");
            isAns=true;
            int size = options.sz == 0 ? 9 : (options.sz == 1 ? 16 : 25);
            board(new int[size][size][2], true);
              
            numEmp=100;
            numOnlook=200;
            numCycle=100000000;
            generate=true;
            gameMode=true;
            try{
                //change
                Thread t = new Thread(SudokuBee.this);
                t.start();
            } catch(Exception ee){
              start=true;
            }
              popUp(size);
          }
          else if(exit.num==9){
            GP.setVisible(5);
            SaveSudoku saving=new SaveSudoku();
            saving.delete(saveFileName);
            saving.save(saveFileName, board.getSudokuArray());
          }
          else if(exit.num==10){
            for (int i = 0; i < board.getSudokuArray().length; ++i) {
              for (int j = 0; j < board.getSudokuArray().length; ++j) {
                if (board.getSudokuArray()[i][j][1] != 0)
                  board.getSudokuArray()[i][j][0] = 0;
              }
            }
            isSolved=false;
            GP.setVisible(5);
            board.changePic();
            int[][][] sudoku=board.getSudokuArray();
            board.decompose();
            board=null;
            board(sudoku, false);
          }
          exit=null;
        }
      });
      exit.no.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          GP.setVisibleButton(true);
          try{
            game.setVisible(true);
            status.setVisible(true);
          } catch(Exception ee){
            //nothing
          }
          /*
          exit.decompose();
          if(exit.num==0)
            GP.setVisible(7);
          else if(exit.num==1 || exit.num==2 || exit.num==6 || exit.num==10)
            GP.setVisible(5);
          else if(exit.num==9){
            GP.setVisible(5);
            save();
          } */
          exit.decompose();
          switch(exit.num) {
            case 0:
              GP.setVisible(7);
              break;
            case 1: case 2: case 6: case 10:
              GP.setVisible(5);
              break;
            case 9:
              GP.setVisible(5);
              save();
              break;
          }
          exit=null;
        }
      });
      exit.okay.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          GP.setVisibleButton(true);
          game.setVisible(true);
          status.setVisible(true);
          if(exit.num==7)
            solve();
          else if(exit.num==5){
            GP.setVisible(5);
            isSolved=true;
            board.changeCursor();
          }
          else if(exit.num==8){
            GP.setVisible(5);
            isSolved=true;
          }
          else if(exit.num!=4 && exit.num!=6){
            board.decompose();
            board=null;
            GP.setVisible(7);
          }
          else if(exit.num==4){
            GP.setVisible(5);
            game.setVisible(false);
          }
          else{
            GP.setVisible(5);
          }
          exit.decompose();
          exit=null;
        }
      });
    }
  }
  
  private void options(){
    options=new UIOptions(GP.panel);
    options.exit.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        try{
          game.setVisible(true);
          status.setVisible(true);
        } catch(Exception ee){
          //nothing
        }
        GP.setVisibleButton(true);
        if(options.num==0)
          GP.setVisible(7);
        else
          GP.setVisible(5);
      }
    });
    options.left[0].addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        options.setSize(false);
      }
    });
    options.left[1].addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        options.setSound(false);
        if(options.snd==1)
          snd.stop();
        else
          snd.loop();
      }
    });
    options.left[2].addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        options.setPenalty(false);
      }
    });
    options.right[0].addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        options.setSize(true);
      }
    });
    options.right[1].addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        options.setSound(true);
        if(options.snd==1)
          snd.stop();
        else
          snd.loop();
        }
    }); 
    options.right[2].addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        options.setPenalty(true);
      }
    });
  }
  
  private void sop(Object obj){
    System.out.println(obj+"");
  }
  
  public static void main(String args[]){
    SudokuBee app=new SudokuBee();
  }
}