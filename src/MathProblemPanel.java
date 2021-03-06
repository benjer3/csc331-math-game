import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * Displays a cover, problem, or image, and allows the user to answer a problem
 * @author Ben
 */
public class MathProblemPanel extends JPanel implements MouseListener, KeyListener
{

	private enum PanelState
	{
		COVER,
		PROBLEM,
		IMAGE
	}
	private enum ProblemState
	{
		NORMAL,
		CORRECT,
		INCORRECT,
		INCORRECT_FINAL
	}
	
	private static final long serialVersionUID = 1L;
	
	private static final int NUMBER_MAX = 12;
	private static final Color CORRECT_COLOR = new Color(32, 176, 32);
	private static final Color INCORRECT_COLOR = Color.RED;
	
	private static Random rng = new Random();
	private Timer resultTimer = new Timer();
	private int caretIndex;
	private char currentCaret;
	private Timer caretTimer = new Timer();
	private final long caretTimerInterval = 500;
	
	private ArrayList<ProblemType> types;
	private int numFamily;
	private Image image;
	
	private ArrayList<MathProblemPanel> allPanels;
	
	private ArrayList<ProblemPanelListener> problemPanelListeners
			= new ArrayList<ProblemPanelListener>();
	
	private Color coverColor;
	
	private PanelState panelState;
	private ProblemState problemState;
	private String problem;
	private String answer;
	private final String defaultAnswer = "___"; // 3 underscores
	private int correctAnswer;
	private int tries;
	
	private long totalNanos;
	private long startNanos;
	
	/**
	 * Creates a MathProblemPanel with the given parameters.
	 * @param types The possible types of problems.
	 * @param numFamily The number family.
	 * @param image The image.
	 * @param allPanels A list of all problem panels being used.
	 */
	public MathProblemPanel(ArrayList<ProblemType> types, int numFamily, Image image,
			ArrayList<MathProblemPanel> allPanels)
	{
		this.types = types;
		this.numFamily = numFamily;
		this.image = image;
		
		this.allPanels = allPanels;
		
		coverColor = Color.GRAY;
		
		addMouseListener(this);
		addKeyListener(this);
		
		panelState = PanelState.COVER;
		resetProblem();
	}
	
	/**
	 * Gets the problem as a String.
	 * @return The problem.
	 */
	public String getProblem()
	{
		return problem;
	}
	
	/**
	 * Sets or resets the problem randomly using the problem type(s) and number family.
	 */
	public void resetProblem()
	{
		ProblemType type = types.get(rng.nextInt(types.size()));
		
		int firstNum = -1;
		int secondNum = -1;
		String sign = null;
		
		int rngNum;
		
		switch (type)
		{
			case ADDITION:
				rngNum = rng.nextInt(NUMBER_MAX + 1);
				
				sign = "+";
				firstNum = rngNum;
				secondNum = numFamily;
				
				correctAnswer = firstNum + secondNum;
				break;
			case SUBTRACTION:
				rngNum = rng.nextInt(NUMBER_MAX + 1);
				
				sign = "-";
				if (rngNum > numFamily)
				{
					firstNum = rngNum;
					secondNum = numFamily;
				}
				else
				{
					firstNum = numFamily;
					secondNum = rngNum;
				}
				
				correctAnswer = firstNum - secondNum;
				break;
			case MULTIPLICATION:
				rngNum = rng.nextInt(NUMBER_MAX + 1);
				
				sign = "�";
				firstNum = rngNum;
				secondNum = numFamily;
				
				correctAnswer = firstNum * secondNum;
				break;
			case DIVISION:
				rngNum = rng.nextInt(NUMBER_MAX) + 1; // can't be 0
				
				sign = "�";
				if (numFamily == 0)
				{
					firstNum = 0;
					secondNum = rngNum;
				}
				else
				{
					firstNum = numFamily * rngNum;
					secondNum = numFamily;
				}
				
				correctAnswer = firstNum / secondNum;
				break;
		}
		
		problem = String.format("%d %s %d =", firstNum, sign, secondNum);
		answer = defaultAnswer;
		tries = 0;
		problemState = ProblemState.NORMAL;

		caretIndex = 2;
		
		totalNanos = 0;
		startNanos = System.nanoTime();
		
		repaint();
	}
	
	/**
	 * Sets the image.
	 * @param image The image.
	 */
	public void setImage(Image image)
	{
		this.image = image;
	}
	
