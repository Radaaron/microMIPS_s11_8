package microMIPS_s11_8;

import java.util.ArrayList;
import java.util.HashMap;

public class PipelineHandler {
	// handles the pipeline processing for each stage
	
	private Converter converter;
	
	public PipelineHandler(){
		converter = new Converter();
	}
	
	public CodeObject IFstage(CodeObject codeObject) {
		// IF/ID.IR <- Mem[PC]
		String opcode = codeObject.getNextInstruction();
		if(opcode.equals("00000000")) { // last instruction
			// reset values
			codeObject.setPipelineRegisterValue(0, "N/A");
			codeObject.setPipelineRegisterValue(1, "N/A");
			return codeObject;
		}
		codeObject.setPipelineRegisterValue(0, opcode);
		opcode = converter.hexToBinary(opcode, 32); // convert to binary
		// IF/ID.PC <- (if(EX/MEM.cond{EX/MEM.ALUOUTPUT} else {PC+4}))
		if(((String)codeObject.getPipelineRegisterValue(9)).equals("1")) {
			codeObject.setProgramCounter(Integer.parseInt((String) codeObject.getPipelineRegisterValue(7), 16));
			codeObject.setPipelineRegisterValue(1, Integer.toHexString(Integer.parseInt((String) codeObject.getPipelineRegisterValue(7), 16)));
		}
		else {
			codeObject.setProgramCounter(codeObject.getProgramCounter() + 4);
			codeObject.setPipelineRegisterValue(1, Integer.toHexString(codeObject.getProgramCounter()));
		}
		codeObject.setStarted();
		return codeObject;
	}
	
	public CodeObject IDstage(CodeObject codeObject) {
		// TODO detect data hazard for stalling
		if(((String) codeObject.getPipelineRegisterValue(0)).equals("N/A")) {
			codeObject.setPipelineRegisterValue(2, "N/A");
			codeObject.setPipelineRegisterValue(3, "N/A");
			codeObject.setPipelineRegisterValue(4, "N/A");
			codeObject.setPipelineRegisterValue(5, "N/A");
			if(((String) codeObject.getPipelineRegisterValue(1)).equals("N/A") && codeObject.isStarted()) {
				codeObject.setPipelineRegisterValue(15, "0");
			}
			return codeObject;
		}
		// ID/EX.IR <- IF/ID.IR
		codeObject.setPipelineRegisterValue(2, codeObject.getPipelineRegisterValue(0));
		String opcode = (String) codeObject.getPipelineRegisterValue(2);
		opcode = converter.hexToBinary(opcode, 32); // convert to binary
		// ID/EX.A <- Regs[IF/ID.IR6...10]
		codeObject.setPipelineRegisterValue(3, Integer.toHexString((Integer) codeObject.getRegisterValue(Integer.parseInt(opcode.substring(6, 11), 2))));
		// ID/EX.B <- Regs[IF/ID.IR11...15]
		codeObject.setPipelineRegisterValue(4, Integer.toHexString((Integer) codeObject.getRegisterValue(Integer.parseInt(opcode.substring(11, 16), 2))));
		// ID/EX.Imm <- Regs[IF/ID.IR16...31]
		codeObject.setPipelineRegisterValue(5, Integer.toHexString((Integer.parseInt(opcode.substring(16, opcode.length()), 2))));
		// HIDDEN: ID.NPC <- // IF/ID.PC
		codeObject.setPipelineRegisterValue(15, codeObject.getPipelineRegisterValue(1));
		return codeObject;
	}
	
