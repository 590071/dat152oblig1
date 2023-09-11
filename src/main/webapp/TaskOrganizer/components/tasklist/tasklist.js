/**
 * @typedef Task
 * @property {number} id
 * @property {string} [title]
 * @property {string} status
 */

const template = document.createElement("template");
template.innerHTML = `
    <link rel="stylesheet" type="text/css" href="${import.meta.url.match(/.*\//)[0]}/tasklist.css"/>

    <div id="tasklist"></div>`;

const tasktable = document.createElement("template");
tasktable.innerHTML = `
    <table>
        <thead><tr><th>Task</th><th>Status</th></tr></thead>
        <tbody></tbody>
    </table>`;

const taskrow = document.createElement("template");
taskrow.innerHTML = `
    <tr>
        <td class="name"></td>
        <td class="status"></td>
        <td>
            <select>
                <option value="0" selected>&lt;Modify&gt;</option>
            </select>
        </td>
        <td><button type="button">Remove</button></td>
    </tr>`;

/**
 * TaskList
 * Manage view with list of tasks
 */
class TaskList extends HTMLElement {

    #changeCallback
    #deleteCallback
    /** @type {string[]} */
    #allstatuses = []
    #container
    #tbody
    #taskCount = 0

    constructor() {
        super();

        this.attachShadow({mode: "open"})
        this.#init()
        this.shadowRoot.appendChild(this.#container)
        this.#toggleTable()
    }

    #init() {
        const copy = template.content.cloneNode(true);
        this.#container = copy.getElementById("tasklist");
        const table = tasktable.content.cloneNode(true).querySelector("table");
        this.#tbody = table.querySelector("tbody");
        table.style.display = 'none'; // Initialize as hidden
        this.#container.appendChild(table);
    }

    #toggleTable() {
        const table = this.#container.querySelector("table");
        if (table) {
            if (this.getNumtasks() === 0) {
                table.style.display = 'none';
            } else {
                table.style.display = 'table';
            }
        }
    }

    /**
     * @public
     * @param {string[]} list with all possible task statuses
     */
    setStatuseslist(list) {
        this.#allstatuses = list
    }

    /**
     * Add callback to run on change of status of a task, i.e. on change in the SELECT element
     * @public
     * @param {function} callback
     */
    changestatusCallback(callback) {
        this.#changeCallback = callback
    }

    /**
     * Add callback to run on click on delete button of a task
     * @public
     * @param {function} callback
     */
    deletetaskCallback(callback) {
        this.#deleteCallback = callback
    }

    /**
     * Add task at top in list of tasks in the view
     * @public
     * @param {Task} task - Object representing a task
     */
    showTask(task) {
        this.#taskCount++

        /**@type {HTMLTableRowElement}*/
        const trTask = taskrow.content.cloneNode(true).querySelector("tr")

        trTask.dataset.id = task.id.toString()
        trTask.querySelector(".status").textContent = task.status
        trTask.querySelector(".name").textContent = task.title

        const select = trTask.querySelector("select")
        if (select) {
            const originalstatus = select.querySelector("option").value
            this.#allstatuses.forEach(value => {
                const option = document.createElement("option")
                option.textContent = value
                option.value = value
                select.appendChild(option)
            })
            select.addEventListener("change", () => {
                const newstatus = select.value
                if (newstatus !== originalstatus) {
                    if (this.#changeCallback && confirm(`set '${task.title}' to ${newstatus}`)) {
                        this.#changeCallback(task.id, newstatus)
                        trTask.querySelector(".status").textContent = newstatus
                    }
                }
                select.value = originalstatus
            })
        }

        const button = trTask.querySelector("button")
        if (button) {
            button.addEventListener("click", () => {
                if (this.#deleteCallback && confirm(`delete task '${task.title}'?`)) {
                    this.#deleteCallback(task.id)
                    this.removeTask(task.id)
                }
            })

        }


        this.#tbody.insertBefore(trTask, this.#tbody.firstChild)
        this.#toggleTable()
    }

    /**
     * Update the status of a task in the view
     * @param {Task} task - Object with attributes {'id':taskId,'status':newStatus}
     */
    updateTask(task) {
        const td = this.#getTask(task.id)?.querySelector(".status")
        if (td) {
            td.textContent = task.status
        }
    }

    /**
     * Remove a task from the view
     * @param {number} id - ID of task to remove
     */
    removeTask(id) {
        this.#taskCount--
        const tr = this.#getTask(id)
        if (tr) {
            tr.remove()
        }
        this.#toggleTable()
    }

    /**
     * @public
     * @return {number} - Number of tasks on display in view
     */
    getNumtasks() {
        return this.#taskCount
    }

    /**
     * @private
     * @param {number} id
     * @returns {HTMLTableRowElement}
     */
    #getTask(id) {
        return this.#container.querySelector(`table tbody tr[data-id="${id}"]`)
    }
}

customElements.define('task-list', TaskList);
