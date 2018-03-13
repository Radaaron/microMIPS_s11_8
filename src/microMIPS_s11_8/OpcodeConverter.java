package microMIPS_s11_8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;

public class OpcodeConverter {
	// converts block of code into corresponding hex opcodes w/ error checking
	
	public OpcodeConverter(){}
	
	public boolean errorCheck(String[] codeLines) {
		// return false if error-free
		// check syntax per line
		for(int i = 0; i < codeLines.length; i++) {
			String instruction = codeLines[i];
			// check format using regex
			if(!(
					instruction.matches("([a-zA-Z][a-zA-Z0-9]+\\s*:\\s*)*(LD|SD)\\s+[R](3[0-1]|[1-2][0-9]|[0-9])\\s*,\\s*[0-9][0-9][0-9][0-9][(][R][0-31][)]")|
					instruction.matches("([a-zA-Z][a-zA-Z0-9]+\\s*:\\s*)*(DADDIU|XORI)\\s+[R](3[0-1]|[1-2][0-9]|[0-9])\\s*,\\s*[R](3[0-1]|[1-2][0-9]|[0-9])\\s*,\\s*[#][0-9][0-9][0-9][0-9]")|
					instruction.matches("([a-zA-Z][a-zA-Z0-9]+\\s*:\\s*)*(DADDU|SLT)\\s+[R](3[0-1]|[1-2][0-9]|[0-9])\\s*,\\s*[R](3[0-1]|[1-2][0-9]|[0-9])\\s*,\\s*[R](3[0-1]|[1-2][0-9]|[0-9])")|
					instruction.matches("([a-zA-Z][a-zA-Z0-9]+\\s*:\\s*)*(BLTZC)\\s+[R](3[0-1]|[1-2][0-9]|[0-9])\\s*,\\s*([a-zA-Z][a-zA-Z0-9]*)")|
					instruction.matches("([a-zA-Z][a-zA-Z0-9]+\\s*:\\s*)*(J)\\s+([a-zA-Z][a-zA-Z0-9]*)")
					)) {
				return true;
			}			
			// only happens when there are no errors
			return false;
		}
		return true;
	}
	
