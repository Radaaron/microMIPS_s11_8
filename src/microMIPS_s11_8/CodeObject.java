package microMIPS_s11_8;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeObject {
	// stores all the information about the code
	
	// fields
	private ArrayList<String> opcodeList;
	private int numLines;
	private ArrayList<HashMap<String, Integer>> labelPointers;
	
	public CodeObject() {
		this.opcodeList = new ArrayList<>();
		this.numLines = 0;
		this.labelPointers = new ArrayList<>();
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
