package org.beetl.core.lab;

import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

import liyf.beetl.util.UserBean;

import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

public class Test
{
	public static void main(String[] args) throws Exception
	{

		System.out.println(System.currentTimeMillis());
		ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader();
		System.out.println(resourceLoader.getResource("/org/beetl/core/lab/hello.txt").getContent(1, 3));
		System.out.println("---------------");
		Configuration cfg = Configuration.defaultConfiguration();
		cfg.setDirectByteOutput(true);
		GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
		cfg.setStatementStart("<%");
		cfg.setStatementEnd("%>");
		gt.registerFunctionPackage("strings", new StringUtils());
		gt.registerTag("menu", TestGeneralVarTagBinding.class);
		for (int i = 0; i < 1; i++)
		{

			//			Map result = gt.runScript("/org/beetl/core/lab/hello.txt", Collections.EMPTY_MAP);
			//			System.out.println(result);
			TestUser user = new TestUser("lijz");
			//	user.setLover(new TestUser("miaojun"));
			Template t = gt.getTemplate("/org/beetl/core/lab/hello.txt");
			t.binding("footer", Boolean.TRUE);
			t.binding("user", user);
			t.binding("date1", new Date(10002));
			t.binding("date2", new Date(10002));
			t.binding("total", 15);
			t.binding("list", new ArrayList());

			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			t.renderTo(bs);
			System.out.println(t.getCtx());

		}

	}
}
