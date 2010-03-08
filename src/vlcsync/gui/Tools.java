package vlcsync.gui;

import javax.swing.JComponent;

public abstract class Tools
{
	public static void setEnabledRecursive( JComponent comp, boolean b )
	{
		for ( int i = 0; i < comp.getComponentCount(); i++ )
		{
			comp.getComponent(i).setEnabled(b);
		}
	}
}
