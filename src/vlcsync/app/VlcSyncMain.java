package vlcsync.app;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;

import vlcsync.data.VlcRcConnection;
import vlcsync.data.VlcRemoteConnection;
import vlcsync.gui.ControlInterface;
import vlcsync.gui.ControlsPanel;

@SuppressWarnings("serial")
public class VlcSyncMain extends javax.swing.JFrame implements ActionListener, ControlInterface
{
	private Vector<VlcRemoteConnection> m_conns;
	
	private JButton m_connect;
	private ControlsPanel m_controls;
	
	public VlcSyncMain()
	{
		super( "VlcSyncMain" );
		
		initGui();

		m_conns = new Vector<VlcRemoteConnection>();
	}
	
	private void initGui()
	{
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.setLayout( new FlowLayout() );
		
		m_connect = new JButton( "connect" );
		m_connect.addActionListener( this );
		add( m_connect );
		
		m_controls = new ControlsPanel();
		m_controls.addControlListener( this );
		add( m_controls );
		
		pack();		
	}
	
	public static void main( String args[] )
	{
		new VlcSyncMain().setVisible( true );
	}

	private void addNewConn()
	{
		VlcRcConnection conn = new VlcRcConnection( "192.168.178.32", 4213 );
		
		try {
			conn.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			conn = null;
		}
		
		if ( conn != null )
		{
			m_conns.add( new VlcRemoteConnection( "Test", conn ) );
		}
	}
	
	public void stopPressed()
	{
		for ( int i = 0; i < m_conns.size(); i++ )
		{
			VlcRemoteConnection c = m_conns.elementAt( i );
		
			c.stop();
		}
	}
	
	public void pausePressed()
	{
		for ( int i = 0; i < m_conns.size(); i++ )
		{
			VlcRemoteConnection c = m_conns.elementAt( i );
		
			c.pause();
		}
	}
	
	public void playPressed()
	{
		for ( int i = 0; i < m_conns.size(); i++ )
		{
			VlcRemoteConnection c = m_conns.elementAt(0);
			
			c.play();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent ae)
	{
		if ( ae.getSource() == m_connect )
		{
			addNewConn();
		}
	}
}
