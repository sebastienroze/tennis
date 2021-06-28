package rad;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class GridRAD {
	private KeySelectListener rowSelect = null;
	private ActionListener mouseDoubleClick = null;
	private JScrollPane scrollPane;
	private int hiddenFieldsCount;
	private GridCalculatedField[] calculatedFields;
	private DefaultTableModel model;
	private ArrayList<Long> keyValues;
	private Connection conn;
	private String sql;
	private JTable table;
	private String KeyField;
	private boolean noSelection;
	private GridSearch gridSearch = null;

	public GridRAD(Connection conn, String KeyField, String sql, int hiddenFieldsCount,
			GridCalculatedField[] calculatedFields) {
		super();
		this.conn = conn;
		this.sql = sql;
		this.hiddenFieldsCount = hiddenFieldsCount;
		this.calculatedFields = calculatedFields;
		this.KeyField = KeyField;
		// System.out.println(sql);
		noSelection = false;
		keyValues = new ArrayList<Long>();
		table = new JTable();
		table.setAutoCreateRowSorter(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			model = new javax.swing.table.DefaultTableModel(new String[][] {}, getColumnsLabel(rs)) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			table.setModel(model);
			while (rs.next()) {
				model.addRow(getRow(rs));
				keyValues.add(rs.getLong(KeyField));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		scrollPane = new JScrollPane(table);
	}

	public void filter(String sqlFilter) {
		noSelection = true;
		model.setRowCount(0);
		keyValues.clear();
		if (!sqlFilter.equals(""))
			sqlFilter = " WHERE " + sqlFilter;
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(sql + sqlFilter);
			while (rs.next()) {
				model.addRow(getRow(rs));
				keyValues.add(rs.getLong(KeyField));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		noSelection = false;
	}

	public void select(long keyValue) {
		noSelection = true;
		int rowIndex = keyValues.indexOf(keyValue);
		table.clearSelection();
		if (rowIndex >= 0) {
			rowIndex = table.convertRowIndexToView(rowIndex);
			table.addRowSelectionInterval(rowIndex, rowIndex);
		}
		noSelection = false;
	}

	public void refreshKey(long keyValue) {
		Statement statement;
		noSelection = true;
		int rowIndex = keyValues.indexOf(keyValue);
		try {
			statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(sql + " WHERE " + KeyField + "=" + keyValue);
//			System.out.println(sql + " WHERE " + KeyField + "=" + keyValue);
//			System.out.println(rowIndex);			
			if (rs.next()) {
				if (rowIndex >= 0) {
					model.removeRow(rowIndex);
					model.insertRow(rowIndex, getRow(rs));
					rowIndex = table.convertRowIndexToView(rowIndex);
					table.addRowSelectionInterval(rowIndex, rowIndex);
				} else {
					model.addRow(getRow(rs));
					keyValues.add(rs.getLong(KeyField));
					table.clearSelection();
					rowIndex = table.getRowCount() - 1;
					rowIndex = table.convertRowIndexToView(rowIndex);					
					table.addRowSelectionInterval(rowIndex, rowIndex);
					scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
						}
					});
				}
			} else {
				if (rowIndex >= 0) {
					model.removeRow(rowIndex);
					keyValues.remove(rowIndex);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		noSelection = false;
	}

	private String[] getColumnsLabel(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int nbCalcFieldCount = 0;
		if (calculatedFields != null) {
			nbCalcFieldCount = calculatedFields.length;
		}
		String[] columnsLabel = new String[rsmd.getColumnCount() - hiddenFieldsCount + nbCalcFieldCount];
		int nbCalcField = 0;
		for (int i = 0; i < rsmd.getColumnCount() - hiddenFieldsCount + nbCalcFieldCount; i++) {
			boolean calculated = false;
			if (nbCalcField < nbCalcFieldCount) {
				if (calculatedFields[nbCalcField].col == i) {
					calculated = true;
					columnsLabel[i] = calculatedFields[nbCalcField].title;
					nbCalcField++;
				}
			}
			if (!calculated)
				columnsLabel[i] = rsmd.getColumnLabel(i + 1 - nbCalcField + hiddenFieldsCount);
		}
		return columnsLabel;
	}

	private Object[] getRow(ResultSet rs) throws SQLException {
		int colCount = rs.getMetaData().getColumnCount();
		int nbCalcFieldCount = 0;
		if (calculatedFields != null) {
			nbCalcFieldCount = calculatedFields.length;
		}
		int nbCalcField = 0;
		Object[] row = new Object[colCount - hiddenFieldsCount + nbCalcFieldCount];
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 0; i < rsmd.getColumnCount() - hiddenFieldsCount + nbCalcFieldCount; i++) {
			boolean calculated = false;
			if (nbCalcField < nbCalcFieldCount) {
				if (calculatedFields[nbCalcField].col == i) {
					calculated = true;
					row[i] = calculatedFields[nbCalcField].Calculate(rs);
					nbCalcField++;
				}
			}
			if (!calculated)
				row[i] = rs.getString(i + 1 - nbCalcField + hiddenFieldsCount);
		}
		return row;
	}

	public void addDoubleClicListener(ActionListener al) {
		mouseDoubleClick = al;
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
					GridRAD.this.mouseDoubleClick.actionPerformed(null);
				}
			}
		});
	}

	public void addRowSelectListener(KeySelectListener rsl) {
		rowSelect = rsl;
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row =GridRAD.this.table.getSelectedRow(); 
				if (!GridRAD.this.noSelection && (row>=0) && !e.getValueIsAdjusting()) {
					row = GridRAD.this.table.convertRowIndexToModel(row);
					long id = keyValues.get(row);					
					GridRAD.this.rowSelect.KeySelected(id);
				}
			}
		});
	}

	public Component getComponent() {
		if (gridSearch == null) {
			return scrollPane;
		} else {
			JPanel jpGrille = new JPanel();
			jpGrille.setLayout(new BorderLayout());
			jpGrille.add(scrollPane, BorderLayout.CENTER);
			jpGrille.add(gridSearch.getPanel(), BorderLayout.NORTH);
			return jpGrille;
		}
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public JPanel getSearchPane() {
		if (gridSearch != null) {
			return gridSearch.getPanel();
		} else
			return null;
	}

	public void setGridSearch(GridSearch gs) {
		gridSearch = gs;
		gs.grille = this;
	}
}
