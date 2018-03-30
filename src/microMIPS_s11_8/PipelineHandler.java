package microMIPS_s11_8;

public class PipelineHandler {
	// handles the pipeline processing for each stage
	
	public PipelineHandler(){}
	
	public CodeObject IFstage(CodeObject codeObject) {
		// get instruction opcode and address
		String opcode = codeObject.getOpcodeList().get(codeObject.getProcessingList().size());
		int address = codeObject.getOpcodeList().indexOf(opcode) + 4096;
		codeObject.addToProcessingList(opcode);
		// IF/ID.IR <- Mem[PC]
		codeObject.setPipelineRegisterValue(0, opcode);
		// IF/ID.PC <- (if(EX/MEM.cond{EX/MEM.ALUOUTPUT} else {PC+4}))
		// TODO aluoutput if statement
		codeObject.setPipelineRegisterValue(1, Integer.toHexString(address + 4));
		return codeObject;
	}
	
	public CodeObject IDstage(CodeObject codeObject) {
		// TODO detect data hazard for stalling
		// ID/EX.IR <- IF/ID.IR
		codeObject.setPipelineRegisterValue(2, codeObject.getPipelineRegisterValue(0));
		String opcode = (String) codeObject.getPipelineRegisterValue(2);
		// ID/EX.A <- Regs[IF/ID.IR6...10]
		codeObject.setPipelineRegisterValue(3, Integer.toHexString((Integer) codeObject.getRegisterValue(Integer.parseInt(opcode.substring(6, 11), 2))));
		// ID/EX.B <- Regs[IF/ID.IR11...15]
		codeObject.setPipelineRegisterValue(4, Integer.toHexString((Integer) codeObject.getRegisterValue(Integer.parseInt(opcode.substring(11, 16), 2))));
		// ID/EX.Imm <- Regs[IF/ID.IR16...31]
		codeObject.setPipelineRegisterValue(5, Integer.toHexString((Integer) codeObject.getRegisterValue(Integer.parseInt(opcode.substring(16, opcode.length()), 2))));
		return codeObject;
	}
	
	public CodeObject EXstage(CodeObject codeObject) {
		// EX/MEM.IR <- ID/EX.IR
		codeObject.setPipelineRegisterValue(6, codeObject.getPipelineRegisterValue(2));
		String opcode = (String) codeObject.getPipelineRegisterValue(6);
		// check instruction type
		switch(opcode.substring(0, 6)) {
		case "110111": // LD
			// EX/MEM.ALUOUTPUT <- ID/EX.A + ID/EX.Imm
			codeObject.setPipelineRegisterValue(7, Integer.toHexString(Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16) 
					+ Integer.parseInt((String) codeObject.getPipelineRegisterValue(5), 16)));
			// EX/MEM.B <- ID/EX.B
			codeObject.setPipelineRegisterValue(8, codeObject.getPipelineRegisterValue(4));
			// EX/MEM.Cond <- 0
			codeObject.setPipelineRegisterValue(9, 0);
			;break;
		case "111111": // SD
			// EX/MEM.ALUOUTPUT <- ID/EX.A + ID/EX.Imm
			codeObject.setPipelineRegisterValue(7, Integer.toHexString(Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16) 
					+ Integer.parseInt((String) codeObject.getPipelineRegisterValue(5), 16)));
			// EX/MEM.B <- ID/EX.B
			codeObject.setPipelineRegisterValue(8, codeObject.getPipelineRegisterValue(4));
			// EX/MEM.Cond <- 0
			codeObject.setPipelineRegisterValue(9, 0);
			;break;
		case "011001": // DADDIU
			// EX/MEM.ALUOUTPUT <- ID/EX.A (add) ID/EX.Imm
			codeObject.setPipelineRegisterValue(7, Integer.toHexString(Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16) 
					+ Integer.parseInt((String) codeObject.getPipelineRegisterValue(5), 16)));
			// EX/MEM.Cond <- 0
			codeObject.setPipelineRegisterValue(9, 0);
			;break;
		case "001110": // XORI
			// EX/MEM.ALUOUTPUT <- ID/EX.A (xor) ID/EX.Imm
			codeObject.setPipelineRegisterValue(7, Integer.toHexString(Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16) 
					^ Integer.parseInt((String) codeObject.getPipelineRegisterValue(5), 16)));
			// EX/MEM.Cond <- 0
			codeObject.setPipelineRegisterValue(9, 0);
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
			codeObject.setPipelineRegisterValue(9, 0);
			;break;
		case "010111": // BLTZC
			// EX/MEM.ALUOUTPUT <- ID/EX.PC + ID/EX.Imm
			// get pc
			int pc = codeObject.getOpcodeList().indexOf(opcode) + 4096 + 4;
			codeObject.setPipelineRegisterValue(7, Integer.toHexString(pc + Integer.parseInt((String) codeObject.getPipelineRegisterValue(5), 16)));
			// EX/MEM.Cond <- (ID/EX.A op 0)
			if(Integer.parseInt((String) codeObject.getPipelineRegisterValue(3), 16) < 0) {
				codeObject.setPipelineRegisterValue(9, 1);
			}
			else {
				codeObject.setPipelineRegisterValue(9, 0);
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
			codeObject.setPipelineRegisterValue(9, 1);
			;break;
		}
		return codeObject;
	}
	
	public CodeObject MEMstage(CodeObject codeObject) {
		return codeObject;
	}
	
	public CodeObject WBstage(CodeObject codeObject) {
		return codeObject;
	}
}
