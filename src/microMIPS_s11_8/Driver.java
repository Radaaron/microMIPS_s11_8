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
		JList opcodeJList = new JList(); //data has type Object[]
		opcodeJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		opcodeJList.setLayoutOrientation(JList.VERTICAL);
		opcodeJList.setVisibleRowCount(-1);
	    JScrollPane opcodeScrollPane = new JScrollPane(opcodeJList);
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
						DefaultListModel listModel = new DefaultListModel();
						for(int i = 0; i < opcodeList.size(); i++) {
							listModel.addElement(opcodeList.get(i));
						}
						opcodeJList.setModel(listModel);
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
