package hvl.dat152.dataaccess;

/*
 * Have replaced JPA with traditional JDBC.
 * Reason for not using JPA:
 * - I did not manage to run the TomEE server from IntelliJ if JPA with an embedded Derby database.
 * - With Eclipse the server will start, but requires some tweaking.
 * JDBC with an embedded Derby DB works without any problems.
 */

import java.util.List;

import hvl.dat152.exceptions.DAOException;
import hvl.dat152.model.Task;
import hvl.dat152.model.Task.Status;

public interface TaskDAO {
	
    public void setupDB();

	public List<Task> getTasks() throws DAOException;

	public void setStatus(Integer taskId, Status newStatus) throws DAOException;

	public Integer addTask(Task t) throws DAOException;
	
	public void removeTask(Integer taskId) throws DAOException;
}
