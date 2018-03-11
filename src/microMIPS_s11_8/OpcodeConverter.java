package microMIPS_s11_8;
import java.util.regex.*;

public class OpcodeConverter {
	// converts block of code into corresponding hex opcodes w/ error checking
	
	public OpcodeConverter(){}
	
	public boolean errorCheck(String[] codeLines) {
		// return false if error-free
		// check syntax per line
		for(int i = 0; i < codeLines.length; i++) {
			String instruction = codeLines[i];
			// break down into string array per word, ignoring spaces
			if(instruction.contains(" ")){
				String[] ins = instruction.split("\\s+");
				for(int j = 0; j < ins.length; j++) {
					System.out.println(ins[j]);
				}
				// check regex syntax based on instruction
				if(ins[0].equals("LD") || ins[0].equals("SD")){
					if(!(ins[1].matches("R[0-31],") && ins[2].matches("[0-9][0-9][0-9][0-9][(]R[0-31][)]"))) {
						return true;
					}
				}
				else if(ins[0].equals("DADDIU") || ins[0].equals("XORI")) {
					
				}
				else if(ins[0].equals("DADDU") || ins[0].equals("SLT")) {
					
				}
				else if(ins[0].equals("BLTZC")) {
					
				}
				else if(ins[0].equals("J")){
					
				}
				else {
					return true;
				}
				// only happens when there are no errors
				return false;
			}
		}
		return true;
	}
}
