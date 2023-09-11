class TaskDemo {
    #taskList

    constructor() {
        this.#taskList = document.querySelector("task-list");

        this.#taskList.changestatusCallback(
            (id, newStatus) => {
                console.log(`Status ${newStatus} for task ${id} approved`)
            }
        )

        this.#taskList.deletetaskCallback(
            (id) => {
                console.log(`Delete of task ${id} approved`)
            }
        )

        const allStatuses = ["Waiting", "Active", "Done"]
        this.#taskList.setStatuseslist(allStatuses);

        const tasks = [
            {id: 1, status: "Waiting", title: "Paint roof"},
            {id: 2, status: "Active", title: "Wash windows"},
            {id: 3, status: "Done", title: "Wash floor"}
        ]

        tasks.forEach(t => this.#taskList.showTask(t))
    }

    newTask(id, title, status) {
        this.#taskList.showTask({id, title, status})
    }

    changeStatus(id, status) {
        this.#taskList.updateTask({id, status})
    }

    removeTask(id) {
        this.#taskList.removeTask(id)
    }
}

const demo = new TaskDemo()
demo.changeStatus(1, "Active")
demo.newTask(5, "Do DAT152 home work", "Active")
demo.removeTask(1)
