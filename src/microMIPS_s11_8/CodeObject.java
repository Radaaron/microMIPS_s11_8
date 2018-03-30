package microMIPS_s11_8;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeObject {
	// stores all the information about the code
	
	// fields
	private ArrayList<String> opcodeList;
	private int numLines;
	private ArrayList<HashMap<String, Integer>> labelPointers;
	private Object[] registers;
	private Object[] memory;
	private Object[] pipelineRegisters;
	private ArrayList<ArrayList<Object>> pipelineMap;
	private ArrayList<String> processingList;
	private ArrayList<String> finishedList;
	
	public CodeObject() {
		this.opcodeList = new ArrayList<>();
		this.numLines = 0;
		this.labelPointers = new ArrayList<>();
		this.registers = new Object[32];
		this.memory = new Object[8192];
		this.pipelineRegisters = new Object[15];
		this.pipelineMap = new ArrayList<>();
		this.processingList = new ArrayList<>();
		this.finishedList = new ArrayList<>();
	}

	public ArrayList<String> getFinishedList() {
		return finishedList;
	}

	public void addToFinishedList(String opcode) {
		this.finishedList.add(opcode);
	}
	
	public ArrayList<String> getProcessingList() {
		return processingList;
	}

	public void addToProcessingList(String opcode) {
		this.processingList.add(opcode);
	}

	public Object getPipelineMapValue(int index) {
		return this.pipelineMap.get(index);
	}

	public void setPipelineMapValue(int index, int index2, Object val) {
		this.pipelineMap.get(index).set(index2, val);
	}
	
	public Object getPipelineRegisterValue(int index) {
		return pipelineRegisters[index];
	}
	
	public Object[] getPipelineRegisters() {
		return this.pipelineRegisters;
	}
	
	public void setPipelineRegisterValue(int index, Object val) {
		this.pipelineRegisters[index] = val;
	}
	
	public Object getRegisterValue(int index) {
		return registers[index];
	}
	
	public Object[] getRegisters() {
		return this.registers;
	}
	
	public void setRegisterValue(int index, Object val) {
		this.registers[index] = val;
	}
	
	public Object getMemoryValue(int index) {
		return memory[index];
	}
	
	public Object[] getMemory() {
		return this.memory;
	}
	
	public void setMemoryValue(int index, Object val) {
		this.memory[index] = val;
	}

	public ArrayList<String> getOpcodeList() {
		return opcodeList;
	}

	public void setOpcodeList(ArrayList<String> opcodeList) {
		this.opcodeList = opcodeList;
	}

	public int getNumLines() {
		return numLines;
	}

	public void setNumLines(int numLines) {
		this.numLines = numLines;
	}

	public ArrayList<HashMap<String, Integer>> getLabelPointers() {
		return labelPointers;
	}

	public void setLabelPointers(ArrayList<HashMap<String, Integer>> labelPointers) {
		this.labelPointers = labelPointers;
	}
	
	

}