	public ArrayList<String> opcodeConvert(String[] codeLines) {
		// returns arraylist of hex opcodes
		ArrayList<String> opcodeList = new ArrayList<>();
		// convert per line
		for(int i = 0; i < codeLines.length; i++) {
			String[] opcodeLines = new String[6];
			String instruction = codeLines[i];
			// break down into string array per word, ignoring spaces and commas
			if(instruction.contains(" ")){
				// check for label
				Pattern pattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]+\\s*:\\s*)*");
				String[] ins = instruction.split("(\\s+|,\\s*|\\s*,)");
				// convert opcode based on instruction using pattern for converting temp elements to binary
				pattern = Pattern.compile("\\d+");
				Matcher m;
				int a;
				String[] temp = new String[3]; // for parameters
				switch(ins[0]) {
				case "LD":
					opcodeLines[0] = "110111";
					// parse numbers
					m = pattern.matcher(ins[1] + " " + ins[2]);
					a = 0;
					while (m.find()) {
					  temp[a] = m.group();
					  a++;
					}
					// convert to binary as needed
					opcodeLines[1] = decimalToBinary(temp[2]); // base
					opcodeLines[2] = decimalToBinary(temp[0]); // rt
					opcodeLines[3] = hexToBinary(temp[1]); // offset
					opcodeList.add(binaryToHex(opcodeLines[0] + opcodeLines[1] + opcodeLines[2] + opcodeLines[3]));
					;break;
				case "SD":
					opcodeLines[0] = "111111";
					// parse numbers
					m = pattern.matcher(ins[1] + " " + ins[2]);
					a = 0;
					while (m.find()) {
					  temp[a] = m.group();
					  a++;
					}
					// convert to binary as needed
					opcodeLines[1] = decimalToBinary(temp[2]); // base
					opcodeLines[2] = decimalToBinary(temp[0]); // rt
					opcodeLines[3] = hexToBinary(temp[1]); // offset
					opcodeList.add(binaryToHex(opcodeLines[0] + opcodeLines[1] + opcodeLines[2] + opcodeLines[3]));
					;break;
				case "DADDIU":
					opcodeLines[0] = "011001";
					// parse numbers
					m = pattern.matcher(ins[1] + " " + ins[2] + " " + ins[3]);
					a = 0;
					while (m.find()) {
					  temp[a] = m.group();
					  a++;
					}
					// convert to binary as needed
					opcodeLines[1] = decimalToBinary(temp[1]); // rs
					opcodeLines[2] = decimalToBinary(temp[0]); // rt
					opcodeLines[3] = hexToBinary(temp[2]); // immediate
					opcodeList.add(binaryToHex(opcodeLines[0] + opcodeLines[1] + opcodeLines[2] + opcodeLines[3]));
					;break;
				case "XORI":
					opcodeLines[0] = "001110";
					// parse numbers
					m = pattern.matcher(ins[1] + " " + ins[2] + " " + ins[3]);
					a = 0;
					while (m.find()) {
					  temp[a] = m.group();
					  a++;
					}
					// convert to binary as needed
					opcodeLines[1] = decimalToBinary(temp[1]); // rs
					opcodeLines[2] = decimalToBinary(temp[0]); // rt
					opcodeLines[3] = hexToBinary(temp[2]); // immediate
					opcodeList.add(binaryToHex(opcodeLines[0] + opcodeLines[1] + opcodeLines[2] + opcodeLines[3]));
					;break;
				case "DADDU":
					opcodeLines[0] = "000000";
					// parse numbers
					m = pattern.matcher(ins[1] + " " + ins[2] + " " + ins[3]);
					a = 0;
					while (m.find()) {
					  temp[a] = m.group();
					  a++;
					}
					// convert to binary as needed
					opcodeLines[1] = decimalToBinary(temp[1]); // rs
					opcodeLines[2] = decimalToBinary(temp[2]); // rt
					opcodeLines[3] = decimalToBinary(temp[0]); // rd
					opcodeLines[4] = "00000"; // sa
					opcodeLines[5] = "101101"; // func
					opcodeList.add(binaryToHex(opcodeLines[0] + opcodeLines[1] + opcodeLines[2] + opcodeLines[3] + opcodeLines[4] + opcodeLines[5]));
					;break;
				case "SLT":
					opcodeLines[0] = "000000";
					// parse numbers
					m = pattern.matcher(ins[1] + " " + ins[2] + " " + ins[3]);
					a = 0;
					while (m.find()) {
					  temp[a] = m.group();
					  a++;
					}
					// convert to binary as needed
					opcodeLines[1] = decimalToBinary(temp[1]); // rs
					opcodeLines[2] = decimalToBinary(temp[2]); // rt
					opcodeLines[3] = decimalToBinary(temp[0]); // rd
					opcodeLines[4] = "00000"; // sa
					opcodeLines[5] = "101010"; // func
					opcodeList.add(binaryToHex(opcodeLines[0] + opcodeLines[1] + opcodeLines[2] + opcodeLines[3] + opcodeLines[4] + opcodeLines[5]));
					;break;
				case "BLTZC":;break;
				case "J":;break;
				}
			}
		}
		return opcodeList;
	}

	public String decimalToBinary(String in) {
		// returns 5 bit binary
		// parse in into base 10, convert to binary, then back to string
		int temp = Integer.parseInt(in, 10);
		in = Integer.toBinaryString(temp);
		// include leading zeroes
		if(in.length() < 5) {
			String zeroes = "";
			for(int i = 0; i < (5 - in.length()); i++) {
				zeroes = zeroes + "0";
			}
			in = zeroes + in;
		}
		return in;
	}
	
	public String hexToBinary(String in) {
		// returns 16 bit binary
		// parse in into base 16, convert to binary, then back to string
		int temp = Integer.parseInt(in, 16);
		in = Integer.toBinaryString(temp);
		// include leading zeroes
		if(in.length() < 16) {
			String zeroes = "";
			for(int i = 0; i < (16 - in.length()); i++) {
				zeroes = zeroes + "0";
			}
			in = zeroes + in;
		}
		return in;
	}
	
	public static String binaryToHex(String in) {
		// returns 8 digit hex
		// split into half since java doesnt parse 32 bit binary
		int mid = in.length() / 2;
		String[] parts = {in.substring(0, mid), in.substring(mid)};
		// parse in into base 2, convert to hex, then back to string
		for(int i = 0; i < parts.length; i++){
			int temp = Integer.parseInt(parts[i], 2);
			parts[i] = Integer.toHexString(temp);
			// include leading zeroes
			if(parts[i].length() < 4) {
				String zeroes = "";
				for(int j = 0; j < (4 - parts[i].length()); j++) {
					zeroes = zeroes + "0";
				}
				parts[i] = zeroes + parts[i];
			}
		}
		return(parts[0] + parts[1]).toUpperCase();
	}
}
