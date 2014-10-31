package liyf.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.beetl.core.GroupTemplate;
import org.beetl.ext.servlet.ServletGroupTemplate;
import org.beetl.ext.web.SimpleCrossFilter;

public class BasicCrossFilter extends SimpleCrossFilter{
	private GroupTemplate groupTemplate;
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

	@Override
	protected GroupTemplate getGroupTemplate() {
		// TODO Auto-generated method stub
		return ServletGroupTemplate.instance().getGroupTemplate();
	}
}
