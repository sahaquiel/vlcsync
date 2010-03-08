package vlcsync.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class VlcRcConnection implements Runnable
{
	private Socket m_socket;
	private InetSocketAddress m_sockAddr;
	boolean m_abortRequested = false;	
	private RcEventListener m_listener;	
	private InputStream m_is;
	private OutputStream m_os;
	private Thread m_rcvThread;
	
	public VlcRcConnection( String host, int port )
	{
		m_sockAddr = new InetSocketAddress( host, port);
		 
		// Java doc says that a new socket needs to be created after a close,
		m_socket = new Socket();
	}
	
	public void open() throws IOException
	{
		m_socket = new Socket();
		m_socket.connect( m_sockAddr, 2000 );
		
		if ( m_socket.isConnected() )
		{
//			m_is = new BufferedInputStream( m_socket.getInputStream() );
			m_is = m_socket.getInputStream();
			m_os = m_socket.getOutputStream();
			
			createRecThread();
		}
	}
	
	public void close() throws IOException
	{
		if ( m_socket.isConnected() )
		{
			m_is.close();
			m_os.close();
		}
			
		m_socket.close();
		
		shutdownRecThread();
				
		/* 
		 * Java doc says that a new socket needs to be created after a close,
		 * so we do that right here. Benefit is that it will have state
		 * "not connected", which we somehow simply can't get by closeing
		 * the existing one.
		 */
		m_socket = new Socket();
	}
	
	public void writeLine( String line )
	{
		try {
			
			m_os.write( line.getBytes() );
			m_os.write( '\n' );
			m_os.flush();
		}
		catch ( SocketException se )
		{
			System.err.println( "writeLine: got exception " + se );
			if ( m_listener != null )
			{
				System.err.println( "rcvThread: notifying listener.." );
				m_listener.connectionAborted();
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String readLine() throws IOException
	{
		StringBuffer buf = new StringBuffer();
		
		int c;
/*		
		if ( m_is.available() == 0 )
		{
			return null;
		}
*/			
		while( true )
		{
			c = m_is.read();
			
//			System.err.print( (char)c );
						
			if ( c == -1 )
			{
				throw new SocketException( "EOF" );
//				return null;
			}
			
			if ( ( (char)c == '\n' ) || ( (char)c == '\r' ) )
			{
				return buf.toString();
			}
			
//			if ( Character.isLetterOrDigit( (char)c) )
			{
				buf.append( (char)c );
			}	
		}
	}
	
	public void registerRcEventListener( RcEventListener ev )
	{
		m_listener = ev;
	}
	
	public void createRecThread()
	{
		m_abortRequested = false;
		m_rcvThread = new Thread( this );
		m_rcvThread.start();
	}
	
	public void shutdownRecThread()
	{
		System.err.println( "shutting down receive thread..." );
		m_abortRequested = true;

//		m_rcvThread.interrupt();
		
		if ( m_rcvThread.isAlive() )
		{
			boolean terminated = false;
			
			while ( !terminated )
			{
				try {
					m_rcvThread.join( 5000 );
					terminated = true;
				} catch (InterruptedException e) {
	//				e.printStackTrace();
				}
			}
		}
		
		if ( m_rcvThread.isAlive() )
		{
			System.out.println( "receive thread didn't finish!" );
		}
		else
		{
			System.err.println( "receive thread shut down sucessfully." );
		}
	}
	
	
	@Override
	public void run() {
		System.out.println( "rcvThread started." );
		
		while( !m_abortRequested )
		{
			String s;
			try {
				System.err.println( "Thread: readLine()..." );
				s = readLine();
				System.err.println( "Thread: got line: '" + s + "'" );
				
				if ( s != null && s.length() > 0 )
				{
					if ( m_listener != null )
					{
						m_listener.LineReturned(s);
					}
				}
			}
			catch ( SocketException se )
			{
				System.err.println( "rcvThread: got exception " + se );
				if ( m_listener != null )
				{
					System.err.println( "rcvThread: notifying listener.." );
					m_listener.connectionAborted();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				s = null;
			}
		} // while m_abortRequested
		
		System.out.println( "rcvThread finished." );
	}

	
	@Override
	public String toString()
	{
		return m_sockAddr.toString() + " - " + ( m_socket.isConnected() ? "connected" : "disconnected" ) ;
	}
	
}