	public CodeObject EXstage(CodeObject codeObject) {
		if(((String) codeObject.getPipelineRegisterValue(2)).equals("N/A")) {
			codeObject.setPipelineRegisterValue(6, "N/A");
			codeObject.setPipelineRegisterValue(7, "N/A");
			codeObject.setPipelineRegisterValue(8, "N/A");
			codeObject.setPipelineRegisterValue(9, "N/A");
			if(((String) codeObject.getPipelineRegisterValue(15)).equals("0")) {
				codeObject.setPipelineRegisterValue(16, "0");
			}
			return codeObject;
		}
		// EX/MEM.IR <- ID/EX.IR
		codeObject.setPipelineRegisterValue(6, codeObject.getPipelineRegisterValue(2));
		String opcode = (String) codeObject.getPipelineRegisterValue(6);
		opcode = converter.hexToBinary(opcode, 32); // convert to binary
		// get hidden npc
		int npc = Integer.parseInt((String) codeObject.getPipelineRegisterValue(15), 16);
		// check instruction type
		switch(opcode.substring(0, 6)) {
		case "110111": // LD
			// EX/MEM.ALUOUTPUT <- ID/EX.A + ID/EX.Imm
			codeObject.setPipelineRegisterValue(7, Integer.toHexString(Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16) 
					+ Integer.parseInt((String) codeObject.getPipelineRegisterValue(5), 16)));
			// EX/MEM.B <- ID/EX.B
			codeObject.setPipelineRegisterValue(8, codeObject.getPipelineRegisterValue(4));
			// EX/MEM.Cond <- 0
			codeObject.setPipelineRegisterValue(9, "0");
			;break;
		case "111111": // SD
			// EX/MEM.ALUOUTPUT <- ID/EX.A + ID/EX.Imm
			codeObject.setPipelineRegisterValue(7, Integer.toHexString(Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16) 
					+ Integer.parseInt((String) codeObject.getPipelineRegisterValue(5), 16)));
			// EX/MEM.B <- ID/EX.B
			codeObject.setPipelineRegisterValue(8, codeObject.getPipelineRegisterValue(4));
			// EX/MEM.Cond <- 0
			codeObject.setPipelineRegisterValue(9, "0");
			;break;
		case "011001": // DADDIU
			// EX/MEM.ALUOUTPUT <- ID/EX.A (add) ID/EX.Imm
			codeObject.setPipelineRegisterValue(7, Integer.toHexString(Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16) 
					+ Integer.parseInt((String) codeObject.getPipelineRegisterValue(5), 16)));
			// EX/MEM.Cond <- 0
			codeObject.setPipelineRegisterValue(9, "0");
			;break;
		case "001110": // XORI
			// EX/MEM.ALUOUTPUT <- ID/EX.A (xor) ID/EX.Imm
			codeObject.setPipelineRegisterValue(7, Integer.toHexString(Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16) 
					^ Integer.parseInt((String) codeObject.getPipelineRegisterValue(5), 16)));
			// EX/MEM.Cond <- 0
			codeObject.setPipelineRegisterValue(9, "0");
			;break;
		case "000000": // DADDU or SLT
			// EX/MEM.ALUOUTPUT <- ID/EX.A func ID/EX.B
			// check func
			switch(opcode.substring(25, opcode.length())) {
			case "101101": // DADDU
				codeObject.setPipelineRegisterValue(7, Integer.toHexString(Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16)
						+ Integer.parseInt((String) codeObject.getPipelineRegisterValue(4), 16)));
				;break;
			case "101010": // SLT
				if(Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16) < Integer.parseInt((String) codeObject.getPipelineRegisterValue(4), 16)) {
					codeObject.setPipelineRegisterValue(7, Integer.toHexString(1));
				} 
				else {
					codeObject.setPipelineRegisterValue(7, Integer.toHexString(0));
				}
				;break;
			}
			// EX/MEM.Cond <- 0
			codeObject.setPipelineRegisterValue(9, "0");
			;break;
		case "010111": // BLTZC
			// EX/MEM.ALUOUTPUT <- ID/EX.PC + ID/EX.Imm
			codeObject.setPipelineRegisterValue(7, Integer.toHexString(npc + Integer.parseInt((String) codeObject.getPipelineRegisterValue(5), 16)));
			// EX/MEM.Cond <- (ID/EX.A op 0)
			if(Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16) < 0) {
				codeObject.setPipelineRegisterValue(9, "1");
			}
			else {
				codeObject.setPipelineRegisterValue(9, "0");
			}
			;break;
		case "000010": // J
			// EX/MEM.ALUOUTPUT <- (ID/EX.A + ID/EX.B + ID/EX.Imm) << 2
			int immediate = Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16) 
					+ Integer.parseInt((String) codeObject.getPipelineRegisterValue(4), 16)
					+ Integer.parseInt((String) codeObject.getPipelineRegisterValue(5), 16);
			immediate = immediate << 2;
			codeObject.setPipelineRegisterValue(7, Integer.toHexString(immediate));
			// EX/MEM.Cond <- 1
			codeObject.setPipelineRegisterValue(9, "1");
			;break;
		}
		// HIDDEN: EX.NPC <- ID.NPC
		codeObject.setPipelineRegisterValue(16, codeObject.getPipelineRegisterValue(15));
		return codeObject;
	}
	
	public CodeObject MEMstage(CodeObject codeObject) {
		if(((String) codeObject.getPipelineRegisterValue(6)).equals("N/A")) {
			codeObject.setPipelineRegisterValue(10, "N/A");
			codeObject.setPipelineRegisterValue(11, "N/A");
			codeObject.setPipelineRegisterValue(12, "N/A");
			codeObject.setPipelineRegisterValue(13, "N/A");
			if(((String) codeObject.getPipelineRegisterValue(16)).equals("0")) {
				codeObject.setPipelineRegisterValue(17, "0");
				codeObject.setFinished();
			}
			return codeObject;
		}
		// MEM/WB.IR <- EX/MEM.IR
		codeObject.setPipelineRegisterValue(10, codeObject.getPipelineRegisterValue(6));
		String opcode = (String) codeObject.getPipelineRegisterValue(10);
		opcode = converter.hexToBinary(opcode, 32); // convert to binary
		// check instruction type
		switch(opcode.substring(0, 6)) {
		case "110111": // LD
			// MEM/WB.LMD <- Mem[EX/MEM.ALUOUTPUT]
			codeObject.setPipelineRegisterValue(12, codeObject.loadFromMemory(Integer.parseInt((String) codeObject.getPipelineRegisterValue(7), 16)));
			;break;
		case "111111": // SD
			// Mem[EX/MEM.ALUOUTPUT] <- EX/MEM.B
			codeObject.storeInMemory(Integer.parseInt((String) codeObject.getPipelineRegisterValue(7), 16), (String) codeObject.getPipelineRegisterValue(8));
			;break;
		case "011001": // DADDIU
			// MEM/WB.ALUOUTPUT <- EX/MEM.ALUOUTPUT
			codeObject.setPipelineRegisterValue(11, codeObject.getPipelineRegisterValue(7));
			;break;
		case "001110": // XORI
			// MEM/WB.ALUOUTPUT <- EX/MEM.ALUOUTPUT
			codeObject.setPipelineRegisterValue(11, codeObject.getPipelineRegisterValue(7));
			;break;
		case "000000": // DADDU or SLT
			// MEM/WB.ALUOUTPUT <- EX/MEM.ALUOUTPUT
			codeObject.setPipelineRegisterValue(11, codeObject.getPipelineRegisterValue(7));
			;break;
		case "010111": // BLTZC
			// nothing
			;break;
		case "000010": // J
			// nothing
		}
		// HIDDEN: MEM.NPC <- EX.NPC
		codeObject.setPipelineRegisterValue(17, codeObject.getPipelineRegisterValue(16));
		return codeObject;
	}
	
	public CodeObject WBstage(CodeObject codeObject) {
		// check previous cycle exists
		if(((String) codeObject.getPipelineRegisterValue(10)).equals("N/A")) {
			codeObject.setPipelineRegisterValue(14, "N/A");
			codeObject.setPipelineRegisterValue(18, "0");
			return codeObject;
		}
		String opcode = (String) codeObject.getPipelineRegisterValue(10);
		opcode = converter.hexToBinary(opcode, 32); // convert to binary
		// check instruction type
		switch(opcode.substring(0, 6)) {
		case "110111": // LD
			// Regs[MEM/WB.IR11...15] <- MEM/WB.LMD
			codeObject.setRegisterValue(Integer.parseInt(opcode.substring(11, 16), 2), signExtend((String) codeObject.getPipelineRegisterValue(12)));
			;break;
		case "111111": // SD
			// nothing
			;break;
		case "011001": // DADDIU
			// Regs[MEM/WB.IR11...15] <- MEM/WB.ALUOUTPUT
			codeObject.setRegisterValue(Integer.parseInt(opcode.substring(11, 16), 2), signExtend((String) codeObject.getPipelineRegisterValue(11)));
			;break;
		case "001110": // XORI
			// Regs[MEM/WB.IR11...15] <- MEM/WB.ALUOUTPUT
			codeObject.setRegisterValue(Integer.parseInt(opcode.substring(11, 16), 2), signExtend((String) codeObject.getPipelineRegisterValue(11)));
			;break;
		case "000000": // DADDU or SLT
			// Regs[MEM/WB.IR16...20] <- MEM/WB.ALUOUTPUT
			codeObject.setRegisterValue(Integer.parseInt(opcode.substring(16, 21), 2), signExtend((String) codeObject.getPipelineRegisterValue(11)));
			;break;
		case "010111": // BLTZC
			// nothing
			;break;
		case "000010": // J
			// nothing
		}
		// HIDDEN: WB.NPC <- MEM.NPC
		codeObject.setPipelineRegisterValue(18, codeObject.getPipelineRegisterValue(17));
		return codeObject;
	}
	
	public String signExtend(String in) {
		String extend = "";
		if(in.length() >= 4 && Character.getNumericValue(in.charAt(0)) >= 8) {
			for(int i = 0; i < (16 - in.length()); i++) {
				extend = extend + "F";
			}
		} else {
			for(int i = 0; i < (16 - in.length()); i++) {
				extend = extend + "0";
			}
		}
		return in = extend + in;
	}
	
	public int getLineNum(String npc) {
		// check if valid
		if(npc.equals("N/A")) {
			return -1;
		}
		int lineNum = Integer.parseInt(npc, 16);
		lineNum -= 4100; // - 4 - 4096
		lineNum /= 4; // instructions are 4 memory spaces long
		return lineNum;
	}
	
	public int max(int first, int... rest) {
	    int ret = first;
	    for (int val : rest) {
	        ret = Math.max(ret, val);
	    }
	    return ret;
	}
	
	public Object[] getCycleInfo(CodeObject codeObject) {
		HashMap<String, Integer> map = new HashMap<>();
		// put status and corresponding lineNum from npc's
		map.put("IF", getLineNum((String) codeObject.getPipelineRegisterValue(1))); // IF/ID.PC
		map.put("ID", getLineNum((String) codeObject.getPipelineRegisterValue(15))); // ID.NPC
		map.put("EX", getLineNum((String) codeObject.getPipelineRegisterValue(16))); // EX.NPC
		map.put("MEM", getLineNum((String) codeObject.getPipelineRegisterValue(17))); // MEM.NPC
		map.put("WB", getLineNum((String) codeObject.getPipelineRegisterValue(18))); // WB.NPC
		ArrayList<String> tempList = new ArrayList<>();
		// populate tempList with status with index matching until last status
		int end = max(map.get("IF"), map.get("ID"), map.get("EX"), map.get("MEM"), map.get("WB"));
		for(int i = 0; i <= end; i++) {
			if(i == map.get("IF")) {
				tempList.add("IF");
			}
			else if(i == map.get("ID")) {
				tempList.add("ID");
			}
			else if(i == map.get("EX")) {
				tempList.add("EX");
			}
			else if(i == map.get("MEM")) {
				tempList.add("MEM");
			}
			else if(i == map.get("WB")) {
				tempList.add("WB");
			}
			else {
				tempList.add("");
			}
		}
		// convert into array
		return tempList.toArray(new Object[tempList.size()]);
	}
	
	public void printMap(HashMap map) {
		System.out.println("--MAP START--"); 
		for (Object name: map.keySet()){
            String key = name.toString();
            String value = map.get(name).toString();  
            System.out.println(key + " " + value); 
		}
		System.out.println("--MAP END--"); 
	}
	
	public void printArrayList(ArrayList list) {
		System.out.println("--LIST START--"); 
		for (Object name : list) {
			System.out.println(name);
		}
		System.out.println("--LIST END--"); 
	}
}
