package hvl.dat152.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import hvl.dat152.model.Task.Status;

@XmlRootElement
public class TaskStatus {
	private Status status;

	public TaskStatus() {
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
