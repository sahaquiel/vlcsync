package vlcsync.data;


public class VlcRemoteConnection implements RcEventListener
{
	private String m_name;
	private VlcRcConnection m_conn;
	private RemoteInput m_input;
	private RemoteOutput m_output;
	
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
		
		m_media = new Media();
		m_mediaWaiter = new Media();

		m_input = new RemoteInput(m_conn, this);
		m_output = new RemoteOutput(m_conn);
		
		m_input.createRecThread();
		
		m_pendingRequest = GET_TITLE;
		m_output.send( "get_title" );
		System.err.println( "waiting for title..." );
		waitFor( m_mediaWaiter.title );
		
		m_pendingRequest = GET_LENGTH;
		m_output.send( "get_length" );
		System.err.println( "waiting for length..." );
		waitFor( m_mediaWaiter.length );
	}
	
	public String getName()
	{
		return m_name;
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
				o.wait();
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
	
	public void stop()
	{
		m_output.sendStop();
	}
	
	public void pause()
	{
		m_output.sendPause();
	}
	
	public void play()
	{
		m_output.sendPlay();
	}

}
