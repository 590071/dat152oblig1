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
    #config = this.dataset.serviceurl
    #taskList
    #dialog

    constructor() {
        super();


        this.attachShadow({mode: "open"})
        const copy = template.content.cloneNode(true)
        this.#taskList = copy.querySelector("task-list")
        this.#dialog = copy.querySelector("task-box")
        this.shadowRoot.appendChild(copy)

        if (!this.dataset.serviceurl) {
            const error = "data-serviceurl is a required prop"

            this.shadowRoot.querySelector("#message p").textContent = error
            throw new Error(error)
        }

        ;(async () => {
            await this.#fetchStatuses()
            await this.#fetchTasks()

            // Enable Button
            const button = this.shadowRoot.querySelector("#newtask button")
            button.disabled = false
            button.addEventListener("click", () => {
                this.#dialog.newtaskCallback(async (title, status) => {
                    const task = await this.#post(
                        "/task",
                        "task",
                        {title, status}
                    )

                    this.#taskList.showTask(task)
                })
                this.#dialog.show()
            })

            this.#taskList.changestatusCallback(
                async (id, newStatus) => {
                    await this.#put(
                        `/task/${id}`,
                        "id",
                        {status: newStatus}
                    )
                }
            )

            this.#taskList.deletetaskCallback(
                async (id) => {
                    await this.#delete(`/task/${id}`, id)
                }
            )
        })()
    }

    async #fetchStatuses() {
        const allStatuses = await this.#get("/allstatuses", "allstatuses")

        this.#taskList.setStatuseslist(allStatuses)
        this.#dialog.setStatuseslist(allStatuses)
    }

    async #fetchTasks() {
        const tasks = await this.#get("/tasklist", "tasks")
        tasks.forEach(task => this.#taskList.showTask(task))

        this.shadowRoot.querySelector("#message p").textContent = `Found ${tasks.length} tasks.`
    }

    async #get(path, key) {
        const data = await this.#fetch(path, "GET")

        return data[key]
    }

    async #delete(path, key) {
        const data = await this.#fetch(path, "DELETE")

        return data[key]
    }

    async #put(path, key, payload) {
        const data = await this.#fetch(path, "PUT", payload)

        return data[key]
    }

    async #post(path, key, payload) {
        const data = await this.#fetch(path, "POST", payload)

        return data[key]
    }

    async #fetch(path, method, payload = undefined) {
        const url = this.#config + path
        const res = await fetch(url, {
            method,
            headers: {"Content-Type": "application/json; charset=utf-8"},
            body: JSON.stringify(payload)
        })

        return await res.json()
    }
}

customElements.define('task-view', TaskView);
