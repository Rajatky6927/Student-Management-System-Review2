import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * Utility class to build a DefaultTableModel from a ResultSet.
 * Used for displaying SQL query results in JTable.
 */
public class Table {

    /**
     * Builds a table model from the given ResultSet.
     *
     * @param rs The ResultSet containing query results.
     * @return A DefaultTableModel to be used with JTable.
     * @throws SQLException if a database access error occurs.
     */
    public DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Extract column names
        int columnCount = metaData.getColumnCount();
        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnLabel(i));  // better for aliases
        }

        // Extract row data
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames) {
            // Make cells non-editable for safety
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
