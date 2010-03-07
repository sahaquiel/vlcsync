package vlcsync.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class VlcRcConnection
{
	private Socket m_socket;
	InetSocketAddress m_sockAddr;
	InputStream m_is;
	OutputStream m_os;
	
	public VlcRcConnection( String host, int port )
	{
		m_sockAddr = new InetSocketAddress( host, port);
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
		}
	}
	
	public void writeLine( String line )
	{
		try {
			
			m_os.write( line.getBytes() );
			m_os.write( '\n' );
			m_os.flush();
		
		} catch (IOException e) {
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
				return null;
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
}