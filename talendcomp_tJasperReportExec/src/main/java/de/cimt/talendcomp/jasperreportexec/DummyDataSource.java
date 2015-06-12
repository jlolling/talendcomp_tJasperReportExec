/**
 * Copyright 2015 Jan Lolling jan.lolling@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
