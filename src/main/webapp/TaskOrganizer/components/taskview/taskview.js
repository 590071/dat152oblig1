import '../tasklist/tasklist.js';
import '../taskbox/taskbox.js';

const template = document.createElement("template");
template.innerHTML = `
    <link rel="stylesheet" type="text/css" href="${import.meta.url.match(/.*\//)[0]}/taskview.css"/>

    <h1>Tasks</h1>

    <div id="message"><p>Waiting for server data.</p></div>
    <div id="newtask"><button type="button" disabled>New task</button></div>

    <!-- The task list -->
    <task-list></task-list>
            
    <!-- The Modal -->
    <task-box></task-box>`;

/**
  * TaskView
  * The full application view
  */
class TaskView extends HTMLElement {

    #config = '../api/services'
    #taskList

    constructor() {
        super();

        this.attachShadow({mode: "open"})
        const copy = template.content.cloneNode(true)
        this.#taskList = copy.querySelector("task-list")
        this.shadowRoot.appendChild(copy)
        this.#fetchStatuses()
        this.#fetchTasks()

        this.shadowRoot.querySelector("#newtask button").disabled = false

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
    }

    async #fetchStatuses() {
        this.#taskList.setStatuseslist(await this.#fetch("/allstatuses", "allstatuses"))
    }
    async #fetchTasks() {
        const tasks = await this.#fetch("/tasklist", "tasks")
        tasks.forEach(task => this.#taskList.showTask(task))
        console.log(this.shadowRoot)
        this.shadowRoot.querySelector("#message p").textContent = `Found ${tasks.length} tasks.`

    }

    async #fetch(path, key) {
        const url = this.#config + path
        const res = await fetch(url)
        const data = await res.json()
        return data[key]
    }

}

customElements.define('task-view', TaskView);
