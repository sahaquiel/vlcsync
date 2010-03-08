package vlcsync.gui;

import java.util.Vector;

import javax.swing.AbstractListModel;

import vlcsync.data.VlcRemoteConnection;

@SuppressWarnings("serial")
public class VlcRemoteConnListModel extends AbstractListModel  
{
	private Vector<VlcRemoteConnection> m_conns;
	
	public VlcRemoteConnListModel( Vector<VlcRemoteConnection> conns )
	{
		super();
		
		m_conns = conns;
	}
	
	public void fire()
	{
		this.fireIntervalAdded( this, 0, m_conns.size() );
	}
	
	@Override
	public Object getElementAt(int index)
	{
		return m_conns.elementAt(index);
	}

	@Override
	public int getSize() {
		return m_conns.size();
	}

}
