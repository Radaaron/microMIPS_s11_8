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
	static String[] pipelineColumns = {"Instruction"};
	static Object[][] data = {};
	static int pipelineCycle = 0;
	
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
			tableModel.addRow(new Object[] {"R" + i, "0000000000000000"});
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
			tableModel.addRow(new Object[] {Integer.toHexString(i).toUpperCase(), "00"});
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
				if(!codeObject.getOpcodeList().isEmpty()) {
					// only works if there opcodes exist
					// get table models
					DefaultTableModel pipeModel = (DefaultTableModel) pipeTable.getModel();
					DefaultTableModel internalRegModel = (DefaultTableModel) internalRegTable.getModel();
					DefaultTableModel regModel = (DefaultTableModel) regTable.getModel();
					DefaultTableModel memoryModel = (DefaultTableModel) memoryTable.getModel();
					executePipeline(pipeModel, internalRegModel, regModel, memoryModel, true);	
				}
			}
		});
		JButton fullEx = new JButton("Full Ex");
		fullEx.addActionListener(new ActionListener() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!codeObject.getOpcodeList().isEmpty()) {
					// only works if there opcodes exist
					// get table models
					DefaultTableModel pipeModel = (DefaultTableModel) pipeTable.getModel();
					DefaultTableModel internalRegModel = (DefaultTableModel) internalRegTable.getModel();
					DefaultTableModel regModel = (DefaultTableModel) regTable.getModel();
					DefaultTableModel memoryModel = (DefaultTableModel) memoryTable.getModel();
					executePipeline(pipeModel, internalRegModel, regModel, memoryModel, false);		
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
						// also populate program part of memory
						DefaultTableModel tableModel = (DefaultTableModel) memoryTable.getModel();
						for(int i = 4096; i < codeObject.getMemory().length; i++) {
							tableModel.setValueAt(codeObject.getMemoryValue(i), i, 1);
						}
						// and pipeline map
						tableModel = (DefaultTableModel) pipeTable.getModel();
						tableModel.setNumRows(0);
						for(int i = 0; i < codeLines.length; i++) {
							tableModel.addRow(new Object[]{codeLines[i]});
						}
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
		model.addRow(new Object[]{"IF/ID.IR", "N/A"}); 						// 0
		model.addRow(new Object[]{"IF/ID.PC", "N/A"}); 						// 1
		model.addRow(new Object[]{"ID/EX.IR", "N/A"}); 						// 2
		// hidden: ID.NPC														// 15
		model.addRow(new Object[]{"ID/EX.A", "N/A"}); 						// 3
		model.addRow(new Object[]{"ID/EX.B", "N/A"}); 						// 4
		model.addRow(new Object[]{"ID/EX.IMM", "N/A"}); 					// 5
		model.addRow(new Object[]{"EX/MEM.IR", "N/A"}); 					// 6
		// hidden: EX.NPC														// 16
		model.addRow(new Object[]{"EX/MEM.ALUOUTPUT", "N/A"}); 				// 7
		model.addRow(new Object[]{"EX/MEM.B", "N/A"}); 						// 8
		model.addRow(new Object[]{"EX/MEM.cond", "N/A"}); 					// 9
		model.addRow(new Object[]{"MEM/WB.IR", "N/A"}); 					// 10
		// hidden: MEM.NPC														// 17
		model.addRow(new Object[]{"MEM/WB.ALUOUTPUT", "N/A"}); 				// 11
		model.addRow(new Object[]{"MEM/WB.LMD", "N/A"}); 					// 12
		model.addRow(new Object[]{"MEM: Actual memory affected", "N/A"}); 	// 13
		// hidden: WB.NPC														// 18
		model.addRow(new Object[]{"WB: Registers affected", "N/A"}); 		// 14
	}

	public static void executePipeline(DefaultTableModel pipeModel, DefaultTableModel internalRegModel, DefaultTableModel regModel, DefaultTableModel memoryModel, boolean singleStep) {
		while(!codeObject.isFinished()) {
			// process backwards to work around dependencies
			// WB
			codeObject = pipelineHandler.WBstage(codeObject);
			// MEM
			codeObject = pipelineHandler.MEMstage(codeObject);	
			// EX
			codeObject = pipelineHandler.EXstage(codeObject);
			// ID
			codeObject = pipelineHandler.IDstage(codeObject);
			// IF
			codeObject = pipelineHandler.IFstage(codeObject);
			// update tables
			// pipeline map
			pipelineCycle++;
			pipeModel.addColumn(pipelineCycle, pipelineHandler.getCycleInfo(codeObject));
			// internal pipeline registers
			for(int i = 0; i < 15; i++) {
				internalRegModel.setValueAt(codeObject.getPipelineRegisterValue(i), i, 1);
			}
			// registers
			for(int i = 0; i < codeObject.getRegisters().length; i++) {
				regModel.setValueAt(codeObject.getRegisterValue(i), i, 1);
			}
			// memory
			for(int i = 0; i < codeObject.getMemory().length; i++) {
				memoryModel.setValueAt(codeObject.getMemoryValue(i), i, 1);
			}
			if(singleStep) {
				break;
			}
		}
	}
	
	public static void printPipeline(CodeObject codeObject) {
		for (Object name : codeObject.getPipelineRegisters()) {
			System.out.println(name);
		}
		System.out.println("---------------------");
	}
}
