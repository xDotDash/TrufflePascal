package cz.cuni.mff.d3s.trupple.tests;

import org.junit.Test;

public class CaseTest extends UnitTest {

	@Test
	public void simpleTest() {
		String code = "begin\n" + 
				" case 3 of\n" + 
				"  1:begin write('Star Wars'); end;	\n" + 
				"  2:write('The Lord of the Rings'); \n" + 
				"  3:write('Game of Thrones');\n" + 
				"  4:begin write('Warcraft'); end;  \n" + 
				" end;\n" + 
				"end.";

		String output = "Game of Thrones";
		test(code, output);
	}
	
	/*
	@Test
	public void defaultTest() {
		String code = "begin\n" + 
				" case 8 of\n" + 
				"  1:begin write('Star Wars'); end;	\n" + 
				"  2:write('The Lord of the Rings'); \n" + 
				"  3:write('Game of Thrones');\n" + 
				"  4:begin write('Warcraft'); end;  \n" + 
				"  else write('ASDF Movie'); \n" + 
				" end;\n" + 
				"end.";

		String output = "ASDF Movie";
		test(code, output);
	}
	*/
}