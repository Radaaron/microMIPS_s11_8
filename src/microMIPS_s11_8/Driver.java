package microMIPS_s11_8;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Driver {
	// main and gui class
	static String[] codeLines;
	static ArrayList<String> opcodeList;
	String[] opcodeColumn;
	static Object[][] opcodeData = {};
	static OpcodeConverter opcodeConverter;
	
	public static void main(String[] args) {
		opcodeConverter = new OpcodeConverter();
		Scanner a = new Scanner(System.in);
		JFrame mainFrame = new JFrame();
		mainFrame.setSize(new Dimension(1400, 800));
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setTitle("microMIPS");
		mainFrame.setResizable(false);
		
		// code and opcodes split pane
		JSplitPane p1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JTextArea codeArea = new JTextArea(48, 60);
		codeArea.setEditable(true);
		String[] opcodeColumn = {"Binary", "Hex"};
	    JTable opcodeTable = new JTable(opcodeData, opcodeColumn){  
	    	private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column){  
	          return false;
	        }
	    };
	    JScrollPane opcodeScrollPane = new JScrollPane(opcodeTable);
		JButton compileBtn = new JButton("Compile");
		compileBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// get input from codeArea and split into string array, error check
				codeLines = (codeArea.getText().toUpperCase()).split(System.getProperty("line.separator"));
				if(codeLines.length != 0) {
					if(opcodeConverter.errorCheck(codeLines)) {
						// error
						JOptionPane.showMessageDialog(null, "SYNTAX ERROR!");
					}
					else {
						// no error, get converted opcodeList
						opcodeList = opcodeConverter.opcodeConvert(codeLines);
						System.out.println(Arrays.toString(opcodeList.toArray()));
					}
				}
			}
		});
	    JPanel opcodePane = new JPanel();
	    opcodePane.setLayout(new BoxLayout(opcodePane, BoxLayout.Y_AXIS));		
	    opcodePane.add(compileBtn);
		opcodePane.add(opcodeScrollPane);
		p1.setLeftComponent(codeArea);
		p1.setRightComponent(opcodePane);
		
		// regs and data pane
		JPanel p2 = new JPanel();
		
		// pipeline pane
		JPanel p3 = new JPanel();
		JTabbedPane tabPane =new JTabbedPane();
		tabPane.addTab("Code & Opcodes", p1);
		tabPane.addTab("Regs & Data", p2);
		tabPane.addTab("Pipeline", p3);
		mainFrame.add(tabPane);
		mainFrame.setVisible(true);
		a.close();
	}
}
