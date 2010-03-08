package vlcsync.gui;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import vlcsync.data.VlcRemoteConnection;

@SuppressWarnings("serial")
public class ConnectionPanel extends JPanel
{
	private JList m_list;
	private VlcRemoteConnListModel m_model;
	
	public ConnectionPanel( Vector<VlcRemoteConnection> conns )
	{
		super();
		
		m_model = new VlcRemoteConnListModel( conns );
		m_list = new JList( m_model );
		m_list.setPreferredSize( new Dimension( 500, 100 ) );
		JScrollPane pane = new JScrollPane( m_list ); 
		
//		add( m_list );
		add( pane );
	}
	
	public void fire()
	{
		m_model.fire();
	}
}
