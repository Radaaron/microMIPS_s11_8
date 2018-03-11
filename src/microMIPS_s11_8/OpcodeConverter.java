package microMIPS_s11_8;

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
					if(!(ins[1].matches("R(3[0-1]|[1-2][0-9]|[0-9]),") && ins[2].matches("[0-9][0-9][0-9][0-9][(]R[0-31][)]") && ins.length == 3)) {
						return true;
					}
				}
				else if(ins[0].equals("DADDIU") || ins[0].equals("XORI")) {
					if(!(ins[1].matches("R(3[0-1]|[1-2][0-9]|[0-9]),") && ins[2].matches("R(3[0-1]|[1-2][0-9]|[0-9]),") && ins[3].matches("[#][0-9][0-9][0-9][0-9]") && ins.length == 4)) {
						return true;
					}
				}
				else if(ins[0].equals("DADDU") || ins[0].equals("SLT")) {
					if(!(ins[1].matches("R(3[0-1]|[1-2][0-9]|[0-9]),") && ins[2].matches("R(3[0-1]|[1-2][0-9]|[0-9]),") && ins[3].matches("R(3[0-1]|[1-2][0-9]|[0-9])") && ins.length == 4)) {
						return true;
					}
				}
				else if(ins[0].equals("BLTZC")) {
					if(!(ins[1].matches("R(3[0-1]|[1-2][0-9]|[0-9]),") && ins.length == 3)) {
						return true;
					}
				}
				else if(ins[0].equals("J")){
					if(!(ins.length == 2)) {
						return true;
					}
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
	
	public String[] opcodeConvert(String[] codeLines) {
		String[] opcodeLines = new String[7];
		// convert per line
		for(int i = 0; i < codeLines.length; i++) {
			String instruction = codeLines[i];
			// break down into string array per word, ignoring spaces
			if(instruction.contains(" ")){
				String[] ins = instruction.split("\\s+");
				for(int j = 0; j < ins.length; j++) {
					System.out.println(ins[j]);
				}
				// convert opcode based on instruction
				switch(ins[0]) {
				case "LD":
					opcodeLines[0] = "110111";
					opcodeLines[1] = ;
					opcodeLines[2] = ;
					opcodeLines[3] = ;
					opcodeLines[4] = ;
					opcodeLines[5] = ;
					opcodeLines[6] = ;
					;break;
				case "SD":;break;
				case "DADDIU":;break;
				case "XORI":;break;
				case "DADDU":;break;
				case "SLT":;break;
				case "BLTZC":;break;
				case "J":;break;
				}
			}
		}
		return opcodeLines;
	}
}
