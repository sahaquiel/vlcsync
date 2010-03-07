package vlcsync.data;


public class RemoteOutput
{
	VlcRcConnection m_conn;
	
	public RemoteOutput( VlcRcConnection conn )
	{
		m_conn = conn;
	}
	
	public void sendStop()
	{
		m_conn.writeLine( "stop" );
	}
	
	public void sendStatus()
	{
		m_conn.writeLine( "status" );
	}
	
	public void sendPlay()
	{
		m_conn.writeLine( "play" );
	}
	
	public void sendPause()
	{
		m_conn.writeLine( "pause" );
	}
	
	public void send( String line )
	{
		m_conn.writeLine( line );
	}
}
