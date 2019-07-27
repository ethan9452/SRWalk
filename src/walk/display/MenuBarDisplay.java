package walk.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuListener;

import walk.SimulationObjectType;
import walk.WalkMain;
import walk.simulator.WalkSimulator;

public class MenuBarDisplay extends JPanel
{
	private static Font				DEFAULT_Font		= new JLabel().getFont();
	private static Font				SMALLER_FONT		= new Font( DEFAULT_Font.getName(), DEFAULT_Font.getStyle(),
			(DEFAULT_Font.getSize() - 3) );

	private static DecimalFormat	decimalFormatter	= new DecimalFormat( "#.00" );

	WalkSimulator					simulator;
	WalkMain						simulatorMain;

	private static final Color		BG_COLOR			= Color.white;

	private final int				widthPixels;
	private final int				heightPixels;

	private JPanel					leftPanel;
	private JPanel					rightPanel;

	private JPanel					statsPanel;

	private JLabel					biggestX;
	private JLabel					biggestY;
	private JLabel					smallestX;
	private JLabel					smallestY;
	private JLabel					iterCount;
	private JLabel					intendedFps;
	private JLabel					actualFps;
	private JLabel					numWalkers;
	private JLabel					numWalls;
	private JLabel					numMagnets;

	private SimulationObjectType	paintBrushObjectType;

	public MenuBarDisplay( WalkMain simulatorMain, WalkSimulator simulator, int widthPixels, int heightPixels )
	{
		this.widthPixels = widthPixels;
		this.heightPixels = heightPixels;

		this.simulatorMain = simulatorMain;
		this.simulator = simulator;

		GridLayout mainLayoutManager = new GridLayout( 1, 2, 0, 0 );
		setLayout( mainLayoutManager );

		leftPanel = new JPanel();
		BoxLayout leftLayoutManager = new BoxLayout( leftPanel, BoxLayout.PAGE_AXIS );
		leftPanel.setLayout( leftLayoutManager );

		rightPanel = new JPanel();
		BoxLayout rightLayoutManager = new BoxLayout( rightPanel, BoxLayout.PAGE_AXIS );
		rightPanel.setLayout( rightLayoutManager );

		add( leftPanel );
		add( rightPanel );

		biggestX = new JLabel( "Biggest X:" );
		biggestY = new JLabel( "Biggest Y:" );
		smallestX = new JLabel( "Smallest X:" );
		smallestY = new JLabel( "Smallest Y:" );
		iterCount = new JLabel( "Iter Count:" );

		intendedFps = new JLabel( "Set FPS:" );
		actualFps = new JLabel( "Actual FPS:" );

		numWalkers = new JLabel( "Num Walkers:" );
		numWalls = new JLabel( "Num Walls:" );
		numMagnets = new JLabel( "Num Magnets:" );

		paintBrushObjectType = SimulationObjectType.NONE;

		registerIndependentComponentsToRightPanel();
	}

	private JPanel getPanelForSection( MenuBarSection section )
	{
		switch ( section )
		{
		case MENU_LEFT:
			return leftPanel;

		case MENU_RIGHT:
			return rightPanel;

		default:
			throw new IllegalArgumentException( "invalid menu seciton " + section );
		}
	}

	/**
	 * Note: For now, these buttons can only alter things in the menuBar object,
	 * as opposed to the left panel, which is mostly set up in WalkMain and can
	 * alter its state.
	 */
	private void registerIndependentComponentsToRightPanel()
	{
		registerPaintBrushChooser();
	}

