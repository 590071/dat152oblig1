package hvl.dat152.dataaccess.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import hvl.dat152.dataaccess.TaskDAO;
import hvl.dat152.exceptions.DAOException;
import hvl.dat152.model.Task;
import hvl.dat152.model.Task.Status;

public class TaskDAOImpl implements TaskDAO {

	public List<Task> getTasks() throws DAOException {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		Exception daoE = null;
		List<Task> taskList = new ArrayList<Task>();

		try {
			conn = DatabaseHelper.getConnection("java:comp/env/jdbc/DAT152_TaskDB");
			conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			st = conn.createStatement();

			String SQL = "SELECT id,title,status FROM WorkOrganizer.Task";
			rs = st.executeQuery(SQL);

			while (rs.next()) {
				Task task = new Task();
				task.setId(rs.getInt("ID"));
				task.setTitle(rs.getString("title"));
				task.setStatus(Status.valueOf(rs.getString("status")));
				taskList.add(task);
			}

		} catch (Exception e) {
			daoE = e;
		} finally {
			DatabaseHelper.r(conn);
			DatabaseHelper.close(st);
			DatabaseHelper.close(rs);
			DatabaseHelper.close(conn);
			if (daoE != null)
				throw new DAOException(daoE);
		}

		return taskList;
	}

	public void setStatus(Integer taskId, Status newStatus) throws DAOException {
		Connection conn = null;
		PreparedStatement ps = null;
		Exception daoE = null;

		try {
			if (taskId == null)
				throw new DAOException("Wrong task id");
			if (taskId < 1)
				throw new DAOException("Wrong task id");
			conn = DatabaseHelper.getConnection("java:comp/env/jdbc/DAT152_TaskDB");
			conn.setAutoCommit(false);
			String SQL = "UPDATE WorkOrganizer.Task set status=? WHERE id=?";
			ps = conn.prepareStatement(SQL);
			ps.setString(1, newStatus.name());
			ps.setInt(2, taskId);
			int count = ps.executeUpdate();
			if (count != 1) {
				daoE = new DAOException("Can not update status of task");
			}
			conn.commit();
		} catch (Exception e) {
			daoE = e;
		} finally {
			DatabaseHelper.r(conn);
			DatabaseHelper.close(ps);
			DatabaseHelper.close(conn);
			if (daoE != null)
				throw new DAOException(daoE);
		}
	}

	public Integer addTask(Task t) throws DAOException {
		Connection conn = null;
		PreparedStatement ps = null;
		Exception daoE = null;
        ResultSet rs = null;
        Integer taskId = null;

		try {
			conn = DatabaseHelper.getConnection("java:comp/env/jdbc/DAT152_TaskDB");
			conn.setAutoCommit(false);

			String SQL = "INSERT INTO WorkOrganizer.Task (title,status) VALUES (?,?)";
			ps = conn.prepareStatement(SQL, new String[] { "ID" });
			ps.setString(1, t.getTitle());
			ps.setString(2, t.getStatus().name());
			int count = ps.executeUpdate();
			if (count != 1) {
				daoE = new DAOException("Can not add task");
			}
			
			rs = ps.getGeneratedKeys();
            //  st = conn.createStatement();
            //  rs = st.executeQuery("SELECT LAST_INSERT_ID() AS memberId");
            if (! rs.next()) throw new DAOException("Something is wrong with the DB");
            taskId = rs.getInt(1);
			conn.commit();
		} catch (Exception e) {
			daoE = e;
		} finally {
			DatabaseHelper.r(conn);
			DatabaseHelper.close(ps);
			DatabaseHelper.close(rs);
			DatabaseHelper.close(conn);
			if (daoE != null)
				throw new DAOException(daoE);
		}
		return taskId;
	}

	public void removeTask(Integer taskId) throws DAOException {
		Connection conn = null;
		PreparedStatement ps = null;
		Exception daoE = null;

		try {
			conn = DatabaseHelper.getConnection("java:comp/env/jdbc/DAT152_TaskDB");
			conn.setAutoCommit(false);

			String SQL = "DELETE FROM WorkOrganizer.Task WHERE id=?";
			ps = conn.prepareStatement(SQL);
			ps.setInt(1, taskId);
			int count = ps.executeUpdate();
			if (count != 1) {
				daoE = new DAOException("Can not delete task");
			}
			conn.commit();
		} catch (Exception e) {
			daoE = e;
		} finally {
			DatabaseHelper.r(conn);
			DatabaseHelper.close(ps);
			DatabaseHelper.close(conn);
			if (daoE != null)
				throw new DAOException(daoE);
		}
	}

	public void setupDB() {
		try (Connection conn = DatabaseHelper.getConnection("java:comp/env/jdbc/DAT152_TaskDB");
				Statement st = conn.createStatement();) {
			try {
				st.executeUpdate("create schema WorkOrganizer");
				System.out.println("Schema WorkOrganizer was created.");
			} catch (Exception e) {
				System.out.println(e.toString());
			}

			try {
				st.executeUpdate("create table WorkOrganizer.Task ("
						+ "id SMALLINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
						+ "title VARCHAR(255) NOT NULL," + "status VARCHAR(7)," + "PRIMARY KEY (id))");
				System.out.println("Table WorkOrganizer.Task was created.");

				System.out.println("Adding task 'Paint roof'");
				st.executeUpdate("INSERT INTO WorkOrganizer.Task (title,status) VALUES ('Paint roof','WAITING')");

				System.out.println("Adding task 'Wash windows'");
				st.executeUpdate("INSERT INTO WorkOrganizer.Task (title,status) VALUES ('Wash windows','ACTIVE')");

				System.out.println("Adding task 'Wash floor'");
				st.executeUpdate("INSERT INTO WorkOrganizer.Task (title,status) VALUES ('Wash floor','DONE')");
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
