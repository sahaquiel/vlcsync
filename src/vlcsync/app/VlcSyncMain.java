package vlcsync.app;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;

import vlcsync.data.VlcConnStateListener;
import vlcsync.data.VlcRcConnection;
import vlcsync.data.VlcRemoteConnection;
import vlcsync.gui.ConnectionPanel;
import vlcsync.gui.ControlInterface;
import vlcsync.gui.ControlsPanel;
import vlcsync.gui.Tools;

@SuppressWarnings("serial")
public class VlcSyncMain extends javax.swing.JFrame implements ActionListener, ControlInterface, VlcConnStateListener
{
	private Vector<VlcRemoteConnection> m_conns;
	
	private JButton m_connect;
	private ControlsPanel m_controls;
	private ConnectionPanel m_connPanel;

	private int m_appState;
	
	
	public VlcSyncMain()
	{
		super( "VlcSyncMain" );
		
		m_conns = new Vector<VlcRemoteConnection>();
		
		initGui();
		
		addDefaultConns();
		
		setAppState( DISCONNECTED );
	}
	
	
	private void initGui()
	{
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.setLayout( new FlowLayout() );
		
		m_connect = new JButton( "connect" );
		m_connect.addActionListener( this );
		add( m_connect );
		
		m_controls = new ControlsPanel();
		Tools.setEnabledRecursive( m_controls, false );
		m_controls.addControlListener( this );
		add( m_controls );
		
		m_connPanel = new ConnectionPanel( m_conns );
		Tools.setEnabledRecursive( m_connPanel, false );
		add( m_connPanel );
		
		pack();
	}
	
	
	public static void main( String args[] )
	{
		new VlcSyncMain().setVisible( true );
	}

	
	static private final int DISCONNECTED = 10;
	static private final int CONNECTED = 11	;

	private int getAppState()
	{
		return m_appState;
	}
	
	private void setAppState( int state )
	{
		// TODO: check for correct state-transition
		m_appState = state;
		
		switch ( m_appState )
		{
			case DISCONNECTED:
			{
				Tools.setEnabledRecursive( m_controls, false );			
				Tools.setEnabledRecursive( m_connPanel, false );
				m_connect.setText( "connect" );
				break;
			}	
			case CONNECTED:
			{
				Tools.setEnabledRecursive( m_controls, true );			
				Tools.setEnabledRecursive( m_connPanel, true );
				m_connect.setText( "disconnect" );
				break;
			}	
		}
	}

	
	private void connectAll()
	{
		setMouseWait();
		
		for ( int i = 0; i < m_conns.size(); i++ )
		{
			VlcRemoteConnection c = m_conns.elementAt( i );
			
			c.connect();
		}
		
	}
	
	
	private void disconnectAll()
	{
		setMouseWait();
		
		for ( int i = 0; i < m_conns.size(); i++ )
		{
			VlcRemoteConnection c = m_conns.elementAt( i );
			
			c.disconnect( false );
		}
	}
	
	
	private void addDefaultConns()
	{
		VlcRcConnection rcConn1 = new VlcRcConnection( "192.168.178.32", 4213 );
		
		VlcRemoteConnection conn1 = new VlcRemoteConnection( "Test", rcConn1 );
		conn1.registerVlcConnStateListener( this );
		
		m_conns.add( conn1 );
		
		
		
		m_connPanel.fire();
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
			VlcRemoteConnection c = m_conns.elementAt( i );
			
			c.play();
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ae)
	{
		if ( ae.getSource() == m_connect )
		{
			if ( getAppState() == DISCONNECTED )
			{				
				connectAll();
				
				// TODO: only do this when all connections are established
//				setAppState( CONNECTED );
			}
			else if ( getAppState() == CONNECTED )
			{				
				disconnectAll();
				
				// TODO: only do this when all connections are established
//				setAppState( DISCONNECTED );
			}
		}
	}


	@Override
	public void connectionClosed(VlcRemoteConnection c)
	{		
//		m_connPanel.fire();
		if ( getAppState() == DISCONNECTED )
		{
			// seems like a connect attempt failed
			System.err.println( "error connecting to " + c );
		}
		else
		{
			setAppState( DISCONNECTED );
		}
		
		setMouseNormal();		
	}


	@Override
		public void connectionOpened(VlcRemoteConnection c)
		{
	//		m_connPanel.fire();
			System.out.println( "connection to " + c.getName() + " established." );
			
			setAppState( CONNECTED );
			
			setMouseNormal();		
		}


	private void setMouseWait() {
		// TODO Auto-generated method stub
		
	}


	private void setMouseNormal() {
		// TODO Auto-generated method stub
		
	}
}
