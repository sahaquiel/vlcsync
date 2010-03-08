package vlcsync.data;

import java.io.IOException;


public class VlcRemoteConnection implements RcEventListener
{
	private String m_name;
	private VlcRcConnection m_conn;
	private VlcConnStateListener m_listener;
	
	private Media m_media;
	private Media m_mediaWaiter;	// objects simply used as mutexes
	
	private int m_pendingRequest = NONE;
	
	private static final int NONE = 0;
	private static final int GET_TITLE = 1;
	private static final int GET_LENGTH = 2;
	
	public VlcRemoteConnection( String name, VlcRcConnection conn )
	{
		m_name = name;
		m_conn = conn;
		
		m_conn.registerRcEventListener( this );
	}
	
	public void registerVlcConnStateListener( VlcConnStateListener l )
	{
		m_listener = l;
	}

	private void initConn() throws VlcRcException
	{
		m_media = new Media();
		m_mediaWaiter = new Media();
		
		m_pendingRequest = GET_TITLE;
		m_conn.writeLine( "get_title" );
		System.out.println( "waiting for title..." );
		waitFor( m_mediaWaiter.title );
		if ( m_media.title == null || m_media.title.length() == 0 )
		{
			System.err.println( "timeout waiting for title." );
			throw new VlcRcException( "timeout waiting for title." );
		}
		
		m_pendingRequest = GET_LENGTH;
		m_conn.writeLine( "get_length" );
		System.out.println( "waiting for length..." );
		waitFor( m_mediaWaiter.length );		
		if ( m_media.length == null || m_media.length == 0 )
		{
			System.err.println( "timeout waiting for length." );			
			throw new VlcRcException( "timeout waiting for length." );
		}
	}
	
	public String getName()
	{
		return m_name;
	}

	public void connect()
	{
		try
		{
			m_conn.open();
		
			initConn();

			notifyListenersOpened( this );
		}
		catch( VlcRcException ve )
		{
			disconnect( true );
		}
		catch (IOException e) 
		{
			notifyListenersClosed( this );
		}
	}
	
	private boolean m_disconInProgress = false;
	
	public void disconnect( boolean force )
	{
		m_disconInProgress = true;
		
		m_media = null;
		
		// only send logout when gracefully ending connection
		if ( !force )
		{
			m_conn.writeLine( "logout" );
		}
		try {
			m_conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		notifyListenersClosed( this );
	}
	
	private void notifyListenersOpened( VlcRemoteConnection conn )
	{
		if ( m_listener != null )
			m_listener.connectionOpened( conn );
	}
	
	private void notifyListenersClosed( VlcRemoteConnection conn )
	{
		if ( m_listener != null )
			m_listener.connectionClosed( conn );
	}
	
	private void waitFor( Object o )
	{
/*		
		while ( o == null )
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
*/				
		synchronized (o) {
			try {
				o.wait( 1000 );
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
	     }
		
	}

	private static final String KEYWORD_POS = "pos: ";
	private static final String KEYWORD_STATUSCHANGE = "status change: ";	
	
	@Override
	public void LineReturned(String s)
	{
		System.err.println( "Line returned: " + s );
		
		/* first we handle all random events possible */

		if ( s.startsWith( KEYWORD_POS ) )
		{
			int start = s.indexOf( KEYWORD_POS ) + KEYWORD_POS.length();
			int end = s.indexOf( "%" );
			String perc = s.substring(start, end);
			
			m_media.currPosPercent = Integer.parseInt( perc );
		}
		else if ( s.startsWith( KEYWORD_STATUSCHANGE ) )
		{
			// TODO: handle status change
		}
		else		
		{
			/* 
			 * no random event we know of. So it must be a reply to
			 * our last request. Find out and interpret accordingly
			 */
			if ( m_pendingRequest == GET_TITLE )
			{
				m_pendingRequest = NONE;
				
				m_media.title = s;
				synchronized (m_mediaWaiter.title)
				{
					m_mediaWaiter.title.notify();
				}
				
			}
			else if ( m_pendingRequest == GET_LENGTH )
			{
				m_pendingRequest = NONE;
				
				m_media.length = new Integer( Integer.parseInt( s ) );

				synchronized (m_mediaWaiter.length)
				{
					m_mediaWaiter.length.notify();
				}
			}
		}
	}
	
	@Override
	public void connectionAborted()
	{
		// only call disconnect if we aren't the cause of the DC
		// (e.g. by an explicit call to disconnect())
		if ( !m_disconInProgress )
			disconnect( true );
	}

	// ==== this should be moved to an interface ====
	public void stop()
	{
		m_conn.writeLine( "stop" );
	}
	
	public void play()
	{
		m_conn.writeLine( "play" );
	}
	
	public void pause()
	{
		m_conn.writeLine( "pause" );
	}
	// ========
	
	
	@Override
	public String toString()
	{
		String ret = m_name + ": " + m_conn.toString();
		
		if ( m_media != null )
		{
			ret += ", playing: " + m_media.toString();
		}
		
		return ret;
	}
	
}
