package vlcsync.data;

import java.io.IOException;

public class RemoteInput implements Runnable
{
	VlcRcConnection m_conn;
	RcEventListener m_listener;
	boolean m_abortRequested = false;
	
	public RemoteInput( VlcRcConnection conn, RcEventListener listener )
	{
		m_conn = conn;
		registerRcEventListener( listener );
//		createRecThread();
	}
	
	public void registerRcEventListener( RcEventListener ev )
	{
		m_listener = ev;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while( !m_abortRequested )
		{
			String s;
			try {
				System.err.println( "Thread: readLine()..." );
				s = m_conn.readLine();
				System.err.println( "Thread: got line: '" + s + "'" );
				
				if ( s != null && s.length() > 0 )
				{
					if ( m_listener != null )
					{
						m_listener.LineReturned(s);
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				s = null;
			}
		} // while m_abortRequested
	}

	public void createRecThread()
	{
		new Thread( this ).start();
	}	
}