	private void registerPaintBrushChooser()
	{
		JComboBox<String> brushChooser = new JComboBox<String>( SimulationObjectType.getDisplayNames() );
		
		fixCBoxSizing(brushChooser);
		
		brushChooser.setSelectedItem( "None" ); // TODO hacky
		
		brushChooser.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
				String selectedBrushName = (String) comboBox.getSelectedItem();
				paintBrushObjectType = SimulationObjectType.displayNameToEnum.get( selectedBrushName );
			}
		} );

		rightPanel.add( brushChooser );
	}
	
	private void fixCBoxSizing( JComboBox<?> feckedUpBox )
	{
		Dimension oldMaxSize = feckedUpBox.getMaximumSize();
		feckedUpBox.setMaximumSize( new Dimension( oldMaxSize.width, 20 ) );
	}
	
	public void registerComboBoxWithSubmitButton( String[] options, String label, MenuBarSection section, ActionListener actionCallback, PopupMenuListener dropDownCallback )
	{
		ComboBoxWithSubmitButton thing = new ComboBoxWithSubmitButton(options, label, actionCallback, dropDownCallback);
		
		getPanelForSection( section ).add( thing );
	}

	public void addSettingsForAllComponentsInHeirarchy()
	{
		// Set background color for all children
		synchronized ( getTreeLock() )
		{
			Stack<Component> stack = new Stack<>();
			stack.push( this );
			while ( !stack.isEmpty() )
			{
				Component curComponent = stack.pop();

				curComponent.setBackground( Color.white );
				curComponent.revalidate();
				curComponent.repaint();

				if ( curComponent instanceof Container )
				{
					Component[] children = ((Container) curComponent).getComponents();
					for ( Component child : children )
					{
						stack.push( child );
					}
				}
			}
		}

	}

	public void addStatsDisplays()
	{
		JPanel statsPanel = new JPanel( new GridLayout( 1, 2, 0, 0 ) );
		// statsPanel.setBorder( BorderFactory.createEmptyBorder( 0,0,10,0 ) );

		JPanel left = new JPanel();
		JPanel right = new JPanel();

		biggestX.setFont( SMALLER_FONT );
		biggestY.setFont( SMALLER_FONT );
		smallestX.setFont( SMALLER_FONT );
		smallestY.setFont( SMALLER_FONT );
		iterCount.setFont( SMALLER_FONT );
		intendedFps.setFont( SMALLER_FONT );
		actualFps.setFont( SMALLER_FONT );
		numWalkers.setFont( SMALLER_FONT );
		numWalls.setFont( SMALLER_FONT );
		numMagnets.setFont( SMALLER_FONT );

		left.add( biggestX );
		left.add( biggestY );
		left.add( smallestX );
		left.add( smallestY );
		left.add( iterCount );

		right.add( intendedFps );
		right.add( actualFps );
		right.add( numWalkers );
		right.add( numWalls );
		right.add( numMagnets );

		statsPanel.add( left );
		statsPanel.add( right );

		// TODO this is to fit all the labels. maybe can make it more general
		// ifthis probelm comes up a lot
		statsPanel.setPreferredSize( new Dimension( widthPixels, 70 ) );

		leftPanel.add( statsPanel );

		this.statsPanel = statsPanel;
		// debugLocationPrint( biggestX );
	}

	public void updateStatsDisplays()
	{
		biggestX.setText( "Biggest X:" + simulator.getBiggestX() );
		biggestY.setText( "Biggest Y:" + simulator.getBiggestY() );
		smallestX.setText( "Smallest X:" + simulator.getSmallestX() );
		smallestY.setText( "Smallest Y:" + simulator.getSmallestY() );
		iterCount.setText( "Iter Count:" + simulator.getIterCount() );

		intendedFps.setText( "Set FPS: " + decimalFormatter.format( (1000. / simulatorMain.getTimerDelayMs()) ) );
		actualFps.setText( "Actual FPS: " + decimalFormatter.format( simulator.getActualFps() ) );

		numWalkers.setText( "Num Walkers:" + simulator.getNumWalkers() );
		numWalls.setText( "Num Walls:" + simulator.getNumWalls() );
		numMagnets.setText( "Num Magnets:" + simulator.getNumMagnets() );

	}

	public void registerButton( String name, ActionListener buttonCallback )
	{
		JButton b = new JButton( name );

		b.setAlignmentX( Component.CENTER_ALIGNMENT );

		leftPanel.add( b );
		b.addActionListener( buttonCallback );
	}

	public void registerToggleButton( String name, boolean startingState, ActionListener callback )
	{
		JToggleButton b = new JToggleButton( name );
		b.setSelected( startingState );
		b.setAlignmentX( Component.CENTER_ALIGNMENT );
		leftPanel.add( b );
		b.addActionListener( callback );
	}

	public void registerSpinnerWithLabel( String name, int min, int max, int start, ChangeListener spinnerCallback )
	{
		JPanel panel = new JPanel();

		SpinnerModel spinnerModel = new SpinnerNumberModel( start, min, max, 1 );
		spinnerModel.addChangeListener( spinnerCallback );

		JLabel label = new JLabel( name );

		panel.add( label );

		JSpinner spinner = new JSpinner( spinnerModel );
		label.setLabelFor( spinner );

		panel.add( spinner );

		leftPanel.add( panel );

	}

	public void registerSubmittableInputField( String buttonName, String labelName, MenuBarSection section,
			ActionListener buttonCallback )
	{
		TextFieldWithSubmitButton b = new TextFieldWithSubmitButton( buttonName, labelName );
		b.addActionListener( buttonCallback );
		b.addToPanel( getPanelForSection( section ) );

	}

	public void registerSliderWithDynamicLabel( String name, int min, int max, int start,
			ChangeListener sliderCallback )
	{

		JSliderWithTextField s = new JSliderWithTextField( JSlider.HORIZONTAL, min, max, start, name );
		s.addChangeListener( sliderCallback );

		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setVgap( 0 );
		panel.setLayout( layout );

		panel.add( s );
		panel.add( s.getLabel() );
		panel.add( s.getTextField() );

		leftPanel.add( panel );
	}

	public void debugLocationPrint( Component component )
	{
		System.out.println(
				"top left: " + component.getLocation() + " size: " + component.getSize() + " preffered size: " +
						component.getPreferredSize() + " min size " + component.getMinimumSize() + " max size "
						+ component.getMaximumSize() );
	}

	public SimulationObjectType getPaintBrushObjectType()
	{
		return paintBrushObjectType;
	}

}
