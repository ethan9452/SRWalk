package walk.display;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;

public class JSliderWithTextField extends JSlider
{
	private JTextField	dynamicText;
	private JLabel		label;

	public JSliderWithTextField( int orientation, int min, int max, int start, String labelString )
	{
		super( orientation, min, max, start );

		dynamicText = new JTextField( 3 );
		dynamicText.setEnabled( false );
		dynamicText.setText( Integer.toString( start ) );

		label = new JLabel( labelString );
	}

	public void setTextFieldValue( String str )
	{
		dynamicText.setText( str );
	}

	public JTextField getTextField()
	{
		return dynamicText;
	}

	public JLabel getLabel()
	{
		return label;
	}
}
