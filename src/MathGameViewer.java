import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.KeyListener;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JOptionPane;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;

/**
 * Shows the math game window, with menu, status bar, and MathGamePanel.
 * 
 * @author Bobby
 */
public class MathGameViewer extends JFrame implements KeyListener, GameCompleteListener {

	private static final long serialVersionUID = 1L;

	MathGamePanel gamePanel;
	JLabel numberCorrectLabel;
	JLabel averageTimeLabel;
	
	JMenuBar menuBar;
	TextField mathText1, mathText2, mathText3, mathText4;
	JFrame frame;
	JPanel panel;
	String gridChoice;
	String imageChoice;
	String typeChoice;
	String baseChoice;

	public MathGameViewer() {
		
		setLayout(new BorderLayout());
		
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		statusPanel.setLayout(new GridLayout(1, 2));
		numberCorrectLabel = new JLabel();
		numberCorrectLabel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		statusPanel.add(numberCorrectLabel);
		averageTimeLabel = new JLabel();
		averageTimeLabel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		statusPanel.add(averageTimeLabel);
		add(statusPanel, BorderLayout.SOUTH);

		gamePanel = new MathGamePanel();
		add(gamePanel);

		gamePanel.addGameCompleteListener(this);

		// Creates a menu

		JMenuBar menuBar = new JMenuBar();

		// Adds sub menus to the menu. Sets Mnemonics.
		JMenu newGameMenu = new JMenu("New");

		JMenuItem newGame = new JMenuItem("New Game");
		newGame.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
		newGameMenu.add(newGame);

		JMenu editMenu = new JMenu("Edit");
		JMenu gameOptions = new JMenu("Game Options");
		editMenu.add(gameOptions);

		JMenuItem helpAction = new JMenuItem("Help");
		helpAction.setAccelerator(KeyStroke.getKeyStroke("ctrl H"));

		JMenuItem quitAction = new JMenuItem("Quit");
		quitAction.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));

		gameOptions.add(helpAction);
		gameOptions.add(quitAction);

		helpAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame,
						"\t\tHello and welcome to Ben and Bobby's math game!"
								+ "\n\n By clicking \"New game\" or \"ctrl N\" under the \"New\" Menu, you can customize the game in a variety of ways."
								+ "\n\nYou can adjust the game by:" + "\nChanging the size of the grid,"
								+ "\nChoosing a new picture to discover," + "\nUsing different math operators,"
								+ "\nOr changing the base number in each problem."
								+ "\n\nIf you ever wish to quit, simply click \"Quit\" or \"ctrl Q\" under \"Game Options\" on your keyboard."
								+ "\n\nThanks for playing, we hope you have fun!");
			}
		});
		quitAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGamePanel();
			}
		});

		// Creates a layer to display Math problems on

		menuBar.add(newGameMenu);
		menuBar.add(editMenu);

		setJMenuBar(menuBar);
		setTitle("Math Game");
		setSize(800, 550);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * New Game Panel that includes 4 button groups of JRadio Buttons for user
	 * to give input.
	 */
	public void newGamePanel() {
		final int numButtons = 3;
		panel = new JPanel();

		ArrayList<ProblemType> newTypeAdd = new ArrayList<ProblemType>();
		newTypeAdd.add(ProblemType.ADDITION);
		newTypeAdd.add(ProblemType.SUBTRACTION);

		ArrayList<ProblemType> newTypeMult = new ArrayList<ProblemType>();
		newTypeMult.add(ProblemType.MULTIPLICATION);
		newTypeMult.add(ProblemType.DIVISION);

		ButtonGroup gridButton = new ButtonGroup();
		ButtonGroup imageButton = new ButtonGroup();
		ButtonGroup typeButton = new ButtonGroup();
		ButtonGroup baseButton = new ButtonGroup();

		String[] objectGridSizes = { "2", "3", "4" };
		String[] objectImages = { "1", "2", "3" };
		String[] objectMathType = { "ADDITION and SUBTRACTION", "MULTIPLICATION AND DIVISION" };
		String[] objectBase = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };

		JRadioButton[] gridSizeOption = new JRadioButton[numButtons];
		JRadioButton[] imageOption = new JRadioButton[numButtons];
		JRadioButton[] typeOption = new JRadioButton[2];
		JRadioButton[] baseOption = new JRadioButton[11];

		Object[] optionObjects = new Object[8];

		// Radio buttons for Grid Option / button group
		for (int i = 0; i < numButtons; i++) {
			gridSizeOption[i] = new JRadioButton(objectGridSizes[i]);
			gridButton.add(gridSizeOption[i]);
		}
		gridSizeOption[0].setSelected(true);
		// Radio Buttons for Image option / button group
		for (int i = 0; i < numButtons; i++) {
			imageOption[i] = new JRadioButton(objectImages[i]);
			imageButton.add(imageOption[i]);
		}
		imageOption[0].setSelected(true);
		// Radio Buttons for Math Type / button group
		for (int i = 0; i < 2; i++) {
			typeOption[i] = new JRadioButton(objectMathType[i]);
			typeButton.add(typeOption[i]);
		}
		typeOption[0].setSelected(true);
		// Radio Buttons for Base number / button group
		for (int i = 0; i < 11; i++) {
			baseOption[i] = new JRadioButton(objectBase[i]);
			baseButton.add(baseOption[i]);
		}
		baseOption[0].setSelected(true);
	

		// Radio Buttons for Base number / button group
		for(int i = 0; i < 11; i++){
			baseOption[i] = new JRadioButton(objectBase[i]);
			baseButton.add(baseOption[i]);
		}
		baseOption[0].setSelected(true);
		JSpinner baseNumSpinner = new JSpinner(
				new SpinnerNumberModel(1, 0, 12, 1));
		

		// List of option objects to make a vertical option pane
		JLabel gridMessage = new JLabel("Grid Size: ");
		JLabel imageMessage = new JLabel("Image Selection: ");
		JLabel typeMessage = new JLabel("Math Type: ");
		JLabel baseMessage = new JLabel("Base number: ");

		optionObjects[0] = gridMessage;
		optionObjects[1] = gridSizeOption;
		optionObjects[2] = imageMessage;
		optionObjects[3] = imageOption;
		optionObjects[4] = typeMessage;
		optionObjects[5] = typeOption;
		optionObjects[6] = baseMessage;
		optionObjects[7] = baseNumSpinner;

		int choice = JOptionPane.showConfirmDialog(panel, optionObjects, "New Game", JOptionPane.OK_CANCEL_OPTION);

		// if user chose Cancel or X'd out, don't do anything
		if (choice != JOptionPane.OK_OPTION)
			return;

		for (int i = 0; i < gridSizeOption.length; i++) {
			if (gridSizeOption[i].isSelected()) {
				gridChoice = gridSizeOption[i].getText();
			}
		}
		for (int i = 0; i < imageOption.length; i++) {
			if (imageOption[i].isSelected()) {
				imageChoice = imageOption[i].getText();
			}
		}
		for (int i = 0; i < typeOption.length; i++) {
			if (typeOption[i].isSelected()) {
				typeChoice = typeOption[i].getText();
			}
		}


		for (int i = 0; i < baseOption.length; i++) {
			if (baseOption[i].isSelected()) {
				baseChoice = baseOption[i].getText();
			}
		}
		int gridSelect = Integer.parseInt(gridChoice);
		imageChoice = "image" + imageChoice + ".jpg";
		int baseSelect = (int)baseNumSpinner.getValue();
		

		BufferedImage imageSelect = null;
		try {
			imageSelect = ImageIO.read(new File(imageChoice));
		} catch (IOException e) {
			System.out.print("File not found");
		}

		if (typeChoice.equals(objectMathType[0])) {
			gamePanel.startNewGame(baseSelect, gridSelect, imageSelect, newTypeAdd);
		}
		if (typeChoice.equals(objectMathType[1])) {
			gamePanel.startNewGame(baseSelect, gridSelect, imageSelect, newTypeMult);
		}

	}

	public class GroupButtonUtils {

		public String getSelectedButtonText(ButtonGroup buttonGroup) {
			for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
				AbstractButton button = buttons.nextElement();

				if (button.isSelected()) {
					return button.getText();
				}
			}

			return null;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// do nothing

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// do nothing
	}

	@Override
	public void keyTyped(KeyEvent e) {


	}

	public void actionPerformed(ActionEvent e) {


		// do nothing

	}

	@Override
	public void gameCompleted(GameCompleteEvent e) {
		int numCorrect = 0;
		for (boolean result : e.getResults())
			if (result)
				++numCorrect;
		numberCorrectLabel.setText("Number correct: " + numCorrect
				+ " out of " + e.getResults().size());
		
		long totalTime = 0;
		for (long time : e.getNanoTimes())
			totalTime += time;
		long averageTime = totalTime / e.getNanoTimes().size();
		averageTimeLabel.setText("Average time: " + getTimeString(averageTime));
	}

	public String getTimeString(long nanos) {
		double elapsedSeconds = nanos / 1e9;
		long elapsedMinutes = (long) (elapsedSeconds / 60);
		elapsedSeconds -= elapsedMinutes * 60;

		return String.format("%02d:%05.2f", elapsedMinutes, elapsedSeconds);
	}

}