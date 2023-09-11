package hvl.dat152.services;

import java.util.ArrayList;
import java.util.List;

import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import hvl.dat152.dataaccess.TaskDAO;
import hvl.dat152.dataaccess.jdbc.TaskDAOImpl;
import hvl.dat152.model.ResponseAddTask;
import hvl.dat152.model.ResponseDeleteTask;
import hvl.dat152.model.ResponseGetAllstatuses;
import hvl.dat152.model.ResponseGetTasks;
import hvl.dat152.model.ResponsePutTaskStatus;
import hvl.dat152.model.Task;
import hvl.dat152.model.TaskStatus;
import hvl.dat152.model.Task.Status;

/**
 * @author bki
 *
 */
@Singleton
@Path("/services")
public class TaskWebServices {
	@Lock(LockType.READ)
	@Path("/tasklist")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseGetTasks getTasks() {
		ResponseGetTasks response = new ResponseGetTasks();

		try {
			TaskDAO taskDAO = new TaskDAOImpl();
			List<Task> tasks = taskDAO.getTasks();
			response.setTasks(tasks);
			response.setResponseStatus(true);
		} catch (Exception e) {
			System.out.println(e);
		}
		return response;
	}
	
	@Lock(LockType.READ)
	@Path("/allstatuses")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseGetAllstatuses getStatuses() {
		List<Status> allstatuses = new ArrayList<Status>();
		for (Status statuses : Status.values()) {
			allstatuses.add(statuses);
		}
		ResponseGetAllstatuses response = new ResponseGetAllstatuses();
		response.setAllstatuses(allstatuses);
		response.setResponseStatus(true);
		return response;
	}

	// @PathParam("id") Integer id,
	@Lock(LockType.WRITE)
	@Path("/task/{id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	// public ServerResponse UpdatedTaskStatuses(@Context UriInfo
	// ui,@PathParam(taskId") Integer taskId, TaskStatus taskStatus) {
	public ResponsePutTaskStatus UpdateTaskStatus(@PathParam("id") Integer taskId, TaskStatus status) {
		ResponsePutTaskStatus response = new ResponsePutTaskStatus();
		try {
			if ((taskId != null) && (status != null)) {
				TaskDAO taskDAO = new TaskDAOImpl();
				taskDAO.setStatus(taskId, status.getStatus());

				response.setId(taskId);
				response.setStatus(status.getStatus());
				response.setResponseStatus(true);
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return response;
	}

	@Lock(LockType.WRITE)
	@Path("/task")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ResponseAddTask AddTask(Task task) {
		Integer taskId = null;
		ResponseAddTask response = new ResponseAddTask();
		try {
			if (task != null) {
				TaskDAO taskDAO = new TaskDAOImpl();
				taskId = taskDAO.addTask(task);
				task.setId(taskId);

				response.setTask(task);
				response.setResponseStatus(true);
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return response;
	}

	@Lock(LockType.WRITE)
	@Path("/task/{id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseDeleteTask deleteTask(@PathParam("id") Integer id) {
		ResponseDeleteTask deleteResponse = new ResponseDeleteTask();
		try {
			if (id != null) {
				if (id >= 1) {
					TaskDAO taskDAO = new TaskDAOImpl();
					taskDAO.removeTask(id);
					deleteResponse.setId(id);
					deleteResponse.setResponseStatus(true);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return deleteResponse;
	}
}