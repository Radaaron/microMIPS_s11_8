package microMIPS_s11_8;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CodeObject {
	// stores all the information about the code
	
	// fields
	private ArrayList<String> opcodeList;
	private int numLines;
	private HashMap<String, Integer> labelMap;
	private Object[] registers;
	private boolean[] registersUsed;
	private Object[] memory;
	private Object[] pipelineRegisters;
	private Object[] pipelineBuffers;
	private ArrayList<ArrayList<Object>> pipelineMap;
	private int programCounter;
	private boolean isFinished;
	private boolean isStarted;
	
	public CodeObject() {
		this.opcodeList = new ArrayList<>();
		this.numLines = 0;
		this.labelMap = new HashMap<>();
		this.registers = new Object[32];
		for(int i = 0; i< registers.length; i++) {
			this.registers[i] = "0000000000000000";
		}
		this.registersUsed = new boolean[32];
		for(int i = 0; i< registersUsed.length; i++) {
			this.registersUsed[i] = false;
		}
		this.memory = new Object[8192];
		for(int i = 0; i< memory.length; i++) {
			this.memory[i] = "00";
		}
		this.pipelineRegisters = new Object[20];
		for(int i = 0; i< pipelineRegisters.length; i++) {
			this.pipelineRegisters[i] = "N/A";
		}
		this.pipelineBuffers = new Object[20];
		for(int i = 0; i< pipelineBuffers.length; i++) {
			this.pipelineBuffers[i] = "N/A";
		}
		this.pipelineMap = new ArrayList<>();
		this.programCounter = 4096; // start at 0x01000
		this.isFinished = false;
		this.isStarted = false;
	}
	
	public boolean isFinished() {
		return this.isFinished;
	}
	
	public void setFinished() {
		this.isFinished = true;
	}
	
	public boolean isStarted() {
		return this.isStarted;
	}
	
	public void setStarted() {
		this.isStarted = true;
	}
	
	public String getInstruction() {
		String ins = "";
		for(int i = this.programCounter; i < this.programCounter + 4; i++) {
			ins = this.memory[i] + ins;
		}
		return ins;
	}

	public int getProgramCounter() {
		return this.programCounter;
	}

	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
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
	
	public Object setPipelineRegisterValue(int index, Object val) {
		this.pipelineRegisters[index] = val;
		return val;
	}
	
	public Object getRegisterValue(int index) {
		return this.registers[index];
	}
	
	public Object[] getRegisters() {
		return this.registers;
	}
	
	public void setRegistersUsedValue(int index, boolean val) {
		this.registersUsed[index] = val;
	}
	
	public boolean getRegistersUsedValue(int index) {
		return this.registersUsed[index];
	}
	
	public boolean[] getRegistersUsed() {
		return this.registersUsed;
	}
	
	public void setRegisterValue(int index, Object val) {
		this.registers[index] = val;
	}
	
	public Object getPipelineBufferValue(int index) {
		return this.pipelineBuffers[index];
	}
	
	public Object[] getPipelineBuffers() {
		return this.pipelineBuffers;
	}
	
	public void setPipelineBufferValue(int index, Object val) {
		this.pipelineBuffers[index] = val;
	}
	
	public Object getMemoryValue(int index) {
		return memory[index];
	}
	
	public Object[] getMemory() {
		return this.memory;
	}
	
	public void storeInMemory(int index, String val) { // just used for store
		if (val.length() % 2 == 1) {
			// odd
			val = "0" + val;
		} 
		for(int j = val.length(); j > 0; j--) {
			this.memory[index] = val.substring(j - 2, j);
			j--;
			index++;
		}
	}
	
	public String loadFromMemory(int index) { // just used for load
		String load = "";
		for(int i = index; i < index + 8; i++) {
			load = this.memory[i] + load;
		}
		return load;
	}

	public ArrayList<String> getOpcodeList() {
		return opcodeList;
	}

	public void setOpcodeList(ArrayList<String> opcodeList) {
		this.opcodeList = opcodeList;
		// add the opcode instructions to memory as well
		int k = 4096;
		for(int i = 0; i < this.opcodeList.size(); i++) {
			for(int j = 8; j > 0; j--) {
				this.memory[k] = this.opcodeList.get(i).substring(j - 2, j);
				j--;
				k++;
			}
		}
	}

	public int getNumLines() {
		return numLines;
	}

	public void setNumLines(int numLines) {
		this.numLines = numLines;
	}

	public HashMap<String, Integer> getLabelMap() {
		return this.labelMap;
	}

	public void setLabelMap(HashMap<String, Integer> labelMap) {
		this.labelMap = labelMap;
	}
	
	

}
