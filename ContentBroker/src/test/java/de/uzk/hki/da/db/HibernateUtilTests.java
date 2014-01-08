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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.hibernate.classic.Session;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;


/**
 * The Class HibernateUtilTests.
 */
public class HibernateUtilTests {

	
	/**
	 * The Class Runner.
	 */
	public class Runner implements Runnable{

		/** The session. */
		Session session;
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			session = HibernateUtil.getThreadBoundSession();
			if (session==null) throw new RuntimeException("Couldn't create session");
		}
		
		/**
		 * Gets the session.
		 *
		 * @return the session
		 */
		public Session getSession(){
			if (session==null) throw new RuntimeException("Couldn't retrieve session");
			return session;
		}
		
	}
	
	/**
	 * Sets the up.
	 */
	@BeforeClass
	public static void setUp(){
		HibernateUtil.init("src/main/conf/hibernateCentralDB.cfg.xml.inmem");
	}
	
	/**
	 * Test create new session per thread.
	 */
	@Test
	public void testCreateNewSessionPerThread(){
		
		TaskExecutor exec = new SimpleAsyncTaskExecutor();
		Runner run1 = new Runner();
		Runner run2 = new Runner();
		run1.run();
		run2.run();
		
		exec.execute(run1);
		exec.execute(run2);
		
		// work a little bit
		String a="a";
		for (int i=1;i<1000000;i++)  {a="abd"+a; a=a.substring(3);}
		
		System.out.println(run1.getSession().hashCode());
		System.out.println(run2.getSession().hashCode());
		if (run1.getSession().equals(run2.getSession())) {
			run1.getSession().close();
			fail();
		}
		
		run1.getSession().close();
		run2.getSession().close();
	}
	
	/**
	 * Test dont create new session in same thread.
	 */
	@Test
	public void testDontCreateNewSessionInSameThread(){
		
		Runner run1 = new Runner();
		run1.run();
		Session sessTest1 = run1.getSession();
		System.out.println(run1.getSession().hashCode());
		run1.run();
		Session sessTest2 = run1.getSession();
		System.out.println(run1.getSession().hashCode());		

		assertSame(sessTest1,sessTest2);
		run1.getSession().close();
	}
	
}
