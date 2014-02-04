package de.uzk.hki.da.convert;

import org.junit.Test;

public class FidoCLITest {

	@Test
	public void test(){
		CLIConnector cli = new CLIConnector();
//		boolean ret = cli.execute(new String[]{"a"});
		boolean ret = cli.execute(new String[]{"identify"});
		System.out.println(ret);
		;
	}
}
