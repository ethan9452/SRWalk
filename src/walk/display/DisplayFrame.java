package walk.display;
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
		

		setSize( widthPixels, heightPixels );

		setTitle( "walkooo" );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setLocationRelativeTo( null );

		setVisible( true );
	}
}
