package hvl.dat152.model;

public class Task {

	private Integer id;

	private String title;

	private Status status;

	public enum Status {
		WAITING, ACTIVE, DONE;
	}

	public Task() {
	}

	public Task(String title, Status status) {
		this.title = title;
		this.status = status;
	}

	public Task(Integer id, String title, Status status) {
		this.id = id;
		this.title = title;
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Todo [id=" + id + ", title=" + title + ", status=" + status + "]";
	}
}
