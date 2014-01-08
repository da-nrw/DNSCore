/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package de.uzk.hki.da.db;

import org.hibernate.Session;
import org.junit.Test;

/**
 * Should prove that static threadLocal session variable in HibernateUtils works 
 * when accessed from different runnables (so we can use it with {@link de.uzk.hki.da.cb.AbstractAction} which 
 * is also runnable).
 * @author Daniel M. de Oliveira
 */
public class HibernateUtilThreadingTest {

	private class Runner extends Thread {

		@Override
		public void run() {
			
			Session session = HibernateUtil.getThreadBoundSession();
			System.out.println(getId()+ ". begin . session in use: " + session.hashCode());

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			session = HibernateUtil.getThreadBoundSession();
			System.out.println(getId()+ ". end. session in use: " + session.hashCode());
			HibernateUtil.closeThreadBoundSession();
		}
		
	}
	
	
	
	@Test
	public void test(){
		
		HibernateUtil.init("src/main/conf/hibernateCentralDB.cfg.xml.inmem");
	
		for (int i=0;i<10;i++){
			
			Thread runner = new Runner();
			runner.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			
		}
		
		
	}
	
	
}
