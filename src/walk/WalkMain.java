package walk;

import java.awt.EventQueue;
import java.util.List;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import walk.display.MenuBarSection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.SpinnerModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import walk.display.ComboBoxWithSubmitButton;
import walk.display.DisplayFrame;
import walk.display.JSliderWithTextField;
import walk.display.MenuBarDisplay;
import walk.display.SimulationDisplay;
import walk.display.TextFieldWithSubmitButton;

public class WalkMain implements ActionListener
{
	// Simulation Display Settings
	final static int						SIMULATION_DISPLAY_PIXELS			= 600;

	final static int						MENU_BAR_WIDTH						= 400;
	final static int						MENU_BAR_MIN_HEIGHT					= 400;

	private static final int				MAX_SIMULATION_DISPLAY_SCALE		= 100;
	private static final int				MIN_SIMULATION_DISPLAY_SCALE		= 1;
	private final static int				DEFAULT_SIMULATION_DISPLAY_SCALE	= 40;

	private static final int				MAX_TIMER_DELAY						= 1000;
	private static final int				MIN_TIMER_DELAY						= 1;
	private static final int				DEFAULT_TIMER_DELAY					= 80;

	private SimulationDisplay				display;
	private SimulationDisplayMouseListener	mouseListener;
	private MenuBarDisplay					menuBar;
	private WalkSimulator					simulator;

	private Timer							timer;

	private boolean							isRenderOn;

	public WalkMain()
	{
		simulator = new WalkSimulator();
		display = new SimulationDisplay( simulator, SIMULATION_DISPLAY_PIXELS, DEFAULT_SIMULATION_DISPLAY_SCALE );
		menuBar = new MenuBarDisplay( this, simulator, MENU_BAR_WIDTH,
				Math.max( MENU_BAR_MIN_HEIGHT, SIMULATION_DISPLAY_PIXELS ) );
		mouseListener = new SimulationDisplayMouseListener( simulator, display, menuBar );

		timer = new Timer( DEFAULT_TIMER_DELAY, this );

		isRenderOn = true;

		EventQueue.invokeLater( () ->
		{
			final int width = SIMULATION_DISPLAY_PIXELS + MENU_BAR_WIDTH;
			final int height = Math.max( SIMULATION_DISPLAY_PIXELS, MENU_BAR_MIN_HEIGHT );

			DisplayFrame ex = new DisplayFrame( display, menuBar, width, height, SIMULATION_DISPLAY_PIXELS );

			registerMenuButtons();
			menuBar.addStatsDisplays();
			menuBar.addSettingsForAllComponentsInHeirarchy();

			menuBar.updateStatsDisplays();
			menuBar.repaint();

			registerDisplayMouseListeners();
		} );

	}

