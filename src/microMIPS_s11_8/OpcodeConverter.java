package microMIPS_s11_8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
					instruction.matches("([a-zA-Z][a-zA-Z0-9]+\\s*:\\s*)*(BLTZC)\\s+[R](3[0-1]|[1-2][0-9]|[1-9])\\s*,\\s*([a-zA-Z][a-zA-Z0-9]*)+")|
					instruction.matches("([a-zA-Z][a-zA-Z0-9]+\\s*:\\s*)*(J)\\s+([a-zA-Z][a-zA-Z0-9]*)+")
					)) {
				return true;
			}			
			// only happens when there are no errors
			return false;
		}
		return true;
	}
	
	public CodeObject opcodeConvert(String[] codeLines) {
		// returns codeObject with opcodeList, labelPointers, and numLines 
		CodeObject codeObject = new CodeObject();
		ArrayList<String> opcodeList = new ArrayList<>();
		int numLines = codeLines.length;
		ArrayList<HashMap<String, Integer>> labelPointers = new ArrayList<>();
		// check for labels
		Pattern pattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]+\\s*:\\s*)");
		Matcher m;
		for(int i = 0; i < codeLines.length; i++) {
			String instruction = codeLines[i];
			m = pattern.matcher(instruction);
			if(m.find()) {
				// if a label is found, store the label name and instruction line number, then remove it
				HashMap<String,Integer> map = new HashMap<String,Integer>();
				String labelName = instruction.substring(0, m.end()); // with colon (:)
				pattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]+\\s*)*");
				Matcher m2 = pattern.matcher(labelName);
				if(m2.find()) {
					map.put(labelName.substring(0, m2.end()), i); // store without colon
				    labelPointers.add(map);
				}
			    instruction = instruction.replaceAll("([a-zA-Z][a-zA-Z0-9]+\\s*:\\s*)", "");
			}
			codeLines[i] = instruction;
		}
		// convert per line
		for(int i = 0; i < codeLines.length; i++) {
			String[] opcodeLines = new String[6];
			String instruction = codeLines[i];
			if(instruction.contains(" ")){
				// split instruction based on spaces and commas where appropriate
				String[] ins = instruction.split("(\\s+|,\\s*|\\s*,)");
				// convert opcode based on instruction using pattern for converting temp elements to binary
				pattern = Pattern.compile("\\d+");
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
					opcodeLines[1] = decimalToBinary(temp[2], 5); // base
					opcodeLines[2] = decimalToBinary(temp[0], 5); // rt
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
					opcodeLines[1] = decimalToBinary(temp[2], 5); // base
					opcodeLines[2] = decimalToBinary(temp[0], 5); // rt
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
					opcodeLines[1] = decimalToBinary(temp[1], 5); // rs
					opcodeLines[2] = decimalToBinary(temp[0], 5); // rt
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
					opcodeLines[1] = decimalToBinary(temp[1], 5); // rs
					opcodeLines[2] = decimalToBinary(temp[0], 5); // rt
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
					opcodeLines[1] = decimalToBinary(temp[1], 5); // rs
					opcodeLines[2] = decimalToBinary(temp[2], 5); // rt
					opcodeLines[3] = decimalToBinary(temp[0], 5); // rd
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
					opcodeLines[1] = decimalToBinary(temp[1], 5); // rs
					opcodeLines[2] = decimalToBinary(temp[2], 5); // rt
					opcodeLines[3] = decimalToBinary(temp[0], 5); // rd
					opcodeLines[4] = "00000"; // sa
					opcodeLines[5] = "101010"; // func
					opcodeList.add(binaryToHex(opcodeLines[0] + opcodeLines[1] + opcodeLines[2] + opcodeLines[3] + opcodeLines[4] + opcodeLines[5]));
					;break;
				case "BLTZC":
					opcodeLines[0] = "010111";
					// parse numbers
					m = pattern.matcher(ins[1]);
					a = 0;
					while (m.find()) {
						temp[a] = m.group();
						a++;
					}
					// convert to binary as needed
					opcodeLines[1] = decimalToBinary(temp[0], 5); // rs = rt
					opcodeLines[2] = decimalToBinary(temp[0], 5); // rt
					// parse label
					pattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]+\\s*)+");
					String label = null;
					m = pattern.matcher(ins[2]);
					a = 0;
					while (m.find()) {
						label = m.group();
						a++;
					}
					// get label line num, use /a/ since it wont be used anymore
					for(int j = 0; j < labelPointers.size(); j++) {
						if(labelPointers.get(j).containsKey(label)) {
							a = labelPointers.get(j).get(label);
						}
					}
					a = (a - i) - 1; // current line num - label line num + 1
					opcodeLines[3] = decimalToBinary(Integer.toString(a), 16);
					opcodeList.add(binaryToHex(opcodeLines[0] + opcodeLines[1] + opcodeLines[2] + opcodeLines[3]));
					;break;
				case "J":
					opcodeLines[0] = "000010";
					// parse label
					pattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]+\\s*)+");
					String index = null;
					m = pattern.matcher(ins[1]);
					a = 0;
					while (m.find()) {
						index = m.group();
						a++;
					}
					// get label line num, use /a/ since it wont be used anymore
					for(int j = 0; j < labelPointers.size(); j++) {
						if(labelPointers.get(j).containsKey(index)) {
							a = labelPointers.get(j).get(index);
						}
					}
					opcodeLines[1] = decimalToBinary(Integer.toString(a), 26);
					opcodeList.add(binaryToHex(opcodeLines[0] + opcodeLines[1]));
					;break;
				}
			}
		}
		codeObject.setOpcodeList(opcodeList);
		codeObject.setNumLines(numLines);
		codeObject.setLabelPointers(labelPointers);
		return codeObject;
	}

	public String decimalToBinary(String in, int n) {
		// returns n bit binary with sign extension
		// parse in into base 10, convert to binary, then back to string
		int temp = Integer.parseInt(in, 10);
		in = Integer.toBinaryString(temp);
		// include leading zeroes
		if(in.length() < n) {
			String zeroes = "";
			for(int i = 0; i < (n - in.length()); i++) {
				zeroes = zeroes + "0";
			}
			in = zeroes + in;
		} 
		else if (in.length() > n) {
			int extra = in.length() - n;
			in = in.substring(extra, in.length());
		}
		return in;
	}
	
	public String hexToBinary(String in) {
		// returns 16 bit binary with leading zeroes
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
	
	public String binaryToHex(String in) {
		// returns 8 digit hex with leading zeroes
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
