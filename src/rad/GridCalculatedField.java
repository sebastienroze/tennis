package rad;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class GridCalculatedField {
	public int col;
	public String title;
	public GridCalculatedField(int col, String title) {
		super();
		this.col = col;
		this.title = title;
	}
	public abstract String Calculate(ResultSet rs) throws SQLException;	
}
