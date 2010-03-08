package vlcsync.data;

public interface VlcConnStateListener
{
	public void connectionOpened( VlcRemoteConnection c );
	public void connectionClosed( VlcRemoteConnection c );
}
