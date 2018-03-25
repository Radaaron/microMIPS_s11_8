package microMIPS_s11_8;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Driver {
	// main and gui class
	static String[] codeLines;
	static CodeObject codeObject;
	static OpcodeConverter opcodeConverter;
	static String[] registerColumns = {"Label", "Value"};
	static String[] memoryColumns = {"Address", "Value"};
	static String[] pipelineColumns = {"1", "2", "3"};
	static Object[][] data = {{},{}};
	
	public static void main(String[] args) {
		codeObject = new CodeObject();
		opcodeConverter = new OpcodeConverter();
		Scanner a = new Scanner(System.in);
		JFrame mainFrame = new JFrame();
		mainFrame.setSize(new Dimension(1400, 800));
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setTitle("microMIPS");
		mainFrame.setResizable(false);
		
		// regs and data splitpane
		JSplitPane RegDataSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		DefaultTableModel tableModel = new DefaultTableModel(data, registerColumns) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		JTable regTable = new JTable(tableModel);
		JScrollPane regScrollPane = new JScrollPane(regTable);
		tableModel = new DefaultTableModel(data, memoryColumns) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		JTable memoryTable = new JTable(tableModel);
		JScrollPane memoryScrollPane = new JScrollPane(memoryTable);
		RegDataSplitPane.setLeftComponent(regScrollPane);
		RegDataSplitPane.setRightComponent(memoryScrollPane);
		
		// pipeline splitpane
		JSplitPane pipelineSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		tableModel = new DefaultTableModel(data, registerColumns) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		JTable internalRegTable = new JTable(tableModel);
		JScrollPane internalRegScrollPane = new JScrollPane(internalRegTable);
		tableModel = new DefaultTableModel(data, pipelineColumns) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		JTable pipeTable = new JTable(tableModel);
		JScrollPane pipeScrollPane = new JScrollPane(pipeTable);
		pipelineSplitPane.setLeftComponent(internalRegScrollPane);
		pipelineSplitPane.setRightComponent(pipeScrollPane);
		
		// code and opcodes split pane
		JSplitPane codeOpcodeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JTextArea codeArea = new JTextArea(48, 60);
		codeArea.setEditable(true);
		JList opcodeJList = new JList(); //data has type Object[]
		opcodeJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		opcodeJList.setLayoutOrientation(JList.VERTICAL);
		opcodeJList.setVisibleRowCount(-1);
	    JScrollPane opcodeScrollPane = new JScrollPane(opcodeJList);
		JButton compileBtn = new JButton("Compile");
		compileBtn.addActionListener(new ActionListener() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
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
						codeObject = opcodeConverter.opcodeConvert(codeLines);
						DefaultListModel listModel = new DefaultListModel();
						for(int i = 0; i < codeObject.getOpcodeList().size(); i++) {
							listModel.addElement(codeObject.getOpcodeList().get(i));
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
		codeOpcodeSplitPane.setLeftComponent(codeArea);
		codeOpcodeSplitPane.setRightComponent(opcodePane);
		
		// construction
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab("Code & Opcodes", codeOpcodeSplitPane);
		tabPane.addTab("Pipeline", pipelineSplitPane);
		tabPane.addTab("Regs & Data", RegDataSplitPane);
		mainFrame.add(tabPane);
		mainFrame.setVisible(true);
		a.close();
	}
}