	/**
	 * Paints the cover, problem, or image depending on the state.
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		switch (panelState)
		{
			case COVER:
				g2.setColor(coverColor);
				g2.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
				g2.setColor(Color.BLACK);
				g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				break;
			case PROBLEM:
				// resize the font according the dimension smaller than the ratio 16:9
				int fontSize;
				if (((double)getWidth() / getHeight()) < (16.0 / 9))
					fontSize = getWidth() / 8;
				else
					fontSize = (int)(getHeight() / 4.5);
				
				g2.setFont(new Font("Courier New", Font.PLAIN, fontSize));
				
				int problemWidth = g2.getFontMetrics().stringWidth(problem);
				int stringHeight = g2.getFontMetrics().getHeight();
				stringHeight = stringHeight * 6 / 5;
				int problemX = (getWidth() - problemWidth) / 2;
				int problemY = (getHeight() / 2) - (stringHeight * 5 / 6);
				int answerY = problemY + stringHeight;
				
				g2.drawString(problem, problemX, problemY);
				
				if (problemState == ProblemState.INCORRECT_FINAL)
				{
					g2.setColor(INCORRECT_COLOR);
				}
				else
				{
					g2.setColor(Color.BLACK);
				}
				
				int answerWidth = g2.getFontMetrics().stringWidth(answer);
				int charWidth = answerWidth / answer.length();
				int spaceWidth = (int)(charWidth * 0.2);
				answerWidth = charWidth * answer.length()
						+ spaceWidth * (answer.length() - 1);
				int answerX = (getWidth() - answerWidth) / 2;
				
				for (int i = 0; i < answer.length(); ++i)
				{
					g2.drawString(answer.charAt(i) + "", answerX, answerY);
					answerX += charWidth + spaceWidth;
				}

				if (problemState != ProblemState.NORMAL)
				{
					String lastLine = null;
					switch (problemState)
					{
						case CORRECT:
							lastLine = "Correct!";
							g2.setColor(CORRECT_COLOR);
							break;
						case INCORRECT:
							lastLine = "Incorrect";
							g2.setColor(INCORRECT_COLOR);
							break;
						case INCORRECT_FINAL:
							lastLine = "(" + correctAnswer + ")";
							g2.setColor(CORRECT_COLOR);
							break;
						default:
							// do nothing
							break;
					}
					
					int lastLineWidth = g2.getFontMetrics().stringWidth(lastLine);
					int lastLineX = (getWidth() - lastLineWidth) / 2;
					int lastLineY = answerY + stringHeight;
					
					g2.drawString(lastLine, lastLineX, lastLineY);
				}
				
				g2.setColor(Color.BLACK);
				g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				break;
			case IMAGE:
				Image img = image.getScaledInstance(getWidth(), getHeight(),
						Image.SCALE_AREA_AVERAGING);
				g2.drawImage(img, 0, 0, (ImageObserver) this);
				break;
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		// do nothing
	}
	/**
	 * Processes the cursor entering the panel.
	 */
	@Override
	public void mouseEntered(MouseEvent e)
	{
		coverColor = Color.DARK_GRAY;
		
		for (MathProblemPanel panel : allPanels)
			if (panel != this && panel.panelState == PanelState.COVER)
			{
				panel.coverColor = Color.GRAY;
				panel.repaint();
			}
		
		repaint();
	}
	/**
	 * Processes a mouse leaving the panel.
	 */
	@Override
	public void mouseExited(MouseEvent e)
	{
		coverColor = Color.GRAY;
		repaint();
	}
	/**
	 * Process the mouse being pressed in the panel.
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (panelState == PanelState.COVER)
		{
			for (MathProblemPanel panel : allPanels)
			{
				if (panel.panelState == PanelState.PROBLEM
						&& panel.problemState != ProblemState.INCORRECT_FINAL)
				{
					long currentNanos = System.nanoTime();
					long elapsedNanos = currentNanos - panel.startNanos;
					panel.totalNanos += elapsedNanos;
					
					panel.panelState = PanelState.COVER;
					panel.repaint();
				}
			}
			
			panelState = PanelState.PROBLEM;

			currentCaret = '_';
			caretTimer.schedule(new CaretTask(), caretTimerInterval);
			
			repaint();
			
			startNanos = System.nanoTime();
		}

		grabFocus();
	}
	@Override
	public void mouseReleased(MouseEvent e)
	{
		// do nothing
	}
	
	/**
	 * Processes a key being pressed.
	 */
	@Override
	public void keyPressed(KeyEvent arg0)
	{
		if (panelState == PanelState.PROBLEM
				&& problemState != ProblemState.INCORRECT_FINAL)
		{
			// if the key was backspace and the answer is not empty
			if (arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE
					&& caretIndex != 2)
			{
				++caretIndex;
				
				// remove the first digit
				answer = defaultAnswer.substring(0, caretIndex) 
						+ currentCaret
						+ answer.substring(caretIndex, answer.length() - 1);
			}
			else if (arg0.getKeyCode() == KeyEvent.VK_ENTER
					&& !answer.endsWith(currentCaret + ""))
			{
				// make all panels unresponsive to input
				for (MathProblemPanel panel : allPanels)
				{
					panel.removeMouseListener(panel);
					panel.removeKeyListener(panel);
				}
				
				// increment tries
				++tries;
				
				int myAnswer = Integer.parseInt(answer.substring(caretIndex + 1));
				if (myAnswer == correctAnswer)
				{
					totalNanos += System.nanoTime() - startNanos;
					
					problemState = ProblemState.CORRECT;
					
					resultTimer.schedule(new TimerTask()
					{
						@Override
						public void run()
						{
							panelState = PanelState.IMAGE;
							repaint();
							
							// restore input
							for (MathProblemPanel panel : allPanels)
							{
								panel.addMouseListener(panel);
								panel.addKeyListener(panel);
							}
						}
					}, 1500);
					
					for (ProblemPanelListener listener : problemPanelListeners)
						listener.problemCompleted(new ProblemPanelEvent(this, true, tries, totalNanos));
				}
				else
				{
					totalNanos += System.nanoTime() - startNanos;
					
					if (tries >= 2)
					{
						problemState = ProblemState.INCORRECT_FINAL;
						
						answer = answer.replace(" ", "_");
						
						// restore input
						for (MathProblemPanel panel : allPanels)
						{
							panel.addMouseListener(panel);
							panel.addKeyListener(panel);
						}
						
						for (ProblemPanelListener listener : problemPanelListeners)
							listener.problemCompleted(new ProblemPanelEvent(this, false, tries, totalNanos));
					}
					else
					{
						totalNanos += System.nanoTime() - startNanos;
						
						problemState = ProblemState.INCORRECT;
						
						resultTimer.schedule(new TimerTask()
							{
								@Override
								public void run()
								{
									panelState = PanelState.PROBLEM;
									problemState = ProblemState.NORMAL;
									answer = defaultAnswer;
									currentCaret = '_';
									caretIndex = 2;
									repaint();
									
									// restore input
									for (MathProblemPanel panel : allPanels)
									{
										panel.addMouseListener(panel);
										panel.addKeyListener(panel);
									}
									
									// start the clock again
									startNanos = System.nanoTime();
								}
							}, 1000);
					}
				}
			}
			else
			{
				char key = arg0.getKeyChar();
				
				// if the key is a digit and the answer is not already 3 digits
				if (Character.isDigit(key) && caretIndex >= 0)
				{
					if (answer.substring(caretIndex + 1) == "0")
					{
						answer = "_" + currentCaret + key;
					}
					else
					{
						if (caretIndex < 1)
							answer = answer.substring(caretIndex + 1) + key;
						else
							answer = defaultAnswer.substring(0, caretIndex - 1)
									+ currentCaret
									+ answer.substring(caretIndex + 1) + key;
						
						--caretIndex;
					}
				}
			}
			
			repaint();
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0)
	{
		// do nothing
	}
	@Override
	public void keyTyped(KeyEvent arg0)
	{
		// do nothing
	}
	
	/**
	 * Adds a ProblemPanelListener.
	 * @param listener The listener to add.
	 */
	public void addProblemPanelListener(ProblemPanelListener listener)
	{
		problemPanelListeners.add(listener);
	}
	/**
	 * Removes a ProblemPanelListener.
	 * @param listener The listener to remove.
	 */
	public void removeProblemPanelListener(ProblemPanelListener listener)
	{
		problemPanelListeners.remove(listener);
	}
	
	class CaretTask extends TimerTask
	{
		@Override
		public void run()
		{
			// if the problem panel is still showing and editable
			if (problemState == ProblemState.NORMAL
					&& caretIndex >= 0)
			{
				// switch the currentCaret
				if (answer.charAt(caretIndex) == '_')
				{
					currentCaret = ' ';
				}
				else
					currentCaret = '_';

				answer = answer.substring(0, caretIndex)
						+ currentCaret
						+ answer.substring(caretIndex + 1);
				
				repaint();
			}

			// start the timer again if the problem is still showing
			if (panelState == PanelState.PROBLEM
					&& problemState != ProblemState.INCORRECT_FINAL)
				caretTimer.schedule(new CaretTask(), caretTimerInterval);
		}
	}
	
}