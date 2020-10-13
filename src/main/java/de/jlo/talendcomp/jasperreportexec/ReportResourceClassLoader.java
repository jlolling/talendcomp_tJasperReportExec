package de.jlo.talendcomp.jasperreportexec;

import java.net.URL;
import java.net.URLClassLoader;

public class ReportResourceClassLoader extends URLClassLoader {

	public ReportResourceClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
	public void addURL(URL url) {
        super.addURL(url);
    }
    
}
