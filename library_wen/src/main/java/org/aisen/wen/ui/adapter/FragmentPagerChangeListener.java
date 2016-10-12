package org.aisen.wen.ui.adapter;

public interface FragmentPagerChangeListener {

	void instantiate(String fragmentName);
	
	void destroy(String fragmentName);
	
}
