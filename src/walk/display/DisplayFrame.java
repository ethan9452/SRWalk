package walk.display;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DisplayFrame extends JFrame
{
	public DisplayFrame( JPanel display, JPanel menuBar, int widthPixels, int heightPixels,
			int simulationDisplayPixels )
	{
		// Note: with absolute layout, i need to make sure the sizes and
		// locations i set do not overlap
		setLayout( null );

		add( menuBar );
		menuBar.setSize( widthPixels - simulationDisplayPixels, heightPixels );
		menuBar.setLocation( simulationDisplayPixels, 0 );

		add( display );
		display.setSize( simulationDisplayPixels, simulationDisplayPixels );
		display.setLocation( 0, 0 );
		display.setDoubleBuffered( true ); // this should make it render
											// better!!!!!

		// in the JFrame, the menu bar actually takes up some space, so the
		// context pane is the size we wanna set.
		this.getContentPane().setPreferredSize( new Dimension( widthPixels, heightPixels ) );
		pack(); // This sets the JFrame size such that it's content pane (the stuff besides the menu bar) is the preffered size
		setMinimumSize( getSize() ); 

		setTitle( "walkooo" );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setLocationRelativeTo( null );

		setVisible( true );
	}
}
