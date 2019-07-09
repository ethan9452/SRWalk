package walk.display;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextFieldWithSubmitButton extends JButton
{
	private JTextField	inputField;
	private JLabel		label;

	TextFieldWithSubmitButton( String buttonName, String labelName )
	{
		super( buttonName );
		inputField = new JTextField( 13 );
		label = new JLabel( labelName );
	}

	public String getInputValue()
	{
		return inputField.getText();
	}
	
	public void setInputValue( String s)
	{
		inputField.setText( s );
	}
	
	public void addToPanel( JPanel p )
	{
		p.add( label );
		p.add( inputField );
		p.add( this );

	}

}
