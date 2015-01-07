package de.cimt.talendcomp.jasperreportexec;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class DummyDataSource implements JRDataSource {
	
	private int numberRecords = 0;
	private int currentIndex = 0;
	
	public DummyDataSource(int numberRecords) {
		this.numberRecords = numberRecords;
	}

	@Override
	public boolean next() throws JRException {
		if (currentIndex < numberRecords) {
			currentIndex++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		return null;
	}

}
