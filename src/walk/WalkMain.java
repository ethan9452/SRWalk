package walk;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SpinnerModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import walk.display.DisplayFrame;
import walk.display.JSliderWithTextField;
import walk.display.MenuBarDisplay;
import walk.display.SimulationDisplay;
import walk.display.TextFieldWithSubmitButton;

public class WalkMain implements ActionListener
{
	// Simulation Display Settings
	final static int			SIMULATION_DISPLAY_PIXELS			= 600;

	final static int			MENU_BAR_WIDTH						= 200;
	final static int			MENU_BAR_MIN_HEIGHT					= 400;

	private static final int	MAX_SIMULATION_DISPLAY_SCALE		= 100;
	private static final int	MIN_SIMULATION_DISPLAY_SCALE		= 1;
	private final static int	DEFAULT_SIMULATION_DISPLAY_SCALE	= 1;

	private static final int	DEFAULT_TIMER_DELAY					= 5;
	private static final int	MAX_TIMER_DELAY						= 100;
	private static final int	MIN_TIMER_DELAY						= 1;

	static int					DEFAULT_SIMULATION_CLOCK_SPEED_MS	= 100;

	private SimulationDisplay	display;
	private MenuBarDisplay		menuBar;
	private WalkSimulator		simulator;

	private Timer				timer;

	public WalkMain()
	{
		simulator = new WalkSimulator();
		display = new SimulationDisplay( simulator, SIMULATION_DISPLAY_PIXELS, DEFAULT_SIMULATION_DISPLAY_SCALE );
		menuBar = new MenuBarDisplay( simulator, MENU_BAR_WIDTH,
				Math.max( MENU_BAR_MIN_HEIGHT, SIMULATION_DISPLAY_PIXELS ) );

		timer = new Timer( DEFAULT_SIMULATION_CLOCK_SPEED_MS, this );

		registerMenuButtons();
		menuBar.addStatsDisplays();
		menuBar.validate();

		EventQueue.invokeLater( () ->
		{
			final int width = SIMULATION_DISPLAY_PIXELS + MENU_BAR_WIDTH;
			final int height = Math.max( SIMULATION_DISPLAY_PIXELS, MENU_BAR_MIN_HEIGHT );

			DisplayFrame ex = new DisplayFrame( display, menuBar, width, height, SIMULATION_DISPLAY_PIXELS );
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

						display.repaint();
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

						display.repaint();
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
						display.repaint();
						menuBar.repaint();
					}
				} );

		menuBar.registerSubmittableInputField( "Run", "Run Simulation for Iterations: ",
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
	}

	private void resetSimulation()
	{
		timer.stop();

		simulator.resetSimulationState();

		display.repaint();
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
//				menuBar.updateStatsDisplays();
//				menuBar.paintImmediately( 0, 0, 200, 800 );
			}
		}

		menuBar.updateStatsDisplays();
		display.repaint();
		menuBar.repaint();
	}

	@Override
	// For Timer
	public void actionPerformed( ActionEvent e )
	{
		simulator.step();

		menuBar.updateStatsDisplays();

		display.repaint();

		menuBar.repaint();
	}

	public static void main( String[] args )
	{
		WalkMain mainLoop = new WalkMain();
		// mainLoop.startSimulation();

		// mainLoop.runSimulationForIterations(10000);
	}

}