	private void registerMenuButtons()
	{
		menuBar.registerButton( "Start Walk", new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				timer.start();

			};
		} );

		menuBar.registerButton( "Pause", new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				timer.stop();
			}
		} );

		menuBar.registerButton( "Reset", new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				resetSimulation();
				menuBar.updateStatsDisplays();
				menuBar.repaint();
			}
		} );

		menuBar.registerButton( "Clear Objects", new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				resetSimulation();
				simulator.clearAllObjects();
				menuBar.updateStatsDisplays();
				menuBar.repaint();
			}
		} );

		menuBar.registerToggleButton( "\"Collisions\"", simulator.getIsCollisionOn(), new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				JToggleButton toggleButton = (JToggleButton) e.getSource();

				if ( toggleButton.isSelected() )
				{
					simulator.tryTurnCollisionOn();

					if ( simulator.getIsCollisionOn() == false )
					{
						toggleButton.setSelected( false );
					}
					else
					{

						display.paintIfTrue( isRenderOn );

					}

					menuBar.updateStatsDisplays();
					menuBar.repaint();
				}
				else
				{
					simulator.turnCollisionOff();
					menuBar.updateStatsDisplays();
					menuBar.repaint();
					display.paintIfTrue( isRenderOn );

				}
			}
		} );

		menuBar.registerToggleButton( "Render", isRenderOn, new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent e )
			{
				JToggleButton button = (JToggleButton) e.getSource();

				if ( button.isSelected() )
				{
					isRenderOn = true;

					menuBar.updateStatsDisplays();
					menuBar.repaint();
					display.paintIfTrue( isRenderOn );
				}
				else
				{
					isRenderOn = false;
				}
			}
		} );

		menuBar.registerSliderWithDynamicLabel( "Display Scale", MIN_SIMULATION_DISPLAY_SCALE,
				MAX_SIMULATION_DISPLAY_SCALE,
				DEFAULT_SIMULATION_DISPLAY_SCALE,
				new ChangeListener()
				{
					@Override
					public void stateChanged( ChangeEvent e )
					{
						JSliderWithTextField source = (JSliderWithTextField) e
								.getSource();

						final int scale = (int) source.getValue();
						display.setDisplayPixelScale( scale );

						source.setTextFieldValue( Integer.toString( scale ) );

						display.paintIfTrue( isRenderOn );
						menuBar.repaint();
					}
				} );

		menuBar.registerSliderWithDynamicLabel( "ms / step", MIN_TIMER_DELAY, MAX_TIMER_DELAY, DEFAULT_TIMER_DELAY,
				new ChangeListener()
				{

					@Override
					public void stateChanged( ChangeEvent e )
					{
						JSliderWithTextField source = (JSliderWithTextField) e
								.getSource();

						final int timerDelayMs = (int) source.getValue();
						timer.setDelay( timerDelayMs );

						source.setTextFieldValue( Integer.toString( timerDelayMs ) );

						menuBar.updateStatsDisplays();
						display.paintIfTrue( isRenderOn );
						menuBar.repaint();
					}
				} );

		menuBar.registerSpinnerWithLabel( "Number of Walkers: ", 0, 10000000, 0,
				new ChangeListener()
				{
					@Override
					public void stateChanged( ChangeEvent e )
					{
						// TODO we would like the spinner to not show invalid
						// input
						SpinnerModel source = (SpinnerModel) e.getSource();

						final int numWalkers = (int) source.getValue();

						simulator.setWalkerCount( numWalkers );

						menuBar.updateStatsDisplays();
						display.paintIfTrue( isRenderOn );
						menuBar.repaint();
					}
				} );

		menuBar.registerSubmittableInputField( "Run", "Run Simulation for Iterations: ", MenuBarSection.MENU_LEFT,
				new ActionListener()
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						TextFieldWithSubmitButton source = (TextFieldWithSubmitButton) e.getSource();

						final String inputValue = source.getInputValue();

						try
						{
							final int iters = Integer.valueOf( inputValue );
							runSimulationForIterations( iters );
						}
						catch ( NumberFormatException exception )
						{
							source.setInputValue( "invalid" );
						}

					}
				} );

		menuBar.registerSubmittableInputField( "Save", "Save State", MenuBarSection.MENU_RIGHT, new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				TextFieldWithSubmitButton source = (TextFieldWithSubmitButton) e.getSource();

				final String inputValue = source.getInputValue();

				WalkSimulatorStateSaver.saveSimulationState( inputValue, simulator );
			}
		} );

		menuBar.registerComboBoxWithSubmitButton( new String[0], "Load", MenuBarSection.MENU_RIGHT, new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				// TODO getting the ComboBoxWithSubmitButton by reffering to parent seems hacky..
				JButton source = (JButton) e.getSource();
				ComboBoxWithSubmitButton parentContainer = (ComboBoxWithSubmitButton)source.getParent();
				
				WalkSimulatorStateSaver.loadSimulationState( parentContainer.getSelectedFile(), simulator );
				
				display.repaint();
				menuBar.repaint();
			}
		},
				new PopupMenuListener()
				{

					@Override
					public void popupMenuWillBecomeVisible( PopupMenuEvent e )
					{
						JComboBox<String> source = (JComboBox<String>) e.getSource();

						List<String> fileNames = WalkSimulatorStateSaver.getSaveFileNames();

						source.removeAllItems();

						for ( String filename : fileNames )
						{
							source.addItem( filename );
						}
					}

					@Override
					public void popupMenuWillBecomeInvisible( PopupMenuEvent e )
					{
						// TODO Auto-generated method stub

					}

					@Override
					public void popupMenuCanceled( PopupMenuEvent e )
					{
						// TODO Auto-generated method stub

					}
				} );
	}

	private void registerDisplayMouseListeners()
	{
		display.addMouseListener( mouseListener );
		display.addMouseMotionListener( mouseListener );
	}

	private void resetSimulation()
	{
		timer.stop();

		simulator.resetSimulationState();

		display.paintIfTrue( isRenderOn );
		menuBar.repaint();

	}

	private void runSimulationForIterations( final int iterations )
	{
		simulator.resetSimulationState();

		for ( int i = 0; i < iterations; i++ )
		{
			simulator.step();

			if ( i % 10000 == 0 )
			{
				System.out.println( "Iter: " + i + " / " + iterations );

				// TODO: come up with a way to display progress
				// menuBar.updateStatsDisplays();
				// menuBar.paintImmediately( 0, 0, 200, 800 );
			}
		}

		menuBar.updateStatsDisplays();
		display.paintIfTrue( isRenderOn );
		menuBar.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * This timer calls this method every tick
	 */
	@Override
	public void actionPerformed( ActionEvent e )
	{
		simulator.step();

		menuBar.updateStatsDisplays();

		display.paintIfTrue( isRenderOn );

		menuBar.repaint();
	}

	public int getTimerDelayMs()
	{
		return timer.getDelay();
	}

	public static void main( String[] args )
	{
		WalkMain mainLoop = new WalkMain();
	}

}
