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
	static OpcodeHandler opcodeHandler;
	static PipelineHandler pipelineHandler;
	static String[] registerColumns = {"Label", "Value"};
	static String[] memoryColumns = {"Address", "Value"};
	static String[] pipelineColumns = {"1"};
	static Object[][] data = {};
	
	public static void main(String[] args) {
		codeObject = new CodeObject();
		opcodeHandler = new OpcodeHandler();
		pipelineHandler = new PipelineHandler();
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
		tableModel = (DefaultTableModel) regTable.getModel();
		for(int i = 0; i < 32; i++) {
			tableModel.addRow(new Object[] {"R" + i, 0});
		}
		JScrollPane regScrollPane = new JScrollPane(regTable);
		tableModel = new DefaultTableModel(data, memoryColumns) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		JTable memoryTable = new JTable(tableModel);
		tableModel = (DefaultTableModel) memoryTable.getModel();
		for(int i = 0; i < 8192; i++) {
			tableModel.addRow(new Object[] {Integer.toHexString(i), 0});
		}
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
		fillInternalRegisters((DefaultTableModel) internalRegTable.getModel());
		JScrollPane internalRegScrollPane = new JScrollPane(internalRegTable);
		tableModel = new DefaultTableModel(data, pipelineColumns) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		JTable pipeTable = new JTable(tableModel);
		JToolBar executionToolBar = new JToolBar();
		executionToolBar.setFloatable(false);
		executionToolBar.setRollover(true);
		JButton singleStepEx = new JButton("Single-Step Ex");
		singleStepEx.addActionListener(new ActionListener() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(codeObject.getOpcodeList().isEmpty()) {
					// only works if there opcodes exist
					DefaultTableModel model = (DefaultTableModel) pipeTable.getModel();
					executePipeline(model, false);	
				}
			}
		});
		JButton fullEx = new JButton("Full Ex");
		singleStepEx.addActionListener(new ActionListener() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(codeObject.getOpcodeList().isEmpty()) {
					// only works if there opcodes exist
					DefaultTableModel model = (DefaultTableModel) pipeTable.getModel();
					executePipeline(model, true);	
				}
			}
		});
		executionToolBar.add(singleStepEx);
		executionToolBar.addSeparator();
		executionToolBar.add(fullEx);
		JPanel internalPipelinePane = new JPanel();
		internalPipelinePane.setLayout(new BoxLayout(internalPipelinePane, BoxLayout.Y_AXIS));
		internalPipelinePane.add(internalRegScrollPane);
		internalPipelinePane.add(executionToolBar);
		JScrollPane pipeScrollPane = new JScrollPane(pipeTable);
		pipelineSplitPane.setLeftComponent(internalPipelinePane);
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
					if(opcodeHandler.errorCheck(codeLines)) {
						// error
						JOptionPane.showMessageDialog(null, "SYNTAX ERROR!");
					}
					else {
						// no error, get converted opcodeList
						codeObject = opcodeHandler.opcodeConvert(codeLines);
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

	// cannot be done through for loop alone
	public static void fillInternalRegisters(DefaultTableModel model) {
		model.addRow(new Object[]{"IF/ID.IR", "N/A"});
		model.addRow(new Object[]{"IF/ID.PC", "N/A"});
		model.addRow(new Object[]{"ID/EX.IR", "N/A"});
		model.addRow(new Object[]{"ID/EX.A", "N/A"});
		model.addRow(new Object[]{"ID/EX.B", "N/A"});
		model.addRow(new Object[]{"ID/EX.IMM", "N/A"});
		model.addRow(new Object[]{"EX/MEM.IR", "N/A"});
		model.addRow(new Object[]{"EX/MEM.ALUOUTPUT", "N/A"});
		model.addRow(new Object[]{"EX/MEM.B", "N/A"});
		model.addRow(new Object[]{"EX/MEM.cond", "N/A"});
		model.addRow(new Object[]{"MEM/WB.IR", "N/A"});
		model.addRow(new Object[]{"MEM/WB.ALUOUTPUT", "N/A"});
		model.addRow(new Object[]{"MEM/WB.LMD", "N/A"});
		model.addRow(new Object[]{"MEM: Actual memory affected", "N/A"});
		model.addRow(new Object[]{"WB: Registers affected", "N/A"});
	}

	public static void executePipeline(DefaultTableModel model, boolean fullEx) {
		// while there unprocessed opcodes
		while(codeObject.getOpcodeList().size() != codeObject.getFinishedList().size()) {
			// working backwards to work around dependencies
			// WB
			
			// MEM
			// ID
			// IF
		}
	}
}
