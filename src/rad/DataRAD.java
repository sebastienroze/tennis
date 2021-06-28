package rad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class DataRAD {
	private String tableName;
	private Connection conn = null;
	private Statement statement = null;
	private String keyField;
	private long keyValue;
	private ResultSet rs = null;
	private ResultSetMetaData md = null;
	private HashMap<String, DataFieldRAD> fields;
	private HashMap<String, DataRAD> links;
	private KeySelectListener afterChange = null;

	public DataRAD(Connection conn, String tableName, String keyField) {
		super();
		this.tableName = tableName;
		this.keyField = keyField;
		this.fields = new HashMap<String, DataFieldRAD>();
		this.links = new HashMap<String, DataRAD>();
		this.conn = conn;
		this.keyValue = -1;
		try {
			this.statement = conn.createStatement();
			rs = statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + keyField + " IS NULL");
			md = rs.getMetaData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JTextField createTextField(String nomChamp) {
		JTextField txtField = null;
		try {
			int limitSize = md.getColumnDisplaySize(colIndex(nomChamp));
			txtField = new JTextField(limitSize);
			txtField.setDocument(new JTextFieldLimit(limitSize));
			fields.put(nomChamp, new TextFieldRAD(txtField));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return txtField;
	}
	
	public void createDataField(String nomChamp,DataFieldRAD dataField) {
		fields.put(nomChamp,dataField);
	}

	public JPanel createRadioGroupField(String nomChamp, String[] values, String[] labels) {
		RadioGroupField rgf = new RadioGroupField();
		for (int i = 0; i < values.length; i++) {
			JRadioButton rb = new JRadioButton(labels[i]);
			rgf.AddRadioButton(values[i], rb);
		}
		fields.put(nomChamp, rgf);
		return rgf;
	}

	public JComboBox<String> createSQLComboBoxField(String nomChamp, String sql) {
		JComboBox<String> cb = new JComboBox<String>();
		SQLComboBoxFieldRAD cbf = new SQLComboBoxFieldRAD(cb);
		try {
			ResultSet rsql = conn.createStatement().executeQuery(sql);
			int comboIndex = 0;
			while (rsql.next()) {
				cb.addItem(rsql.getString(2));
				cbf.AddValue(rsql.getString(1), comboIndex++);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fields.put(nomChamp, cbf);
		return cb;
	}

	public void createLink(String nomChamp, DataRAD dr) {
		links.put(nomChamp, dr);
	}

	public JButton createListSelectorField(String nomChamp, JFrame owner, String title,GridBuilderListener gbl) {
		ListSelectorField lsf = new ListSelectorField(owner, title);
		lsf.addSelectListener(value -> {
			DataRAD.this.readLink(nomChamp);
		});
		lsf.addGridBuilderListener(gbl);
		fields.put(nomChamp,lsf);
		return lsf.btSelect;
	}
	
	public void read() {
		read(this.keyValue);
	}

	public void delete() throws SQLException {
		delete(this.keyValue);
	}

	public void delete(long keyValue) throws SQLException {
			PreparedStatement ps = conn
					.prepareStatement("DELETE FROM " + tableName + " WHERE " + keyField + " = " + keyValue);
			ps.executeUpdate();
		doAfterChange();
	}

	public void readLink(String linkField,Long KeyValue) {
		links.get(linkField).read(KeyValue);
	}
	public void readLink(String linkField) {
		readLink(linkField, Long.parseLong(fields.get(linkField).getValue()));
//		System.out.println("Read link "+linkField);
	}	
	
	public boolean read(long keyValue) {
		this.keyValue = keyValue;
		try {
			rs = statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + keyField + " = " + keyValue);
			if (rs.next()) {
				for (Map.Entry<String, DataFieldRAD> entry : fields.entrySet()) {
					entry.getValue().setValue(rs.getString(entry.getKey()));
				}
				for (Map.Entry<String, DataRAD> entry : links.entrySet()) {
					entry.getValue().read(rs.getInt(entry.getKey()));
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;		
	}

	public void clear() {
		this.keyValue = -1;
		for (Map.Entry<String, DataFieldRAD> entry : fields.entrySet()) {
			entry.getValue().setValue("");
		}
		for (Map.Entry<String, DataRAD> entry : links.entrySet()) {
			entry.getValue().clear();
		}
	}

	public boolean update() throws SQLException {
		int nbUpdate = 0;
			StringBuilder sqlUpdate = null;

			for (Map.Entry<String, DataFieldRAD> entry : fields.entrySet()) {
				if (sqlUpdate == null) {
					sqlUpdate = new StringBuilder("UPDATE " + tableName + " SET ");
				} else {
					sqlUpdate.append(",");
				}
				sqlUpdate.append(entry.getKey() + "=?");
			}
			sqlUpdate.append(" WHERE ID=?");
			PreparedStatement ps = conn.prepareStatement(sqlUpdate.toString());
			int i = 1;
			for (Map.Entry<String, DataFieldRAD> entry : fields.entrySet()) {
				String value = entry.getValue().getValue();
				if (value.equals("")) {
					ps.setNull(i++, java.sql.Types.NULL); 
				}
				else {
					ps.setString(i++,value);
				}
			}
			ps.setLong(i, keyValue);
			nbUpdate = ps.executeUpdate();
		if (nbUpdate>0) {
			doAfterChange();		
			return true;
		}
		else {
			return false;
		}
	}

	public void create() throws SQLException {
			StringBuilder sqlInsert = null;
			StringBuilder sqlInsertVal = null;
			for (Map.Entry<String, DataFieldRAD> entry : fields.entrySet()) {
				if (sqlInsert == null) {
					sqlInsert = new StringBuilder("INSERT INTO " + tableName + " (");
					sqlInsertVal = new StringBuilder(") VALUES(");
				} else {
					sqlInsert.append(",");
					sqlInsertVal.append(",");
				}
				sqlInsert.append(entry.getKey());
				sqlInsertVal.append("?");
			}
			sqlInsert.append(sqlInsertVal + ")");
			PreparedStatement ps = conn.prepareStatement(sqlInsert.toString(), Statement.RETURN_GENERATED_KEYS);
			int i = 1;
			for (Map.Entry<String, DataFieldRAD> entry : fields.entrySet()) {
				ps.setString(i++, entry.getValue().getValue());
			}
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			keyValue = rs.getLong(1);
			doAfterChange();
	}

	public void setReadOnly(boolean readonly) {
		for (Map.Entry<String, DataFieldRAD> entry : fields.entrySet()) {
			entry.getValue().setReadOnly(readonly);
		}
	}

	public long getKeyValue() {
		return keyValue;
	}

	private int colIndex(String nomChamp) throws SQLException {
		int count = md.getColumnCount();
		for (int i = 1; i <= count; i++) {
			if (md.getColumnName(i).equals(nomChamp)) {
				return i;
			}
		}
		return 0;
	}
	
	public void doAfterChange() {
		if (afterChange != null) {
			afterChange.KeySelected(keyValue);
		}
	}

	public void addAfterChangeListener(KeySelectListener ksl) {
		afterChange = ksl;
	}
	
}
