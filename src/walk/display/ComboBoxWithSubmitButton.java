package walk.display;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.PopupMenuListener;

public class ComboBoxWithSubmitButton extends JPanel
{
	private JComboBox<String>	cBox;
	private JButton				button;
	

	public ComboBoxWithSubmitButton( String[] items, String label, ActionListener buttonCallback , PopupMenuListener dropDownCallback)
	{
		cBox = new JComboBox<String>( items );
		button = new JButton( label );
		fixCBoxSizing( cBox );
		
		this.add(cBox);
		this.add(button);
		
		button.addActionListener( buttonCallback );
		cBox.addPopupMenuListener( dropDownCallback );
		
		
	}
	
	public String getSelectedFile()
	{
		return (String) cBox.getSelectedItem();
	}
	
	private void fixCBoxSizing( JComboBox<?> feckedUpBox )
	{
		Dimension oldMaxSize = feckedUpBox.getMaximumSize();
		feckedUpBox.setMaximumSize( new Dimension( oldMaxSize.width, 20 ) );
	}
}
