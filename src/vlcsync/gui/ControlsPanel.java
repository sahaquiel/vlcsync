package vlcsync.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ControlsPanel extends JPanel implements ActionListener
{
	public JButton m_stop;
	public JButton m_play;
	public JButton m_pause;
	
	private Vector<ControlInterface> m_listeners;
	
	public ControlsPanel()
	{
		super();
		
		m_stop = new JButton( "stop" );
		m_play = new JButton( "play" );
		m_pause = new JButton( "pause" );
		
		m_stop.addActionListener( this );
		m_play.addActionListener( this );
		m_pause.addActionListener( this );
		
		add( m_stop );
		add( m_play );
		add( m_pause );
		
		m_listeners = new Vector<ControlInterface>();		
	}
	
	
	public void addControlListener( ControlInterface itf )
	{
		m_listeners.add( itf );
	}
	
	
	public void deleteControlListener( ControlInterface itf )
	{
		m_listeners.remove( itf );
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ae)
	{
		ControlInterface controller = null;
		
		if ( ae.getSource() == m_stop )
		{
			for ( int i = 0; i < m_listeners.size(); i++ )
			{
				controller = m_listeners.elementAt(i);
				controller.stopPressed();
			}
		} 
		else if ( ae.getSource() == m_play )
		{
			for ( int i = 0; i < m_listeners.size(); i++ )
			{
				controller = m_listeners.elementAt(i);
				controller.playPressed();
			}
		} 
		else if ( ae.getSource() == m_pause )
		{
			for ( int i = 0; i < m_listeners.size(); i++ )
			{
				controller = m_listeners.elementAt(i);
				controller.pausePressed();
			}
		} 
	}
}
