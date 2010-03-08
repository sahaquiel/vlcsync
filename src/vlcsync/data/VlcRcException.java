package vlcsync.data;

@SuppressWarnings("serial")
public class VlcRcException extends Exception
{
	private String m_msg = "<unknown>";
	
	public VlcRcException( String m )
	{
		super();
		m_msg = m;
	}

	public String getVlcCause() {
		return m_msg;
	}
	
	public String toString() {
		return getVlcCause();
	}	
}
