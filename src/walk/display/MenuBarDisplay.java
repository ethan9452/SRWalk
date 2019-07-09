package walk.display;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import walk.WalkSimulator;

public class MenuBarDisplay extends JPanel
{
	WalkSimulator				simulator;

	private static final Color	BG_COLOR	= Color.white;

	private final int			widthPixels;
	private final int			heightPixels;

	private JLabel				biggestX;
	private JLabel				biggestY;
	private JLabel				smallestX;
	private JLabel				smallestY;
	private JLabel				numWalkers;
	private JLabel				iterCount;

	public MenuBarDisplay( WalkSimulator simulator, int widthPixels, int heightPixels )
	{
		this.widthPixels = widthPixels;
		this.heightPixels = heightPixels;

		this.simulator = simulator;

		setBackground( Color.white );
		setLayout( new FlowLayout() );

		biggestX = new JLabel( "Biggest X:" );
		biggestY = new JLabel( "Biggest Y:" );
		smallestX = new JLabel( "Smallest X:" );
		smallestY = new JLabel( "Smallest Y:" );
		numWalkers = new JLabel( "Num Walkers:" );
		iterCount = new JLabel( "Iter Count:" );
	}

	public void addStatsDisplays()
	{
		// TODO get this to work
		// JPanel statsPanel = new JPanel();
		// add(statsPanel);
		// statsPanel.setBackground( BG_COLOR );
		// statsPanel.add( biggestX );
		// statsPanel.add( biggestY );
		// statsPanel.add( smallestX );
		// statsPanel.add( smallestY );
		// statsPanel.add( numWalkers );
		// statsPanel.add( iterCount );

		add( biggestX );
		add( biggestY );
		add( smallestX );
		add( smallestY );
		add( numWalkers );
		add( iterCount );
	}

	public void updateStatsDisplays()
	{
		biggestX.setText( "Biggest X:" + simulator.getBiggestX() );
		biggestY.setText( "Biggest Y:" + simulator.getBiggestY() );
		smallestX.setText( "Smallest X:" + simulator.getSmallestX() );
		smallestY.setText( "Smallest Y:" + simulator.getSmallestY() );
		numWalkers.setText( "Num Walkers:" + simulator.getNumWalkers() );
		iterCount.setText( "Iter Count:" + simulator.getIterCount() );
	}

	public void registerButton( String name, ActionListener buttonCallback )
	{
		JButton b = new JButton( name );
		add( b );
		b.addActionListener( buttonCallback );
	}

	public void registerSpinnerWithLabel( String name, int min, int max, int start, ChangeListener spinnerCallback )
	{
		SpinnerModel spinnerModel = new SpinnerNumberModel( start, min, max, 1 );
		spinnerModel.addChangeListener( spinnerCallback );

		JLabel label = new JLabel( name );

		add( label );

		JSpinner spinner = new JSpinner( spinnerModel );
		label.setLabelFor( spinner );

		add( spinner );

	}

	public void registerSubmittableInputField( String buttonName, String labelName, ActionListener buttonCallback )
	{
		TextFieldWithSubmitButton b = new TextFieldWithSubmitButton( buttonName, labelName );
		b.addActionListener( buttonCallback );
		b.addToPanel( this );

	}

	public void registerSliderWithDynamicLabel( String name, int min, int max, int start,
			ChangeListener sliderCallback )
	{

		JSliderWithTextField s = new JSliderWithTextField( JSlider.HORIZONTAL, min, max, start, name );
		s.addChangeListener( sliderCallback );

		add( s );
		add( s.getLabel() );
		add( s.getTextField() );
	}

}
